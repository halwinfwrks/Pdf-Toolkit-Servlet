package com.dev.dto;

import com.dev.model.User;

// AuthenticationResult.java
public class AuthenticationResult {
    private final boolean successful;
    private final User user;
    private final String errorMessage;

    private AuthenticationResult(boolean successful, User user, String errorMessage) {
        this.successful = successful;
        this.user = user;
        this.errorMessage = errorMessage;
    }

    public static AuthenticationResult success(User user) {
        return new AuthenticationResult(true, user, null);
    }

    public static AuthenticationResult failed(String errorMessage) {
        return new AuthenticationResult(false, null, errorMessage);
    }

    // Getters
    public boolean isSuccessful() {
        return successful;
    }

    public User getUser() {
        return user;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}