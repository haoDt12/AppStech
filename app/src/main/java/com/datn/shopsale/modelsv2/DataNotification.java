package com.datn.shopsale.modelsv2;

public class DataNotification {
    private String _id;
    private Notification notification_id;
    private String customer_id;
    private String create_time;
    private int __v;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Notification getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(Notification notification_id) {
        this.notification_id = notification_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}
