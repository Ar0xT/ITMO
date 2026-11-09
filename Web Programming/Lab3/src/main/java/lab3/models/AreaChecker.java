package lab3.models;

public class AreaChecker {

    public static boolean isHit(double x, double y, double r) {
        return checkSquare(x, y, r) || checkCircle(x, y, r) || checkTriangle(x, y, r);
    }

    private static boolean checkSquare(double x, double y, double r) {
        return x >= 0 && x <= r && y >= 0 && y <= r;
    }

    private static boolean checkCircle(double x, double y, double r) {
        return x >= 0 && y <= 0 && (x*x + y*y) <= (r*r) / 4;
    }

    private static boolean checkTriangle(double x, double y, double r) {
        return x <= 0 && y >= 0 && y <= 2 * x + r;
    }

}
