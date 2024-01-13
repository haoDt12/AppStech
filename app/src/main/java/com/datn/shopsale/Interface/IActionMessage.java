package com.datn.shopsale.Interface;

import com.datn.shopsale.modelsv2.Conversation;

public interface IActionMessage {
    void doAction(String typeAction, String msgID, String status, Conversation conversation);
}
