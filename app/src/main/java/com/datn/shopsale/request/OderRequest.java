package com.datn.shopsale.request;

import java.util.List;

public class OderRequest {
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
    }
}
