package com.datn.shopsale.modelsv2;

import java.util.List;

public class DataOrder {
    public List<DetailOrder> detailOrder;

    public List<DetailOrder> getDetailOrder() {
        return detailOrder;
    }

    public void setDetailOrder(List<DetailOrder> detailOrder) {
        this.detailOrder = detailOrder;
    }
}
