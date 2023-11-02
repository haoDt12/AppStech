package com.datn.shopsale.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Cart {
    private String id;
    private String userId;
    private ArrayList<String> productId;
    private String title;
    private int status;
    private String img;
    private int quantity;
    private double price;
    private double total;
    private Date date;

    public Cart() {
    }

    public Cart(String id, String userId, ArrayList<String> productId, String title, int status, String img, int quantity, double price, double total, Date date) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.title = title;
        this.status = status;
        this.img = img;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getProductId() {
        return productId;
    }

    public void setProductId(ArrayList<String> productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}