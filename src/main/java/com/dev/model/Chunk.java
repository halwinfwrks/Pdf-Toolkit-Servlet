package com.dev.model;

public class Chunk {
    private int id;
    private int fileId;
    private int index;
    private String value;

    public Chunk() {
    }

    public Chunk(int fileId, int index, String value) {
        this.fileId = fileId;
        this.index = index;
        this.value = value;
    }

    public Chunk(int id, int fileId, int index, String value) {
        this.id = id;
        this.fileId = fileId;
        this.index = index;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
