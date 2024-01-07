package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.FeedBack;

import java.util.List;

public class FeedBackResponse extends BaseResponse{
    public List<FeedBack> listFeedBack;

    public List<FeedBack> getListFeedBack() {
        return listFeedBack;
    }

    public void setListFeedBack(List<FeedBack> listFeedBack) {
        this.listFeedBack = listFeedBack;
    }
}
