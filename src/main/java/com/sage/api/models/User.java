package com.sage.api.models;

public class User {

    private static int latestUserId = 0;
    private int userId;
    private String userEmail;

    public User() {
        // set and increment userId
        userId = latestUserId++;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
