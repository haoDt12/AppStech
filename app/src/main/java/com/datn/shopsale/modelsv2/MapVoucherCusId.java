package com.datn.shopsale.modelsv2;

import java.io.Serializable;

public class MapVoucherCusId implements Serializable {
    private String _id;
    private String vocher_id;
    private String customer_id;
    private boolean is_used;
    private int __v;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getVocher_id() {
        return vocher_id;
    }

    public void setVocher_id(String vocher_id) {
        this.vocher_id = vocher_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public boolean isIs_used() {
        return is_used;
    }

    public void setIs_used(boolean is_used) {
        this.is_used = is_used;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}
