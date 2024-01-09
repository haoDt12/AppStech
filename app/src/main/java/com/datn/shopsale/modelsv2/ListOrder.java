package com.datn.shopsale.modelsv2;

import java.io.Serializable;

public class ListOrder implements Serializable {
    private String productCartId;
    private String product_id;
    private String quantity;

    public String getProductCartId() {
        return productCartId;
    }

    public void setProductCartId(String productCartId) {
        this.productCartId = productCartId;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
