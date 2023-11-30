package com.datn.shopsale.request;

import java.util.List;

public class OrderVnPayRequest {
    public static class Product{
        private String productId;
        private String color;
        private String ram_rom;
        private int quantity;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getRam_rom() {
            return ram_rom;
        }

        public void setRam_rom(String ram_rom) {
            this.ram_rom = ram_rom;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Product(String productId, String color, String ram_rom, int quantity) {
            this.productId = productId;
            this.color = color;
            this.ram_rom = ram_rom;
            this.quantity = quantity;
        }
    }

    public static class Root{
        private String userId;
        private List<OderRequest.Product> product;
        private String address;
        private String amount;
        private String bankCode;
        private String language;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<OderRequest.Product> getProduct() {
            return product;
        }

        public void setProduct(List<OderRequest.Product> product) {
            this.product = product;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
