package com.datn.shopsale.request;

import java.util.ArrayList;
import java.util.List;

public class OderRequest {
    public static class Product{
        private String productId;
        private ArrayList<Option> option;
        private int quantity;

        public Product(String productId, ArrayList<Option> option, int quantity) {
            this.productId = productId;
            this.option = option;
            this.quantity = quantity;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public ArrayList<Option> getOption() {
            return option;
        }

        public void setOption(ArrayList<Option> option) {
            this.option = option;
        }
    }

    public static class Root{
        private String voucherId;
        private String userId;
        private List<Product> product;
        private String address;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<Product> getProduct() {
            return product;
        }

        public void setProduct(List<Product> product) {
            this.product = product;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getVoucherId() {
            return voucherId;
        }

        public void setVoucherId(String voucherId) {
            this.voucherId = voucherId;
        }
    }
    public static class Option{
        private String type;
        private String title;
        private String content;
        private String quantity;
        private String feesArise;

        public Option(String type, String title, String content, String quantity, String feesArise) {
            this.type = type;
            this.title = title;
            this.content = content;
            this.quantity = quantity;
            this.feesArise = feesArise;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

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
}
