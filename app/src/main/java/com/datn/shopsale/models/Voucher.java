package com.datn.shopsale.models;

public class Voucher {
    private String _id;
    private String title;
    private String content;
    private String sale;
    private String date;

    public Voucher() {
    }

    public Voucher(String _id, String title, String content, String sale, String date) {
        this._id = _id;
        this.title = title;
        this.content = content;
        this.sale = sale;
        this.date = date;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
