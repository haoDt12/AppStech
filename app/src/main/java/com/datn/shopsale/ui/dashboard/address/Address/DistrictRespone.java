package com.datn.shopsale.ui.dashboard.address.Address;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DistrictRespone {
    @SerializedName("districts")
    private List<AddressCDW.District> districts;

    public List<AddressCDW.District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<AddressCDW.District> districts) {
        this.districts = districts;
    }
}
