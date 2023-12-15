package com.datn.shopsale.Interface;

import com.datn.shopsale.response.GetConversationResponse;
import com.datn.shopsale.response.GetMessageResponse;

import java.util.ArrayList;

public interface LatestMessageCallback {
    void onLatestMessageLoaded(ArrayList<GetMessageResponse.Message> dataMessage);
}
