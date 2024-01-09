package com.datn.shopsale.modelsv2;

import java.io.Serializable;
import java.util.List;

public class DataListOrder implements Serializable {
    private List<Product> list;

    public List<Product> getList() {
        return list;
    }

    public void setList(List<Product> list) {
        this.list = list;
    }
}
