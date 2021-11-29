package com.tqz.business.model.request;

public class FindOfferByProductRequestRequest {
    private int num;
    private int productId;

    public FindOfferByProductRequestRequest(int num, int productId) {
        this.num = num;
        this.productId = productId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
