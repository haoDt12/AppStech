package com.datn.shopsale.modelsv2;

public class FeedBack {
    private Customer customer_id;
    private String product_id;
    private String rating;
    private String comment;
    private String create_time;

    public FeedBack() {
    }

    public FeedBack(Customer customer_id, String product_id, String rating, String comment, String create_time) {
        this.customer_id = customer_id;
        this.product_id = product_id;
        this.rating = rating;
        this.comment = comment;
        this.create_time = create_time;
    }

    public Customer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Customer customer_id) {
        this.customer_id = customer_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
