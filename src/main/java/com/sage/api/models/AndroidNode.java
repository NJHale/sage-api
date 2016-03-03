package com.sage.api.models;

public class AndroidNode {

    // Unique android node Id
    private String androidId;
    private int nodeId;
    private int ownerId;
    private String info;
    public AndroidNode(){
        // set and increment computeId
        //computeId = latestComputeId++;
    }

    public String getAndroidId() { return androidId; }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public int getNodeId(){
        return nodeId;
    }

    public void setNodeId(int nodeId){
        this.nodeId = nodeId;
    }

    public int getOwnerId(){
        return ownerId;
    }

    public void setOwnerId(int ownerId){
        this.ownerId = ownerId;
    }

    public String getInfo(){
        return info;
    }

    public void setInfo(String info){
        this.info = info;
    }
}
