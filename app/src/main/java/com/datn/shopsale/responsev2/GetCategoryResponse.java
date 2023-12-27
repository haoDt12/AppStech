package com.datn.shopsale.responsev2;

import com.datn.shopsale.modelsv2.Category;

import java.util.List;

public class GetCategoryResponse extends BaseResponse{
    List<Category> category;

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }
}
