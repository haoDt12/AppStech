package com.datn.shopsale.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartRequest {


    public static class Product{
        public String productId;
        public String color;
        public String ram_rom;
        public int quantity;

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
    }

    public static class Root{
        public String userId;
        public Product product;
    }

}


