package com.datn.shopsale.response;

import java.util.ArrayList;

public class GetNotificationResponse {
    public class Notification{
        private String _id;
        private String userId;
        private String content;
        private String title;
        private String date;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public class Root{
        public String message;
        public int code;
        public ArrayList<Notification> notification;
    }
}
