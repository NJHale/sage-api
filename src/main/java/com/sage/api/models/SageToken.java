package com.sage.api.models;

public class SageToken {

    private String tokenString;
    private int userId;

    /**
     * Default SageToken constructor
     */
    public SageToken() {

    }

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
