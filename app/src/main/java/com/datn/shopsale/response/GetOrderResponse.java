package com.datn.shopsale.response;

import java.util.ArrayList;

public class GetOrderResponse {
    public class Order{
        public String _id;
        public ArrayList<GetOrderResponse.Product> product;
        public String userId;
        public GetOrderResponse.AddressId addressId;
        public String date_time;
        public String status;
        public int total;
    }

    public class Product{
        public String productId;
        public String title;
        public String color;
        public String price;
        public int quantity;
        public String ram_rom;
        public String img_cover;

        @Override
        public String toString() {
            return "Product{" +
                    "_id='" + productId + '\'' +
                    ", title='" + title + '\'' +
                    ", color='" + color + '\'' +
                    ", price='" + price + '\'' +
                    ", quantity=" + quantity +
                    ", ram_rom='" + ram_rom + '\'' +
                    ", img_cover='" + img_cover + '\'' +
                    '}';
        }
    }

    public class AddressId{
        public String _id;
        public String name;
        public String city;
        public String street;
        public String phone_number;
        public String date;
        public int __v;
    }

    public class Root{
        public GetOrderResponse.Order order;
        public String message;
        public int code;
    }

}
