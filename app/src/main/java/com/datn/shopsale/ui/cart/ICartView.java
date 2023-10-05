package com.datn.shopsale.ui.cart;

import com.datn.shopsale.models.Cart;

import java.util.List;

public interface ICartView {
    void getDataCartSuccess(List<Cart> list);

    void getDataCartFail(String message);
}
