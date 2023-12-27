package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.DataGetProduct;

import java.util.List;

public class GetDetailProductResponse extends BaseResponse{
    private List<DataGetProduct> data;

    public List<DataGetProduct> getData() {
        return data;
    }

    public void setData(List<DataGetProduct> data) {
        this.data = data;
    }
}
