package com.datn.shopsale.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartRequest {


    public static class Product{
        public String productId;
        public String color;
        public String ram_rom;
        public int quantity;

    }

    public static class Root{
        public String userId;
        public Product product;
        private String message;
        private int code;
    }

}


