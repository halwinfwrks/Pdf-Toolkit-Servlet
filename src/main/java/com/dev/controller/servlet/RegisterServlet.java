package com.dev.controller.servlet;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dev.model.User;
import com.dev.service.UserService;
import com.dev.util.AuthUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final String TAG = RegisterServlet.class.getSimpleName();
    private static final Logger logger = LogManager.getLogger(TAG);
    private final UserService userService = UserService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("{}: GET request from {}", TAG, req.getRemoteAddr());
        List<String> usernames = userService.findAllUsers().stream().map(user -> user.getUsername()).toList();
        req.setAttribute("usernames", usernames);
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null) {
            logger.warn("{}: Registration failed due to missing username or password", TAG);
            req.setAttribute("error", "Username and password are required.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        if (userService.isUsernameExists(username)) {
            logger.warn("{}: Registration failed, username {} already exists", TAG, username);
            List<String> usernames = userService.findAllUsers().stream().map(user -> user.getUsername()).toList();
            req.setAttribute("usernames", usernames);
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(AuthUtils.hashedPassword(password));
        newUser.setFullname("User " + UUID.randomUUID().toString().substring(0, 8));
        newUser.setAvatarUrl(req.getContextPath() + "/images/default-user.jpg");

        newUser.setId(userService.saveUser(newUser));

        if (newUser.getId() > 0) {
            logger.info("{}: User {} registered successfully with ID {}", TAG, username, newUser.getId());
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }
}
