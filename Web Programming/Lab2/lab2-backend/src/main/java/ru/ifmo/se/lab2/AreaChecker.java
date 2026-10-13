package ru.ifmo.se.lab2;

import java.math.BigDecimal;

public class AreaChecker {
    private static final BigDecimal TWO = BigDecimal.valueOf(2); // For Triangle
    private static final BigDecimal FOUR = BigDecimal.valueOf(4); // For Circle

    public static boolean isHit(Point p) {
        return inSquare(p) || inQuarterCircle(p) || inTriangle(p);
    }

    // top right, the square from 0 to R on both axes
    private static boolean inSquare(Point p) {
        return p.x().signum() >= 0 && p.x().compareTo(p.r()) <= 0
                && p.y().signum() >= 0 && p.y().compareTo(p.r()) <= 0;
    }

    // bottom right, a quarter circle whose radius is half of R
    // 4(x^2 + y^2) <= R/4 (this is where the FOUR comes to play)
    private static boolean inQuarterCircle(Point p) {
        if (p.x().signum() < 0 || p.y().signum() > 0) {
            return false;
        }
        BigDecimal sumOfSquares = p.x().multiply(p.x()).add(p.y().multiply(p.y()));
        return FOUR.multiply(sumOfSquares).compareTo(p.r().multiply(p.r())) <= 0;
    }

    private static boolean inTriangle(Point p) {
        if (p.x().signum() > 0 || p.y().signum() > 0) {
            return false;
        }
        BigDecimal edge = TWO.multiply(p.x()).negate().subtract(p.r());
        return p.y().compareTo(edge) >= 0;
    }
}