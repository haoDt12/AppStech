package com.datn.shopsale.modelsv2;

import java.util.List;

public class ProductCart {
    private String customer_id;
    private Product product_id;
    private int quantity;
    private int status;
    private String create_time;

    public ProductCart(String customer_id, Product productCart, int quantity,int status, String create_time) {
        this.customer_id = customer_id;
        this.product_id = productCart;
        this.quantity = quantity;
        this.status = status;
        this.create_time = create_time;
    }

    public ProductCart() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public Product getProductCart() {
        return product_id;
    }

    public void setProductCart(Product productCart) {
        this.product_id = productCart;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
