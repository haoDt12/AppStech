package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.Address;

import java.util.List;

public class GetDeliveryAddressResponse extends BaseResponse{
    private List<Address> address;

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> addresses) {
        this.address = addresses;
    }
}
