package com.datn.shopsale.models;

public class FeedBack {
    private String userId;
    private String productId;
    private double rating;
    private String nameUser;
    private String avtUser;
    private String comment;
    private String date;

    public FeedBack() {
    }

    public FeedBack(String userId, String productId, double rating, String comment,String nameUser,String avtUser,String date) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
        this.nameUser = nameUser;
        this.avtUser = avtUser;
        this.date = date;
    }

    public String getAvtUser() {
        return avtUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAvtUser(String avtUser) {
        this.avtUser = avtUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
