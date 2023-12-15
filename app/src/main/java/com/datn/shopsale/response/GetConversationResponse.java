package com.datn.shopsale.response;


import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetConversationResponse {

    public static class Conversation implements Serializable {
        private String _id;
        private String name;
        private ArrayList<User> user;
        private String timestamp;

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

        public ArrayList<User> getUser() {
            return user;
        }

        public void setUser(ArrayList<User> user) {
            this.user = user;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @NonNull
        @Override
        public String toString() {
            return "Conversation{" +
                    "_id='" + _id + '\'' +
                    ", name='" + name + '\'' +
                    ", user=" + user +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }
    }

    public static class Root {
        private ArrayList<Conversation> conversation;
        private String message;
        private int code;

        public Root(ArrayList<Conversation> conversation, String message, int code) {
            this.conversation = conversation;
            this.message = message;
            this.code = code;
        }

        public ArrayList<Conversation> getConversation() {
            return conversation;
        }

        public void setConversation(ArrayList<Conversation> conversation) {
            this.conversation = conversation;
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
    }

    public static class User implements Serializable {
        private String _id;
        private String avatar;
        private String email;
        private String password;
        private String full_name;
        private String phone_number;
        private String role;
        private List<String> address;
        private String date;
        private String account_type;
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

        public List<String> getAddress() {
            return address;
        }

        public void setAddress(List<String> address) {
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
                    ", otp=" + otp +
                    '}';
        }
    }
}
