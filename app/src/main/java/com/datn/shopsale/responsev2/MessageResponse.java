package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.Message;

public class MessageResponse extends BaseResponse {
    private Message dataMessage;


    public Message getMessages() {
        return dataMessage;
    }

    public void setMessages(Message message) {
        this.dataMessage = message;
    }
}
