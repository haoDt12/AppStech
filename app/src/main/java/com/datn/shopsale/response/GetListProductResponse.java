package com.datn.shopsale.response;

import java.util.ArrayList;

public class GetListProductResponse {
    public class Category{
        private String _id;
        private String title;
        private String date;
        private String img;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public class Product{
        private String _id;
        private Category category;
        private String title;
        private String description;
        private ArrayList<String> color;
        private String price;
        private String quantity;
        private String sold;
        private ArrayList<String> list_img;
        private String date;
        private ArrayList<String> ram_rom;
        private String img_cover;
        private String video;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public ArrayList<String> getColor() {
            return color;
        }

        public void setColor(ArrayList<String> color) {
            this.color = color;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getSold() {
            return sold;
        }

        public void setSold(String sold) {
            this.sold = sold;
        }

        public ArrayList<String> getList_img() {
            return list_img;
        }

        public void setList_img(ArrayList<String> list_img) {
            this.list_img = list_img;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public ArrayList<String> getRam_rom() {
            return ram_rom;
        }

        public void setRam_rom(ArrayList<String> ram_rom) {
            this.ram_rom = ram_rom;
        }

        public String getImg_cover() {
            return img_cover;
        }

        public void setImg_cover(String img_cover) {
            this.img_cover = img_cover;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }
    }

    public class Root{
        private ArrayList<Product> product;
        private String message;
        private int code;

        public ArrayList<Product> getProduct() {
            return product;
        }

        public void setProduct(ArrayList<Product> product) {
            this.product = product;
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
