package com.datn.shopsale.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Cart implements Serializable {
    private String productId;
    private String userId;
    private String title;
    private int price;
    private int quantity;
    private String imgCover;
    private int status;
    private ArrayList<Option> option;

    public Cart() {
    }

    public Cart(String productId, String userId, String title, ArrayList<Option> option, int price, int quantity, String imgCover, int status) {
        this.productId = productId;
        this.userId = userId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.imgCover = imgCover;
        this.status = status;
        this.option = option;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImgCover() {
        return imgCover;
    }

    public void setImgCover(String imgCover) {
        this.imgCover = imgCover;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<Option> getOption() {
        return option;
    }

    public void setOption(ArrayList<Option> option) {
        this.option = option;
    }

    public static class Option implements Serializable {
        private String type;
        private String title;
        private String content;
        private String quantity;
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

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public Option(String type, String title, String content, String quantity, String feesArise) {
            this.type = type;
            this.title = title;
            this.content = content;
            this.quantity = quantity;
            this.feesArise = feesArise;
        }

        public Option() {
        }
    }
}
