package com.datn.shopsale.modelsv2;

public class ProductCart {
    private String _id;
    private String customer_id;
    private Product product_id;
    private int quantity;
    private int status = 1;
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Product getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Product product_id) {
        this.product_id = product_id;
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
