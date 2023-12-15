package com.datn.shopsale.models;


import java.util.ArrayList;

public class Conversation {
    private String id;
    private String name;
    private ArrayList<User> user;
    private String timestamp;

    public Conversation(String id, String name, ArrayList<User> user, String timestamp) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
