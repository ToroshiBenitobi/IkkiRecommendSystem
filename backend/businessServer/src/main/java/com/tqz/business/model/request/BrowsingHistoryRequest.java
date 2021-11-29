package com.tqz.business.model.request;

public class BrowsingHistoryRequest {
    private int userId;
    private int number;

    public BrowsingHistoryRequest(int userId, int number) {
        this.userId = userId;
        this.number = number;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
