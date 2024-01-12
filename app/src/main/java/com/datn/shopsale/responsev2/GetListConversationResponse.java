package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.Conversation;
import com.datn.shopsale.modelsv2.Product;

import java.util.List;

public class GetListConversationResponse extends BaseResponse {
    private List<Conversation> conversation;

    public List<Conversation> getConversations() {
        return conversation;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversation = conversations;
    }
}
