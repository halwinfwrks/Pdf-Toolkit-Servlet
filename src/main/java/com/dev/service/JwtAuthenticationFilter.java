package com.dev.service;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dev.util.JwtUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class JwtAuthenticationFilter implements Filter {

    private static final String TAG = JwtAuthenticationFilter.class.getSimpleName();
    private static final Logger logger = LogManager.getLogger(TAG);

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String JWT_SESSION_ATTR = "jwt-token";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();
        logger.debug("{}: Processing request path: {}", TAG, path);

        // Nếu đã đăng nhập rồi mà lại cố vào /login hoặc /register => redirect
        if (isAuthenticated(request)) {
            if (isAuthPage(request, path)) {
                logger.info("{}: Authenticated user tried to access login/register page -> redirecting", TAG);
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
        }

        // Bỏ qua kiểm tra nếu là public
        if (isPublicPath(request, path)) {
            chain.doFilter(request, response);
            return;
        }

        // Ưu tiên check JWT trong session (web)
        String jwtToken = extractJwtFromSession(request);
        if (jwtToken != null) {
            try {
                if (JwtUtils.isJwtValid(jwtToken)) {
                    logger.info("{}: Valid JWT from session", TAG);
                    chain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                logger.error("{}: Session JWT invalid: {}", TAG, e.getMessage(), e);
            }
        }

        // Nếu không có, check JWT từ header (API)
        jwtToken = extractJwtFromHeader(request);
        if (jwtToken != null) {
            try {
                if (JwtUtils.isJwtValid(jwtToken)) {
                    logger.info("{}: Valid JWT from Authorization header", TAG);
                    chain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                logger.error("{}: Header JWT invalid: {}", TAG, e.getMessage(), e);
            }
        }

        // Không có JWT hợp lệ → redirect về login
        logger.warn("{}: No valid JWT token found", TAG);
        response.sendRedirect(request.getContextPath() + "/login");
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        try {
            String jwtToken = extractJwtFromSession(request);
            if (jwtToken != null && JwtUtils.isJwtValid(jwtToken))
                return true;

            jwtToken = extractJwtFromHeader(request);
            return jwtToken != null && JwtUtils.isJwtValid(jwtToken);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPublicPath(HttpServletRequest request, String path) {
        String context = request.getContextPath();
        return path.startsWith(context + "/auth/") ||
                path.startsWith(context + "/login") ||
                path.startsWith(context + "/register") ||
                path.startsWith(context + "/logout") ||
                path.startsWith(context + "/css/") ||
                path.startsWith(context + "/js/") ||
                path.startsWith(context + "/images/") ||
                path.startsWith(context + "/fonts/") ||
                path.endsWith(".jpg") || path.endsWith(".jpeg") ||
                path.endsWith(".ttf") || path.endsWith(".woff") ||
                path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png");
    }

    private boolean isAuthPage(HttpServletRequest request, String path) {
        String context = request.getContextPath();
        return path.startsWith(context + "/login") ||
                path.startsWith(context + "/register") ||
                path.startsWith(context + "/auth/login") ||
                path.startsWith(context + "/auth/register");
    }

    private String extractJwtFromHeader(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private String extractJwtFromSession(HttpServletRequest request) {
        Object jwtAttr = request.getSession(false) != null
                ? request.getSession(false).getAttribute(JWT_SESSION_ATTR)
                : null;
        return jwtAttr != null ? jwtAttr.toString() : null;
    }
}
