package com.sage.api.models;

public class Java {

    private int javaId;
    private int creatorId;
    private String encodedJava;
    private String encodedDex;


    public Java() { }

    public int getJavaId() {
        return javaId;
    }

    public void setJavaId(int javaId) {
        this.javaId = javaId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getEncodedJava() {
        return encodedJava;
    }

    public void setEncodedJava(String encodedJava) {
        this.encodedJava = encodedJava;
    }

    public String getEncodedDex() {
        return encodedDex;
    }

    public void setEncodedDex(String encodedDex) {
        this.encodedDex = encodedDex;
    }
}