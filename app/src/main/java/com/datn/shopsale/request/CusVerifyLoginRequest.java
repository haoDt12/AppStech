package com.datn.shopsale.request;

public class CusVerifyLoginRequest {
    private String cusId;
    private String otp;

    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
