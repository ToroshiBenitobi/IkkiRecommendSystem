package com.tqz.business.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Product {

    @JsonIgnore
    private String _id;

    private int productId;

    private String name;

    private String imageUrl;

    private Double score;

    private String categories;

    private String tags;

    private Double originalPrice;

    private Double lowestPrice;

    private int offersNum;

    private RatingInfo ratingInfo;

    public RatingInfo getRatingInfo() {
        return ratingInfo;
    }

    public void setRatingInfo(RatingInfo ratingInfo) {
        this.ratingInfo = ratingInfo;
    }

    public void setOffersNum(int offersNum) {
        this.offersNum = offersNum;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(Double lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public int getOffersNum() {
        return offersNum;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}