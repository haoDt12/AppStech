package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.DataNotification;

import java.util.List;

public class GetNotificationResponse extends BaseResponse{
    List<DataNotification> data;

    public List<DataNotification> getData() {
        return data;
    }

    public void setData(List<DataNotification> data) {
        this.data = data;
    }
}
