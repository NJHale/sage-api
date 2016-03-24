package com.sage.api.models;



import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * Created by Nick Hale on 2/21/16.
 * @author Nick Hale
 *         NJohnHale@gmail.com
 *
 */
@XmlRootElement
public class Job {

    /**
     * Hibernate Annotations for ORM persistence
     */
    private int jobId;

    private int ordererId;

    private int nodeId;

    private JobStatus status;

    private long timeOut;

    private String encodedDex;

    private byte[] data;

    private byte[] result;

    private Date completion;

    /**
     * Default constructor
     */
    public Job() { }

    /**
     * Jersey JAXB Annotations
     */
    @XmlElement(name = "jobId")
    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) { this.jobId = jobId; }

    @XmlElement(name = "ordererId")
    public int getOrdererId() { return ordererId; }

    public void setOrdererId(int ordererId) { this.ordererId = ordererId; }

    @XmlElement(name = "nodeId")
    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }


    @XmlElement(name = "status")
    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @XmlElement(name = "timeout")
    @JsonProperty("timeout")
    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    @XmlElement(name = "encodedDex")
    public String getEncodedDex() { return encodedDex; }

    public void setEncodedDex(String encodedDex) { this.encodedDex = encodedDex; }

    @XmlElement(name = "data")
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @XmlElement(name = "result")
    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    @XmlElement(name = "completion")
    public Date getCompletion() { return completion; }

    public void setCompletion(Date completion) { this.completion = completion; }

}
