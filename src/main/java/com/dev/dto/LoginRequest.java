package com.dev.dto;

public class LoginRequest {
    private final String username;
    private final String password;
    private final boolean rememberMe;
    
    public LoginRequest(String username, String password, boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
    }
    
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
               password != null && !password.isEmpty() &&
               username.length() <= 100 && password.length() <= 200;
    }
    
    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isRememberMe() { return rememberMe; }
}