import java.awt.geom.Rectangle2D;

public class Tricorn extends FractalGenerator {
    /** Максимальное число итераций **/
    public static final int MAX_ITERATIONS = 2000;

    /** метод позволяет генератору
     * фракталов определить наиболее «интересную» область комплексной плоскости
     * для конкретного фрактала; устанавливает начальный диапазон
     * */
    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2;
        range.width = 4;
        range.height = 4;
    }

    /** метод реализует итеративную
     * функцию для Треугольного фрактала
     **/
    @Override
    public int numIterations(double x, double y) {
        int iteration = 0;
        double zReal = 0;
        double zImaginary = 0;

        while (iteration < MAX_ITERATIONS && zReal * zReal + zImaginary * zImaginary < 4)
        {
            double zRealNext = zReal * zReal - zImaginary * zImaginary + x;
            double zImaginaryNext = -2 * zReal * zImaginary + y;
            zReal = zRealNext;
            zImaginary = zImaginaryNext;
            iteration++;
        }


        if (iteration == MAX_ITERATIONS)
        {
            return -1;
        }

        return iteration;
    }

    /** реализация метода toString() для вывода названия **/
    public String toString(){
        return "Tricorn";
    }
}

