package com.datn.shopsale.ui.cart;

import com.datn.shopsale.models.Cart;
import com.datn.shopsale.modelsv2.ProductCart;

import java.util.List;

public interface IChangeQuantity {
    void IclickReduce(ProductCart objCart,int index);
    void IclickIncrease(ProductCart objCart,int index);
    void IclickCheckBox(ProductCart objCart,int index);
    void IclickCheckBox2(ProductCart objCart, int index);
}
