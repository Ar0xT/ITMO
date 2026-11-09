package lab3.controllers;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lab3.models.AreaChecker;
import lab3.models.Result;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@WebServlet("/check")
public class AreaCheckServlet extends HttpServlet {
    private static final BigDecimal X_MIN = new BigDecimal("-3");
    private static final BigDecimal X_MAX = new BigDecimal("3");
    private static final BigDecimal Y_MIN= new BigDecimal("-2");
    private static final BigDecimal Y_MAX = new BigDecimal("2");
    private static final List<BigDecimal> R_VALUES = List.of(
            new BigDecimal("1"),
            new BigDecimal("1.5"),
            new BigDecimal("2"),
            new BigDecimal("2.5"),
            new BigDecimal("3")
    );

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        if (req.getDispatcherType() != DispatcherType.FORWARD) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        BigDecimal x = parse(req.getParameter("x"));
        BigDecimal y = parse(req.getParameter("y"));
        BigDecimal r = parse(req.getParameter("r"));

        String error = validate(x, y, r);
        if (error != null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            req.setAttribute("error", error);
        } else {
            boolean hit = AreaChecker.isHit(x.doubleValue(), y.doubleValue(), r.doubleValue());
            Result result = new Result(x, y, r, hit);
            saveToSession(req, result);
            req.setAttribute("result", result);
        }
        req.getRequestDispatcher("/WEB-INF/result.jsp").forward(req, resp);
    }

    public static BigDecimal parse(String text) {
        if (text == null) {
            return null;
        }
        try {
            return new BigDecimal(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String validate(BigDecimal x, BigDecimal y, BigDecimal r) {
        if (x == null || x.compareTo(X_MIN) <= 0 || x.compareTo(X_MAX) >= 0) {
            return "X must be a number greater than -3 and less than 3.";
        }
        if (y == null || y.compareTo(Y_MIN) < 0 || y.compareTo(Y_MAX) > 0) {
            return "Y must be a number greater than -2 and less than 2.";
        }
        if (r == null || R_VALUES.stream().noneMatch(value -> value.compareTo(r) == 0)) {
            return "R must be 1, 1.5, 2, 2.5, or 3";
        }
        return null;
    }

    public static void saveToSession(HttpServletRequest req, Result result) {
        HttpSession session = req.getSession();
        @SuppressWarnings("unchecked")
        List<Result> results = (List<Result>) session.getAttribute("results");
        if (results == null) {
            results = new CopyOnWriteArrayList<>();
            session.setAttribute("results", results);
        }
        results.add(result);
    }
}