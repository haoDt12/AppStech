package com.datn.shopsale.request;

import java.util.List;

public class GetProductByIdResponse {
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
        private List<String> color;
        private String price;
        private String quantity;
        private String sold;
        private List<String> list_img;
        private String date;
        private List<String> ram_rom;
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

        public List<String> getColor() {
            return color;
        }

        public void setColor(List<String> color) {
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

        public List<String> getList_img() {
            return list_img;
        }

        public void setList_img(List<String> list_img) {
            this.list_img = list_img;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<String> getRam_rom() {
            return ram_rom;
        }

        public void setRam_rom(List<String> ram_rom) {
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
        private Product product;
        private String message;
        private int code;

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
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
