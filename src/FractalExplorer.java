import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

public class FractalExplorer {
    /** Целое число «размер экрана», которое является шириной и высотой
     отображения в пикселях **/
    private int size;

    /** Ссылка JImageDisplay, для обновления отображения в разных
     методах в процессе вычисления фрактала **/
    private JImageDisplay jDisplay;

    /** Будет использоваться ссылка на базовый
     класс для отображения других видов фракталов в будущем **/
    private FractalGenerator fractal;

    /** Объект Rectangle2D.Double, указывающий диапазона комплексной
     плоскости, которая выводится на экран **/
    private Rectangle2D.Double range;

    public FractalExplorer(int display_size) {
        size = display_size;
        range = new Rectangle2D.Double();
        fractal = new Mandelbrot();
        fractal.getInitialRange(range);
        jDisplay = new JImageDisplay(display_size, display_size);
    }

    /** метод createAndShowGUI (), инициализирует
     графический интерфейс Swing: JFrame, содержащий объект JImageDisplay, и
     кнопку для сброса отображения **/
    public void createAndShowGUI () {
        //создание окна
        JFrame frame = new JFrame("Fractal Explorer");

        //добавление изображения в центр окна
        jDisplay.setLayout(new BorderLayout());
        frame.add(jDisplay, BorderLayout.CENTER);

        //создание кнопок сохранения и сброса изображения
        JButton saveImage = new JButton("Save Image");
        JButton resetDisplay = new JButton("Reset Display");

        //добавление панели кнопок сохранения и сброса в нижнюю часть дисплея
        JPanel panelButton = new JPanel();
        panelButton.add(saveImage);
        panelButton.add(resetDisplay);
        frame.add(panelButton, BorderLayout.SOUTH);

        //добавление реакции на нажатие кнопки сброса
        InActionListener clearAction = new InActionListener();
        resetDisplay.addActionListener(clearAction);

        //добавление реакции на нажатие дисплея
        InMouseListener mouseListener = new InMouseListener();
        jDisplay.addMouseListener(mouseListener);

        //добавление выхода из окна по умолчанию
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

        //создание таблицы фракталов и панели выбора одного из представленных
        String[] names = {"Mandelbrot", "Tricorn", "Burning Ship"};
        JComboBox comboBox = new JComboBox(names);
        JLabel label = new JLabel("Fractal: ");
        JPanel panelBox = new JPanel();

        //добавление панели фракталов в верхнюю часть окна
        panelBox.add(label);
        panelBox.add(comboBox);
        frame.add(panelBox, BorderLayout.NORTH);

        //добавление реакции на выбор фрактала
        ChooseButtonHandler chooseAction = new ChooseButtonHandler();
        comboBox.addActionListener(chooseAction);

        //добавление реакции на сохранение изображения
        SaveImageButton saveAction = new SaveImageButton();
        saveImage.addActionListener(saveAction);

        //Данные операции правильно разметят содержимое окна, сделают его
        //видимым (окна первоначально не отображаются при их создании для того,
        //чтобы можно было сконфигурировать их прежде, чем выводить на экран), и
        //затем запретят изменение размеров окна
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }

    /** drawFractal проходится по каждому пикселю и
     * зарисовывает его в соответствии с количеством проделанных итераций **/
    private void drawFractal () {
        for (int x = 0; x < size; x ++) {
            for (int y = 0; y < size; y ++) {
                //x, y - пиксельные координаты; xCord, yCord - координаты в пространстве фрактала
                double xCord = fractal.getCoord(range.x,range.x + range.width, size, x);
                double yCord = fractal.getCoord(range.y, range.y + range.height, size, y);
                int numIters = fractal.numIterations(xCord,yCord);

                if (numIters == -1) jDisplay.drawPixel(x,y,0);

                else {
                    float hue = 0.7f + (float) numIters / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    //получается плавная последовательность цветов от
                    //красного к желтому, зеленому, синему, фиолетовому и затем обратно к
                    //красному
                    jDisplay.drawPixel(x, y, rgbColor);
                }
            }
        }
        jDisplay.repaint();
    }

    /** внутренний класс для обработки событий
     java.awt.event.ActionListener от кнопки сброса **/
    public class InActionListener implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent event) {
            fractal.getInitialRange(range);
            drawFractal();
        }
    }
    /** внутренний класс для обработки событий
     java.awt.event.MouseListener с дисплея **/
    private class InMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {
            int x = event.getX();
            double xCord = fractal.getCoord(range.x, range.x+range.width, size,x);

            int y = event.getY();
            double yCord = fractal.getCoord(range.y, range.y+range.height, size,y);

            fractal.recenterAndZoomRange(range, xCord, yCord, 0.5);
            // При получении события о щелчке мышью, класс
            //отображает пиксельные координаты щелчка в область фрактала, а затем вызывает
            //метод генератора recenterAndZoomRange() с координатами, по которым
            //щелкнули, и масштабом 0.5
            drawFractal();
        }
    }

    /** Реализация класса реакции на выбор фрактала **/
    public class ChooseButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            JComboBox combo = (JComboBox) event.getSource();
            String name = (String) combo.getSelectedItem();
            switch (Objects.requireNonNull(name)) {
                case ("Mandelbrot") -> fractal = new Mandelbrot();
                case ("Tricorn") -> fractal = new Tricorn();
                case ("Burning Ship") -> fractal = new BurningShip();
            }
            fractal.getInitialRange(range);
            drawFractal();
        }
    }

    /** Реализация класса реакции на сохранение изображения **/
    public class SaveImageButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //настройка средства выбора файлов, чтобы
            //изображения сохранялись только в формате PNG
            JFileChooser chooser = new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter("PNG Images", "png");
            chooser.setFileFilter(filter);
            chooser.setAcceptAllFileFilterUsed(false);

            //сохранение фрактала на диск
            int result = chooser.showSaveDialog(jDisplay);
            if (result == JFileChooser.APPROVE_OPTION) {
                File dir = chooser.getSelectedFile();
                String dirString = dir.toString();
                //обработка исключений метода write()
                try{
                    BufferedImage image = jDisplay.getImage();
                    ImageIO.write(image, "png", dir);
                }
                catch(Exception exception){
                    JOptionPane.showMessageDialog(chooser, exception.getMessage(),"Can not save image", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main (String[] args) {

        FractalExplorer display = new FractalExplorer(800);
        display.createAndShowGUI();
        display.drawFractal();
    }
}