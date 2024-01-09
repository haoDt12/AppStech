package com.datn.shopsale.modelsv2;

public class Category {
    private String _id;
    private String title;
    private String date;
    private String img;
    private int __v;

    public Category(String _id, String title, String date, String img) {
        this._id = _id;
        this.title = title;
        this.date = date;
        this.img = img;
    }

    public Category() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}
