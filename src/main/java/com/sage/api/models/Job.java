package com.sage.api.models;

import java.util.Date;

public class Job {

    private int jobId;
    private int ordererId;
    private int nodeId;
    private int bounty;
    private JobStatus status;
    private long timeOut;
    private String encodedDex;
    private String encodedJava;
    private byte[] data;
    private byte[] result;
    private Date completion;

    /**
     * Default constructor
     */
    public Job() {

    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getOrdererId() {
        return ordererId;
    }

    public void setOrdererId(int ordererId) {
        this.ordererId = ordererId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getBounty() {
        return bounty;
    }

    public void setBounty(int bounty) {
        this.bounty = bounty;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public String getEncodedDex() {
        return encodedDex;
    }

    public void setEncodedDex(String encodedDex) {
        this.encodedDex = encodedDex;
    }

    public String getEncodedJava() {
        return encodedJava;
    }

    public void setEncodedJava(String encodedJava) {
        this.encodedJava = encodedJava;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    public Date getCompletion() {
        return completion;
    }

    public void setCompletion(Date completion) {
        this.completion = completion;
    }
}
