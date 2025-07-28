package com.dev.util;

import java.util.Optional;

import com.dev.model.User;
import com.dev.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    public static Optional<User> extractUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        Object tokenObj = request.getSession().getAttribute("jwtToken");
        if (tokenObj == null) {
            return Optional.empty();
        }
        String token = tokenObj.toString();

        String username = JwtUtils.getUsernameFromJwt(token).get();
        if (username == null || username.isEmpty()) {
            return Optional.empty();
        }

        User user = UserService.getInstance().findUserByUsername(username).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(user);
    }

}
