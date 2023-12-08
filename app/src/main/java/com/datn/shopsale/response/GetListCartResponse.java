package com.datn.shopsale.response;

import java.util.ArrayList;

public class GetListCartResponse {
    public class ListCart {
        private String productId;
        private String title;
        private int quantity;
        private String imgCover;
        private String price;
        private ArrayList<Option> option;
        private int status = 2;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public ArrayList<Option> getOption() {
            return option;
        }

        public void setOption(ArrayList<Option> option) {
            this.option = option;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

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
    }

    public class Root {
        private ArrayList<ListCart> listCart;
        private String message;
        private int code;

        public ArrayList<ListCart> getListCart() {
            return listCart;
        }

        public void setListCart(ArrayList<ListCart> listCart) {
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

}
