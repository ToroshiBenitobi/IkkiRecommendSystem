package com.tqz.business.model.recom;

public class History {

    private int productId;

    private long timestamp;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public History(int productId, long timestamp) {
        this.productId = productId;
        this.timestamp = timestamp;
    }
}
