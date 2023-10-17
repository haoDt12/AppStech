package com.datn.shopsale.models;

import java.util.ArrayList;
import java.util.Date;

public class Orders {
    private String id;
    private String userId;
    private ArrayList<String> product;

    private String title;
    private String status;
    private String img;
    private String address;
    private int quantity;
    private double price;
    private double total;
    private Date date_time;
    public Orders() {
    }

    public Orders(String id, String userId, ArrayList<String> product,String title, String status, String img, String address, int quantity, double price, double total, Date date_time) {
        this.id = id;
        this.userId = userId;
        this.product = product;
        this.title = title;
        this.status = status;
        this.img = img;
        this.address = address;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.date_time = date_time;
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

    public ArrayList<String> getProduct() {
        return product;
    }

    public void setProduct(ArrayList<String> product) {
        this.product = product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Date getDate_time() {
        return date_time;
    }

    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
