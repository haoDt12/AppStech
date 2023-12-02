package com.datn.shopsale.models;

import java.util.ArrayList;

public class ResponeFeedBack {
    private ArrayList<FeedBack> listFeedBack;
    private String message;
    private int code;

    public ArrayList<FeedBack> getListFeedBack() {
        return listFeedBack;
    }

    public void setListFeedBack(ArrayList<FeedBack> listFeedBack) {
        this.listFeedBack = listFeedBack;
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
