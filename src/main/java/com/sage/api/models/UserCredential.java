package com.sage.api.models;

public class UserCredential {

    private String googleIdStr;

    public UserCredential() { }

    public String getGoogleIdToken() {
        return googleIdStr;
    }

    public void setGoogleIdStr(String googleIdStr) {
        this.googleIdStr = googleIdStr;
    }
}