package com.datn.shopsale.models;

import androidx.annotation.NonNull;

public class User {
    private String _id;
    private String avatar;
    private String email;
    private String full_name;
    private String password;
    private String phone_number;
    private String role;

    public User() {
    }

    public User(String _id, String avatar, String email, String full_name, String password, String phone_number, String role) {
        this._id = _id;
        this.avatar = avatar;
        this.email = email;
        this.full_name = full_name;
        this.password = password;
        this.phone_number = phone_number;
        this.role = role;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", full_name='" + full_name + '\'' +
                ", password='" + password + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
