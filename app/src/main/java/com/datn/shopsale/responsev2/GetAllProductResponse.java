package com.datn.shopsale.responsev2;


import com.datn.shopsale.modelsv2.Product;

import java.util.List;

public class GetAllProductResponse extends BaseResponse{
    public List<Product> product;

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }
}
