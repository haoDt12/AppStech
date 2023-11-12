package com.datn.shopsale.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Cart implements Serializable {
    private String productId;
    private String userId;
    private String title;
    private String color;
    private String ram_rom;
    private int price;
    private int quantity;
    private String imgCover;
    private int status;

    public Cart() {
    }

    public Cart(String productId, String userId, String title, String color, String ram_rom, int price, int quantity, String imgCover, int status) {
        this.productId = productId;
        this.userId = userId;
        this.title = title;
        this.color = color;
        this.ram_rom = ram_rom;
        this.price = price;
        this.quantity = quantity;
        this.imgCover = imgCover;
        this.status = status;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRam_rom() {
        return ram_rom;
    }

    public void setRam_rom(String ram_rom) {
        this.ram_rom = ram_rom;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImgCover() {
        return imgCover;
    }

    public void setImgCover(String imgCover) {
        this.imgCover = imgCover;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}