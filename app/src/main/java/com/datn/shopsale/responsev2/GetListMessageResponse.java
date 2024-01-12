package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.Message;

import java.util.List;

public class GetListMessageResponse extends BaseResponse {
    private List<Message> dataMessage;

    public List<Message> getMessages() {
        return dataMessage;
    }

    public void setMessages(List<Message> messages) {
        this.dataMessage = messages;
    }
}
