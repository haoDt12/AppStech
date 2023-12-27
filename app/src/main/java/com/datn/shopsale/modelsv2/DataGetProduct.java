package com.datn.shopsale.modelsv2;

import java.util.List;

public class DataGetProduct {
    private Product product;
    private List<Img> img;
    private List<Video> video;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Img> getImg() {
        return img;
    }

    public void setImg(List<Img> img) {
        this.img = img;
    }

    public List<Video> getVideo() {
        return video;
    }

    public void setVideo(List<Video> video) {
        this.video = video;
    }
}
