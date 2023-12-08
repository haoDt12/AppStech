package com.datn.shopsale.models;

import com.datn.shopsale.response.GetListProductResponse;

import java.util.ArrayList;

public class Product {
    private String _id;
    private GetListProductResponse.Category category;
    private String title;
    private String description;
    private ArrayList<String> color;
    private String price;
    private String quantity;
    private String sold;
    private ArrayList<String> list_img;
    private String date;
    private ArrayList<String> ram_rom;
    private String img_cover;
    private String video;

    public Product() {
    }

    public Product(String _id, GetListProductResponse.Category category, String title, String description, String price, String quantity, String sold, ArrayList<String> list_img, String date, String img_cover, String video) {
        this._id = _id;
        this.category = category;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.sold = sold;
        this.list_img = list_img;
        this.date = date;
        this.img_cover = img_cover;
        this.video = video;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public GetListProductResponse.Category getCategory() {
        return category;
    }

    public void setCategory(GetListProductResponse.Category category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getColor() {
        return color;
    }

    public void setColor(ArrayList<String> color) {
        this.color = color;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public ArrayList<String> getList_img() {
        return list_img;
    }

    public void setList_img(ArrayList<String> list_img) {
        this.list_img = list_img;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getRam_rom() {
        return ram_rom;
    }

    public void setRam_rom(ArrayList<String> ram_rom) {
        this.ram_rom = ram_rom;
    }

    public String getImg_cover() {
        return img_cover;
    }

    public void setImg_cover(String img_cover) {
        this.img_cover = img_cover;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }


}

