package com.dev.dto;

import java.io.Serializable;

public class RegisterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String fullname;
    private String avatarUrl;

    public RegisterRequest() {
        // Default constructor
    }

    public RegisterRequest(String username, String password, String fullname, String avatarUrl) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
