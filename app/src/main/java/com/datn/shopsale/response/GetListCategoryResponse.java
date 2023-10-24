package com.datn.shopsale.response;

import java.util.ArrayList;

public class GetListCategoryResponse {
    public class Category{
        private String _id;
        private String title;
        private String date;
        private String img;

        public Category(String _id, String title, String img) {
            this._id = _id;
            this.title = title;
            this.img = img;
        }

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

        @Override
        public String toString() {
            return "Category{" +
                    "_id='" + _id + '\'' +
                    ", title='" + title + '\'' +
                    ", date='" + date + '\'' +
                    ", img='" + img + '\'' +
                    '}';
        }
    }

    public class Root{
        private ArrayList<Category> category;
        private String message;
        private int code;

        public ArrayList<Category> getCategory() {
            return category;
        }

        public void setCategory(ArrayList<Category> category) {
            this.category = category;
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

        @Override
        public String toString() {
            return "Root{" +
                    "category=" + category +
                    ", message='" + message + '\'' +
                    ", code=" + code +
                    '}';
        }
    }

}
