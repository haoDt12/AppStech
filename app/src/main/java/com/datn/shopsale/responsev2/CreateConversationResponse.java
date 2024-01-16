package com.datn.shopsale.responsev2;


import com.datn.shopsale.modelsv2.Conversation;

public class CreateConversationResponse extends BaseResponse {

    private Conversation conversation;

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
