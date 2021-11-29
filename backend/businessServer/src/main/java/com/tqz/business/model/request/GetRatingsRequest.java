package com.tqz.business.model.request;

public class GetRatingsRequest {
    private int productId;

    public GetRatingsRequest(int productId) {
        this.productId = productId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
