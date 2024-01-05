package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.ListDetailOrder;

import java.util.List;

public class GetOrderResponseV2 extends BaseResponse{
    private List<ListDetailOrder> listDetailOrder;

    public List<ListDetailOrder> getListDetailOrder() {
        return listDetailOrder;
    }

    public void setListDetailOrder(List<ListDetailOrder> listDetailOrder) {
        this.listDetailOrder = listDetailOrder;
    }
}
