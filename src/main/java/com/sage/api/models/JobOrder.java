package com.sage.api.models;

import java.math.BigDecimal;

public class JobOrder {

    private int javaId;
    private BigDecimal bounty;
    private long timeout;
    private byte[] data;

    // Default constructor
    public JobOrder() {

    }

    public JobOrder(int javaId, BigDecimal bounty, long timeout, byte[] data) {
        this.javaId = javaId;
        this.bounty = bounty;
        this.timeout = timeout;
        this.data = data;
    }

    public BigDecimal getBounty() {
        return bounty;
    }

    public void setBounty(BigDecimal bounty) {
        this.bounty = bounty;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}