package com.datn.shopsale.models;

import java.util.ArrayList;

public class ResponseCart {
    private ArrayList<Cart> listCart;
    private String message;
    private int code;

    public ArrayList<Cart> getListCart() {
        return listCart;
    }

    public void setListCart(ArrayList<Cart> listCart) {
        this.listCart = listCart;
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
