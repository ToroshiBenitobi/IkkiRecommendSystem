package com.tqz.business.model.request;

import scala.collection.immutable.List;

public class AddOfferRequest {
    private int ownerId;

    private int productId;

    private Double price;

    private int capacity;

    public AddOfferRequest(int ownerId, int productId, Double price, int capacity) {
        this.ownerId = ownerId;
        this.productId = productId;
        this.price = price;
        this.capacity = capacity;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
