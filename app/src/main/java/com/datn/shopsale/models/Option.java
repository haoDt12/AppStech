package com.datn.shopsale.models;

public class Option {
    private String type;
    private String title;
    private String content;
    private String feesArise;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getFeesArise() {
        return feesArise;
    }

    public void setFeesArise(String feesArise) {
        this.feesArise = feesArise;
    }

    public Option(String type, String title, String content, String feesArise) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.feesArise = feesArise;
    }

    public Option() {
    }
}
