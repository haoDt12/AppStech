package com.datn.shopsale.modelsv2;

import androidx.annotation.NonNull;

public class Conversation {
    private String conversation_id;
    private String sender_id;
    private String message;
    private String message_type;
    private String idMsg;
    private String status;
    private String creator_id;
    private String receive_id;
    private String msg_deleted_at;
    private String created_at;
    private String updated_at;
    private String deleted_at;
    private String userID;
    private String name;
    private String avatar;
    private String email;
    private String phone;

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getIdMsg() {
        return idMsg;
    }

    public void setIdMsg(String idMsg) {
        this.idMsg = idMsg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getReceive_id() {
        return receive_id;
    }

    public void setReceive_id(String receive_id) {
        this.receive_id = receive_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    @NonNull
    @Override
    public String toString() {
        return "Conversation{" +
                "conversation_id='" + conversation_id + '\'' +
                ", sender_id='" + sender_id + '\'' +
                ", message='" + message + '\'' +
                ", message_type='" + message_type + '\'' +
                ", idMsg='" + idMsg + '\'' +
                ", status='" + status + '\'' +
                ", creator_id='" + creator_id + '\'' +
                ", receive_id='" + receive_id + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", deleted_at='" + deleted_at + '\'' +
                ", userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public String getMsg_deleted_at() {
        return msg_deleted_at;
    }

    public void setMsg_deleted_at(String msg_deleted_at) {
        this.msg_deleted_at = msg_deleted_at;
    }
}
