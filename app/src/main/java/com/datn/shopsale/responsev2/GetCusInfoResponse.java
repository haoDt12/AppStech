package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.Customer;

public class GetCusInfoResponse extends BaseResponse{
    private Customer cus;

    public Customer getCus() {
        return cus;
    }

    public void setCus(Customer cus) {
        this.cus = cus;
    }
}
