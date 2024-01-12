package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.Voucher;

public class GetVoucherByIdResponse extends BaseResponse{
    private Voucher voucher;

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}
