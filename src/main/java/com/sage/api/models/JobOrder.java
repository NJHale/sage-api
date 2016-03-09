package com.sage.api.models;

public class JobOrder {

    private int bounty;
    private long timeOut;
    private byte[] data;
    private String encodedJava;

    // Default constructor
    public JobOrder() {

    }

    public JobOrder(int bounty, long timeOut, byte[] data, String encodedJava) {
        this.bounty = bounty;
        this.timeOut = timeOut;
        this.data = data;
        this.encodedJava = encodedJava;
    }

    public int getBounty() {
        return bounty;
    }

    public void setBounty(int bounty) {
        this.bounty = bounty;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getEncodedJava() {
        return encodedJava;
    }

    public void setEncodedJava(String encodedJava) {
        this.encodedJava = encodedJava;
    }
}