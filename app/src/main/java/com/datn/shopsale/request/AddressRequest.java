package com.datn.shopsale.request;

public class AddressRequest {
    private String name;
    private String city;
    private String street;
    private String phone_number;
    private String addressId;

    public AddressRequest() {
    }

    public AddressRequest(String name, String city, String street, String phone_number, String addressId) {
        this.name = name;
        this.city = city;
        this.street = street;
        this.phone_number = phone_number;
        this.addressId = addressId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
}
