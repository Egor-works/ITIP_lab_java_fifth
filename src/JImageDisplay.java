import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JImageDisplay extends JComponent{

    /**Класс BufferedImage управляет изображением, содержимое которого можно записать**/
    private BufferedImage image;
    /** Геттер возвращающий изображение **/
    public BufferedImage getImage(){
        return image;
    }

    /**Конструктор JImageDisplay принимает целочисленные
     значения ширины и высоты, и инициализировать объект BufferedImage новым
     изображением с этой шириной и высотой, и типом изображения
     TYPE_INT_RGB**/
    public JImageDisplay(int weight, int height) {
        //значение TYPE_INT_RGB обозначает, что красные, зеленые и синие компоненты имеют по 8 битов, представленные в формате int в указанном порядке
        image = new BufferedImage(weight, height, BufferedImage.TYPE_INT_RGB);
        Dimension size = new Dimension(weight, height);
        //setPreferredSize задаёт предпочтительный размер этого компонента
        super.setPreferredSize(size);
    }

    //paintComponent первым прорисовывает сам компонент
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //drawImage рисует столько изображени, чтобы уместиться в указанные рамки
        g.drawImage(image,0,0,image.getWidth(),image.getHeight(),null);

    }

    /** метод закрашивающий изображение в чёрный **/
    public void clearImage() {
        for (int i=0; i < image.getHeight(); i++ ) {
            for (int j = 0; j < image.getWidth(); j ++) {
                drawPixel(i,j,0);
            }
        }
    }

    /** метод закрашивающий изображение в заданный цвет **/
    public void drawPixel (int x, int y, int rgbColor) {
        image.setRGB(x,y,rgbColor);
    }
}
