package com.datn.shopsale.response;

import java.util.ArrayList;

public class GetBannerResponse {

    public static class Banner{
        public String _id;
        public String img;

        public Banner(String _id, String img) {
            this._id = _id;
            this.img = img;
        }

        public Banner(String imageUrl) {
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public class Root{
        public String message;
        public int code;
        public ArrayList<Banner> banner;
    }
}
