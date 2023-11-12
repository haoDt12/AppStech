package com.datn.shopsale.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class ChatRoomModal implements Serializable {
    String chatRoomId;
    List<String> userId;
    Timestamp lastMessageTimestamp;
    String lastMessage;
    String idUserOfLastMessage;
    String avatarUser;
    String nameUser;
    String lastImage;
    public ChatRoomModal() {
    }

    public ChatRoomModal(String chatRoomId, List<String> userId, Timestamp lastMessageTimestamp, String lastMessage, String idUserOfLastMessage, String avatarUser,String nameUser,String lastImage) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessage = lastMessage;
        this.idUserOfLastMessage = idUserOfLastMessage;
        this.avatarUser = avatarUser;
        this.nameUser = nameUser;
        this.lastImage = lastImage;
    }

    public String getLastImage() {
        return lastImage;
    }

    public void setLastImage(String lastImage) {
        this.lastImage = lastImage;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getAvatarUser() {
        return avatarUser;
    }

    public void setAvatarUser(String avatarUser) {
        this.avatarUser = avatarUser;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getIdUserOfLastMessage() {
        return idUserOfLastMessage;
    }

    public void setIdUserOfLastMessage(String idUserOfLastMessage) {
        this.idUserOfLastMessage = idUserOfLastMessage;
    }
}
