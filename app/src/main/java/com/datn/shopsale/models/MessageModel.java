package com.datn.shopsale.models;

import com.google.firebase.Timestamp;

public class MessageModel {
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private String image;

    public MessageModel() {
    }

    public MessageModel(String message, String senderId, Timestamp timestamp,String image) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
