package com.datn.shopsale.response;

import java.util.ArrayList;

public class GetListOrderResponse {

    public static class AddressId{
        public String _id;
        public String name;
        public String city;
        public String street;
        public String phone_number;
        public String date;

        @Override
        public String toString() {
            return "AddressId{" +
                    "_id='" + _id + '\'' +
                    ", name='" + name + '\'' +
                    ", city='" + city + '\'' +
                    ", street='" + street + '\'' +
                    ", phone_number='" + phone_number + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }

    public static class ListOrder{
        public String _id;
        public String userId;
        public ArrayList<Product> product;
        public AddressId addressId;
        public int total;
        public String status;
        public String payment_method;
        public String date_time;

        @Override
        public String toString() {
            return "ListOrder{" +
                    "_id='" + _id + '\'' +
                    ", userId='" + userId + '\'' +
                    ", product=" + product +
                    ", addressId=" + addressId +
                    ", total=" + total +
                    ", status='" + status + '\'' +
                    ", date_time='" + date_time + '\'' +
                    '}';
        }
    }

    public static class Option{
        public String type;
        public String title;
        public String content;
        public String feesArise;

        @Override
        public String toString() {
            return "Option{" +
                    "type='" + type + '\'' +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", feesArise='" + feesArise + '\'' +
                    '}';
        }
    }

    public static class Product{
        public String productId;
        public ArrayList<Option> option;
        public int quantity;

        @Override
        public String toString() {
            return "Product{" +
                    "productId='" + productId + '\'' +
                    ", option=" + option +
                    ", quantity=" + quantity +
                    '}';
        }
    }

    public static class Root{
        public ArrayList<ListOrder> listOrder;
        public String message;
        public int code;

        @Override
        public String toString() {
            return "Root{" +
                    "listOrder=" + listOrder +
                    ", message='" + message + '\'' +
                    ", code=" + code +
                    '}';
        }
    }
}
