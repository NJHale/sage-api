package com.sage.rest.models;

public class JobOrder {

    private int ordererId;
    private int bounty;
    private long timeOut;
    private byte[] data;
    private String encodedJava;

    // Default constructor
    public JobOrder() {

    }

    public int getOrdererId() {
        return ordererId;
    }

    public void setOrdererId(int ordererId) {
        this.ordererId = ordererId;
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