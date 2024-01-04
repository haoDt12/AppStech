package com.datn.shopsale.request;

import com.datn.shopsale.modelsv2.ListOrder;

import java.util.List;

public class CreateOrderRequest {
    private List<ListOrder> list_order;
    private String map_voucher_cus_id;
    private String employee_id;
    private String delivery_address_id;

    public List<ListOrder> getList_order() {
        return list_order;
    }

    public void setList_order(List<ListOrder> list_order) {
        this.list_order = list_order;
    }

    public String getMap_voucher_cus_id() {
        return map_voucher_cus_id;
    }

    public void setMap_voucher_cus_id(String map_voucher_cus_id) {
        this.map_voucher_cus_id = map_voucher_cus_id;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public String getDelivery_address_id() {
        return delivery_address_id;
    }

    public void setDelivery_address_id(String delivery_address_id) {
        this.delivery_address_id = delivery_address_id;
    }
}
