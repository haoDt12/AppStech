package com.datn.shopsale.models;

import com.datn.shopsale.response.GetListOrderResponse;

import java.util.ArrayList;
import java.util.Date;

public class Orders {
    private String id;
    private String userId;
    private ArrayList<GetListOrderResponse.Product> product;
    private String status;
    private String payment_method;
    private GetListOrderResponse.AddressId addressId;
    private int total;
    private Date date_time;
    public Orders() {
    }

    public Orders(String id, String userId, ArrayList<GetListOrderResponse.Product> product, String status, GetListOrderResponse.AddressId addressId, int total, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.product = product;
        this.status = status;
        this.addressId = addressId;
        this.total = total;
        this.payment_method = paymentMethod;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String paymentMethod) {
        this.payment_method = paymentMethod;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<GetListOrderResponse.Product> getProduct() {
        return product;
    }

    public void setProduct(ArrayList<GetListOrderResponse.Product> product) {
        this.product = product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GetListOrderResponse.AddressId getAddressId() {
        return addressId;
    }

    public void setAddressId(GetListOrderResponse.AddressId addressId) {
        this.addressId = addressId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Date getDate_time() {
        return date_time;
    }

    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }
}
