package com.datn.shopsale.modelsv2;

import java.io.Serializable;
import java.util.List;

public class ListDetailOrder implements Serializable {
    private Order order;
    private List<Product> listProduct;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<Product> getListProduct() {
        return listProduct;
    }

    public void setListProduct(List<Product> listProduct) {
        this.listProduct = listProduct;
    }
}
