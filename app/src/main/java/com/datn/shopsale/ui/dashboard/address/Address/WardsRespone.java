package com.datn.shopsale.ui.dashboard.address.Address;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WardsRespone {
    @SerializedName("wards")
    private List<AddressCDW.Ward> wards;

    public List<AddressCDW.Ward> getWards() {
        return wards;
    }

    public void setWards(List<AddressCDW.Ward> wards) {
        this.wards = wards;
    }
}
