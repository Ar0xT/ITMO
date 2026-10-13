package ru.ifmo.se.lab2;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Validator {
    private static final List<BigDecimal> ALLOWED_X =
            decimals("-2", "-1.5", "-1", "-0.5", "0", "0.5", "1", "1.5", "2");
    private static final List<BigDecimal> ALLOWED_R =
            decimals("1", "1.5", "2", "2.5", "3");
    private static final BigDecimal Y_MIN = new BigDecimal("-3");
    private static final BigDecimal Y_MAX = new BigDecimal("3");

    // plain decimal numbers only, like 2 or -0.67
    private static final Pattern NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final int MAX_LENGTH = 50;

    public static Point validate(Map<String, List<String>> params) throws BadRequestException {
        BigDecimal x = readNumber(params, "x");
        BigDecimal y = readNumber(params, "y");
        BigDecimal r = readNumber(params, "r");

        if (!isOneOf(x, ALLOWED_X)) {
            throw new BadRequestException("X must be one of -2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2");
        }
        if (y.compareTo(Y_MIN) <= 0 || y.compareTo(Y_MAX) >= 0) {
            throw new BadRequestException("Y must be strictly between -3 and 3");
        }
        if (!isOneOf(r, ALLOWED_R)) {
            throw new BadRequestException("R must be one of 1, 1.5, 2, 2.5, 3");
        }
        return new Point(x, y, r);
    }

    // reads exactly one value for the name and turns it into a number
    private static BigDecimal readNumber(Map<String, List<String>> params, String name)
            throws BadRequestException {
        String label = name.toUpperCase();
        List<String> values = params.get(name);
        if (values == null || values.isEmpty()) {
            throw new BadRequestException(label + " is missing");
        }
        if (values.size() > 1) {
            throw new BadRequestException("Only one value of " + label + " is allowed");
        }
        String text = values.get(0).trim();
        if (text.length() > MAX_LENGTH || !NUMBER.matcher(text).matches()) {
            throw new BadRequestException(label + " must be a number like 1.5 or -2");
        }
        return new BigDecimal(text);
    }

    private static boolean isOneOf(BigDecimal value, List<BigDecimal> allowed) {
        for (BigDecimal a : allowed) {
            if (a.compareTo(value) == 0) {
                return true;
            }
        }
        return false;
    }

    private static List<BigDecimal> decimals(String... texts) {
        return Arrays.stream(texts)
                .map(BigDecimal::new)
                .toList();
    }
}