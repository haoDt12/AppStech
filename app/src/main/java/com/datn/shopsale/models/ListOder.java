package com.datn.shopsale.models;

import java.io.Serializable;
import java.util.List;

public class ListOder implements Serializable {
    private List<Cart> list;

    public List<Cart> getList() {
        return list;
    }

    public void setList(List<Cart> list) {
        this.list = list;
    }
}
