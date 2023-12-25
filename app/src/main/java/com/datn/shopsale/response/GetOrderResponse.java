package com.datn.shopsale.response;

import java.util.ArrayList;

public class GetOrderResponse {
    public static class AddressId{
        public String _id;
        public String name;
        public String city;
        public String street;
        public String phone_number;
        public String date;
    }
    public static class Option{
        public String type;
        public String title;
        public String content;
        public String _id;
        public String feesArise;
    }
    public static class Order{
        public String _id;
        public String userId;
        public ArrayList<Product> product;
        public AddressId addressId;
        public int total;
        public String status;
        public String payment_method;
        public String date_time;
    }
    public static class Product{
        public ProductId productId;
        public ArrayList<Option> option;
        public int quantity;
        public String _id;
    }

    public static class ProductId{
        public String _id;
        public String category;
        public String title;
        public String description;
        public String price;
        public String quantity;
        public String sold;
        public ArrayList<String> list_img;
        public String date;
        public ArrayList<Option> option;
        public String img_cover;
        public String video;
    }
    public static class Root{
        public Order order;
        public String message;
        public int code;
    }
}
