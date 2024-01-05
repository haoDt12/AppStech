package com.datn.shopsale.modelsv2;

import java.io.Serializable;

public class Order implements Serializable {
    private String _id;
    private MapVoucherCusId map_voucher_cus_id;
    private Customer customer_id;
    private Employee employee_id;
    private DeliveryAddress delivery_address_id;
    private String status;
    private String total_amount;
    private String create_time;
    private String payment_methods;
    private int __v;

    public String getPayment_methods() {
        return payment_methods;
    }

    public void setPayment_methods(String payment_methods) {
        this.payment_methods = payment_methods;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public MapVoucherCusId getMap_voucher_cus_id() {
        return map_voucher_cus_id;
    }

    public void setMap_voucher_cus_id(MapVoucherCusId map_voucher_cus_id) {
        this.map_voucher_cus_id = map_voucher_cus_id;
    }

    public Customer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Customer customer_id) {
        this.customer_id = customer_id;
    }

    public Employee getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(Employee employee_id) {
        this.employee_id = employee_id;
    }

    public DeliveryAddress getDelivery_address_id() {
        return delivery_address_id;
    }

    public void setDelivery_address_id(DeliveryAddress delivery_address_id) {
        this.delivery_address_id = delivery_address_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
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
