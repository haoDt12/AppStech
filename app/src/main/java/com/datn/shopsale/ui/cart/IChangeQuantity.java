package com.datn.shopsale.ui.cart;

import com.datn.shopsale.modelsv2.ProductCart;

public interface IChangeQuantity {
    void IclickReduce(ProductCart objCart,int index);
    void IclickIncrease(ProductCart objCart,int index);
    void IclickCheckBox(ProductCart objCart,int index,boolean checkAll);
    void IclickCheckBox2(ProductCart objCart, int index);
}
