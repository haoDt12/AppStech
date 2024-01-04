package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.MapVoucherCus;

import java.util.List;

public class GetVoucherResponse extends BaseResponse{
    public List<MapVoucherCus> voucher;

    public List<MapVoucherCus> getVoucher() {
        return voucher;
    }

    public void setVoucher(List<MapVoucherCus> voucher) {
        this.voucher = voucher;
    }
}
