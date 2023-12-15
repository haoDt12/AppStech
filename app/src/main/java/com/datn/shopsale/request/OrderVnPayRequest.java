package com.datn.shopsale.request;

import java.util.List;

public class OrderVnPayRequest {
    public static class Root{
        private String voucherId;
        private String userId;
        private List<OderRequest.Product> product;
        private String address;
        private String amount;
        private String bankCode;
        private String language;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<OderRequest.Product> getProduct() {
            return product;
        }

        public void setProduct(List<OderRequest.Product> product) {
            this.product = product;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getVoucherId() {
            return voucherId;
        }

        public void setVoucherId(String voucherId) {
            this.voucherId = voucherId;
        }
    }
}
