package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.ProductCart;

import java.util.List;

public class ProductCartResponse extends BaseResponse{
    public List<ProductCart> productCart;

    public List<ProductCart> getProductCart() {
        return productCart;
    }

    public void setProductCart(List<ProductCart> productCart) {
        this.productCart = productCart;
    }
}
