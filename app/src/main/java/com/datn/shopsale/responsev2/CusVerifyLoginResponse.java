package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.Customer;

public class CusVerifyLoginResponse extends BaseResponse{
    private Customer cus;
    private String token;

    public Customer getCus() {
        return cus;
    }

    public void setCus(Customer cus) {
        this.cus = cus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
