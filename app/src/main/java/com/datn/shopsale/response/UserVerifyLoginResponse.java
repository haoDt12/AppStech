package com.datn.shopsale.response;

import java.io.Serializable;
import java.util.ArrayList;

public class UserVerifyLoginResponse implements Serializable {
    public class Address implements Serializable{
        private String _id;
        private String name;
        private String detail;
        private String phone_number;
        private String date;

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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "_id='" + _id + '\'' +
                    ", name='" + name + '\'' +
                    ", detail='" + detail + '\'' +
                    ", phone_number='" + phone_number + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }

    public class Root implements Serializable{
        private User user;
        private String token;
        private String message;
        private int code;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "Root{" +
                    "user=" + user +
                    ", token='" + token + '\'' +
                    ", message='" + message + '\'' +
                    ", code=" + code +
                    '}';
        }
    }

    public class User implements Serializable{
        private String _id;
        private String avatar;
        private String email;
        private String password;
        private String full_name;
        private String phone_number;
        private String role;
        private ArrayList<Address> address;
        private String date;
        private String account_type;
        private int __v;
        private Object otp;

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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
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

        public ArrayList<Address> getAddress() {
            return address;
        }

        public void setAddress(ArrayList<Address> address) {
            this.address = address;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAccount_type() {
            return account_type;
        }

        public void setAccount_type(String account_type) {
            this.account_type = account_type;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }

        public Object getOtp() {
            return otp;
        }

        public void setOtp(Object otp) {
            this.otp = otp;
        }

        @Override
        public String toString() {
            return "User{" +
                    "_id='" + _id + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    ", full_name='" + full_name + '\'' +
                    ", phone_number='" + phone_number + '\'' +
                    ", role='" + role + '\'' +
                    ", address=" + address +
                    ", date='" + date + '\'' +
                    ", account_type='" + account_type + '\'' +
                    ", __v=" + __v +
                    ", otp=" + otp +
                    '}';
        }
    }



}
