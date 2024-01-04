package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.DataOrder;

import java.util.List;

public class GetOrderResponseV2 extends BaseResponse{
    public List<DataOrder> dataOrder;

    public List<DataOrder> getDataOrder() {
        return dataOrder;
    }

    public void setDataOrder(List<DataOrder> dataOrder) {
        this.dataOrder = dataOrder;
    }
}
