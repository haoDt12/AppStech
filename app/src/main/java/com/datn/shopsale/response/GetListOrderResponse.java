package com.datn.shopsale.response;

import java.util.ArrayList;
import java.util.Date;

public class GetListOrderResponse {

    public class ListOrder{
        public String _id;
        public ArrayList<GetListOrderResponse.Product> product;
        public String userId;
        public AddressId addressId;
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
        public ArrayList<ListOrder> listOrder;
        public String message;
        public int code;
    }
}
