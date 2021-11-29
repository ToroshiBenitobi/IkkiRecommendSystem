package com.tqz.business.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RatingInfo {
    @JsonIgnore
    private String _id;

    private int productId;
    private double averageScore;
    private int ratingSum;
    private Integer[] ratingStarSum = {0, 0, 0, 0, 0};

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public int getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(int ratingSum) {
        this.ratingSum = ratingSum;
    }

    public Integer[] getRatingStarSum() {
        return ratingStarSum;
    }

    public void setRatingStarSum(Integer[] ratingStarSum) {
        this.ratingStarSum = ratingStarSum;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
