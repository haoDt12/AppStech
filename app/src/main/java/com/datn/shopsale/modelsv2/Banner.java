package com.datn.shopsale.modelsv2;

import java.io.Serializable;

public class Banner implements Serializable {
    private String _id;
    private String admin_id;
    private String img;

    public Banner(String _id, String admin_id, String img) {
        this._id = _id;
        this.admin_id = admin_id;
        this.img = img;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
