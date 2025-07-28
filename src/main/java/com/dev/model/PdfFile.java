package com.dev.model;

import java.util.Date;

public class PdfFile {
    private int id;
    private int userId;
    private String name;
    private Long totalSize;
    private Long totalChunk;
    private Date lastModified;

    public PdfFile() {
    }

    public PdfFile(int userId, String name, Long totalSize, Long totalChunk, Date lastModified) {
        this.userId = userId;
        this.name = name;
        this.totalSize = totalSize;
        this.totalChunk = totalChunk;
        this.lastModified = lastModified;
    }

    public PdfFile(int id, int userId, String name, Long totalSize, Long totalChunk, Date lastModified) {
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

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public Long getTotalChunk() {
        return totalChunk;
    }

    public void setTotalChunk(Long totalChunk) {
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
