package com.dev.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullname;
    private String avatarUrl;

    public User() {
    }

    public User(String username, String password, String fullname, String avatarUrl) {
        this.password = password;
        this.username = username;
        this.fullname = fullname;
        this.avatarUrl = avatarUrl;
    }

    public User(int id, String username, String password, String fullname, String avatarUrl) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.fullname = fullname;
        this.avatarUrl = avatarUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public String toString() {
        return "User [id=" + id + ", password=" + password + ", username=" + username + ", fullname=" + fullname
                + ", avatarUrl=" + avatarUrl + "]";
    }

}
