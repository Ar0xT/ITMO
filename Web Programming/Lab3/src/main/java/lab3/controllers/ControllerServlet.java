package lab3.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"", "/controller"})
public class ControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if ("clear".equals(req.getParameter("action"))) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.removeAttribute("results");
            }

            resp.sendRedirect("controller");
            return;
        }

        boolean hasPointData = req.getParameter("x") != null
                || req.getParameter("y") != null
                || req.getParameter("r") != null;

        if (hasPointData) {
            req.getRequestDispatcher("/check").forward(req, resp);
        } else {
            resp.setHeader("Cache-Control", "no-store");
            req.getRequestDispatcher("/WEB-INF/index.jsp").forward(req, resp);
        }
    }
}