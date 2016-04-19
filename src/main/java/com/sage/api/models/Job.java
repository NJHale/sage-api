package com.sage.api.models;

import java.math.BigDecimal;
import java.util.Date;

public class Job {

    private int jobId;
    private int ordererId;
    private int nodeId;
    private int javaId;
    private BigDecimal bounty;
    private JobStatus status;
    private long timeout;
    private Date completion;
    private byte[] data;
    private byte[] result;

    public Job() { }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) { this.jobId = jobId; }

    public BigDecimal getBounty() {
        return bounty;
    }

    public void setBounty(BigDecimal bounty) {
        this.bounty = bounty;
    }

    public int getOrdererId() { return ordererId; }

    public void setOrdererId(int ordererId) { this.ordererId = ordererId; }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getJavaId() {
        return javaId;
    }

    public void setJavaId(int javaId) {
        this.javaId = javaId;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeOut) {
        this.timeout = timeOut;
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

    public Date getCompletion() { return completion; }

    public void setCompletion(Date completion) { this.completion = completion; }

}