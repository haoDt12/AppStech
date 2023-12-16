package com.datn.shopsale.response;

import androidx.annotation.NonNull;

import com.datn.shopsale.models.User;

public class GetUserGoogleResponse {
    public static class Root {
        private User user;
        private String message;
        private String token;
        private int code;
        private String timestamp;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }


        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
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

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @NonNull
        @Override
        public String toString() {
            return "Root{" +
                    "user=" + user +
                    ", message='" + message + '\'' +
                    ", token='" + token + '\'' +
                    ", code=" + code +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }
    }
}
