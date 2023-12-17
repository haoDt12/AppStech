package com.datn.shopsale.response;

import java.util.ArrayList;

public class GetMessageResponse {

    public static class Conversation {
        private String _id;
        private String name;
        private ArrayList<String> user;
        private String timestamp;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<String> getUser() {
            return user;
        }

        public void setUser(ArrayList<String> user) {
            this.user = user;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }

    public static class Message {
        private String _id;
        private Conversation conversation;
        private String senderId;
        private String receiverId;
        private String message;
        private ArrayList<String> filess;
        private ArrayList<String> images;
        private String video;
        private String status;
        private boolean deleted;
        private String timestamp;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public Conversation getConversation() {
            return conversation;
        }

        public void setConversation(Conversation conversation) {
            this.conversation = conversation;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderID(String senderID) {
            this.senderId = senderID;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverID) {
            this.receiverId = receiverID;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<String> getFiless() {
            return filess;
        }

        public void setFiless(ArrayList<String> files) {
            this.filess = files;
        }

        public ArrayList<String> getImages() {
            return images;
        }

        public void setImages(ArrayList<String> images) {
            this.images = images;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "_id='" + _id + '\'' +
                    ", conversation=" + conversation +
                    ", senderId='" + senderId + '\'' +
                    ", receiverId='" + receiverId + '\'' +
                    ", message='" + message + '\'' +
                    ", filess=" + filess +
                    ", images=" + images +
                    ", video='" + video + '\'' +
                    ", status='" + status + '\'' +
                    ", deleted=" + deleted +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }
    }

    public static class Root {
        private ArrayList<Message> dataMessage;
        private String message;
        private int code;

        public ArrayList<Message> getDataMessage() {
            return dataMessage;
        }

        public void setDataMessage(ArrayList<Message> dataMessage) {
            this.dataMessage = dataMessage;
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

    public class ResponseMessage {
        private MessageAdded dataMessage;
        private String message;
        private int code;
        private String time;

        public MessageAdded getDataMessage() {
            return dataMessage;
        }

        public void setDataMessage(MessageAdded dataMessage) {
            this.dataMessage = dataMessage;
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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    public static class MessageAdded {

        private String conversation;
        private String senderId;
        private String receiverId;
        private String message;
        private ArrayList<String> filess;
        private ArrayList<String> images;
        private String video;
        private String status;
        private boolean deleted;
        private String timestamp;
        private String _id;

        public String getConversation() {
            return conversation;
        }

        public void setConversation(String conversation) {
            this.conversation = conversation;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<String> getFiless() {
            return filess;
        }

        public void setFiless(ArrayList<String> filess) {
            this.filess = filess;
        }

        public ArrayList<String> getImages() {
            return images;
        }

        public void setImages(ArrayList<String> images) {
            this.images = images;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }
}
