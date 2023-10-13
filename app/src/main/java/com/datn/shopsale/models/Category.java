package com.datn.shopsale.models;

import androidx.annotation.NonNull;

public class Category {
    private String id;
    private String title;
    private String img;
    private String date;

    public Category() {
    }

    public Category(String id, String title, String img, String date) {
        this.id = id;
        this.title = title;
        this.img = img;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return "id: " + id + " title: " + title + "";
    }
}
