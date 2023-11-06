package com.datn.shopsale.ui.cart;

import com.datn.shopsale.models.Cart;

public interface IChangeQuantity {
    void IclickReduce(Cart objCart,int index);
    void IclickIncrease(Cart objCart,int index);
    void IclickCheckBox(Cart objCart,int index);
    void IclickCheckBox2(Cart objCart,int index);
}
