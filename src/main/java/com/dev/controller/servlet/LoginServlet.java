package com.dev.controller.servlet;

import java.io.IOException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dev.model.User;
import com.dev.service.UserService;
import com.dev.util.AuthUtils;
import com.dev.util.JwtUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final String TAG = LoginServlet.class.getSimpleName();
    private static final Logger logger = LogManager.getLogger(TAG);
    private final UserService userService = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null) {
            logger.warn("{}: Missing username or password", TAG);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Missing username or password\"}");
            return;
        }

        password = AuthUtils.hashedPassword(password);
        Optional<User> userOpt = userService.authenticate(username, password);

        if (userOpt.isEmpty()) {
            logger.warn("{}: Authentication failed for username '{}'", TAG, username);
            req.setAttribute("error", "Invalid username or password");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        User user = userOpt.get();

        Claims claims = Jwts.claims().add("username", user.getUsername()).build();
        Optional<String> jwtOpt = JwtUtils.generateJwt(claims);
        if (jwtOpt.isEmpty()) {
            logger.error("{}: Failed to generate JWT for user '{}'", TAG, user.getUsername());
            req.setAttribute("error", "Internal server error");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        String jwtToken = jwtOpt.get();
        logger.info("{}: User '{}' authenticated successfully", TAG, user.getUsername());
        req.getSession().setAttribute("jwt-token", jwtToken);
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
