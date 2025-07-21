package com.dev.model;

import java.util.Date;

public class FileInfo {
    private int id;
    private int userId;
    private String name;
    private String totalSize;
    private String totalChunk;
    private Date lastModified;

    public FileInfo() {
    }

    public FileInfo(int userId, String name, String totalSize, String totalChunk, Date lastModified) {
        this.userId = userId;
        this.name = name;
        this.totalSize = totalSize;
        this.totalChunk = totalChunk;
        this.lastModified = lastModified;
    }

    public FileInfo(int id, int userId, String name, String totalSize, String totalChunk, Date lastModified) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.totalSize = totalSize;
        this.totalChunk = totalChunk;
        this.lastModified = lastModified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public String getTotalChunk() {
        return totalChunk;
    }

    public void setTotalChunk(String totalChunk) {
        this.totalChunk = totalChunk;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
