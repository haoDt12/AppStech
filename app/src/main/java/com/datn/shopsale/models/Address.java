package com.datn.shopsale.models;

public class Address {
    private String _id;
    private String name;
    private String detail;
    private String phone_number;

    public Address() {
    }

    public Address(String _id, String name, String detail, String phone_number) {
        this._id = _id;
        this.name = name;
        this.detail = detail;
        this.phone_number = phone_number;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
