package com.dev.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dev.config.EnvConfig;
import com.dev.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtils {
    private static final String TAG = JwtUtils.class.getSimpleName();
    private static final Logger logger = LogManager.getLogger(TAG);
    private static volatile Key jwtSecretKey;

    public static Key getJwtSecretKey() {
        if (jwtSecretKey == null) {
            synchronized (JwtUtils.class) {
                if (jwtSecretKey == null) {
                    String secret = EnvConfig.get("jwt-authentication-secret");
                    if (secret == null || secret.length() < 32) {
                        logger.error("{}: JWT secret must be at least 32 characters. Provided: {}", TAG, secret);
                        throw new IllegalStateException("JWT secret must be at least 32 characters (256 bits)");
                    }
                    jwtSecretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                    logger.info("{}: JWT secret key initialized successfully", TAG);
                }
            }
        }
        return jwtSecretKey;
    }

    // Parse JWT safely
    private static Optional<Claims> parseJwt(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getJwtSecretKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            return Optional.of(claims);
        } catch (Exception e) {
            logger.error("{}: Failed to parse JWT: {}", TAG, e.getMessage());
            return Optional.empty();
        }
    }

    // Generate JWT from claims
    public static Optional<String> generateJwt(Claims claims) {
        try {
            String jwt = Jwts.builder()
                    .setClaims(claims)
                    .signWith(getJwtSecretKey())
                    .compact();
            return Optional.of(jwt);
        } catch (Exception e) {
            logger.error("{}: Failed to generate JWT: {}", TAG, e.getMessage());
            return Optional.empty();
        }
    }

    // Get username from token
    public static Optional<String> getUsernameFromJwt(String jwt) {
        return parseJwt(jwt)
                .map(claims -> claims.get("username", String.class));
    }

    // Validate token + user
    public static boolean isJwtValid(String jwt) {
        return parseJwt(jwt)
                .map(claims -> {
                    String username = claims.get("username", String.class);
                    if (username == null || username.isEmpty()) {
                        logger.warn("{}: JWT does not contain a valid username", TAG);
                        return false;
                    }

                    boolean exists = UserService.getInstance().isUsernameExists(username);
                    if (!exists) {
                        logger.error("{}: User from JWT not found: {}", TAG, username);
                        throw new IllegalStateException("User does not exist: " + username);
                    }

                    return true;
                })
                .orElseGet(() -> {
                    logger.warn("{}: JWT is invalid or expired", TAG);
                    return false;
                });
    }
}
