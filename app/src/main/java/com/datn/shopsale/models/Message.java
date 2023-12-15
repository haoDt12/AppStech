package com.datn.shopsale.models;

public class Message {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    private int mType;

    public int getType() {
        return mType;
    }

    private String id;
    private Conversation conversation;
    private String senderID;
    private String receiverID;
    private String message;
    private String files;
    private String images;
    private String video;
    private String status;
    private boolean deleted;
    private String timestamp;

    public Message() {
    }

    public Message(String id, Conversation conversation, String senderID, String receiverID,
                   String message, String files, String images, String video, String status,
                   boolean deleted, String timestamp) {
        this.id = id;
        this.conversation = conversation;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.files = files;
        this.images = images;
        this.video = video;
        this.status = status;
        this.deleted = deleted;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
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

}
