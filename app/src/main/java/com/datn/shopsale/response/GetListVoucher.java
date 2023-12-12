package com.datn.shopsale.response;

import java.io.Serializable;
import java.util.List;

public class GetListVoucher{
    public class ListVoucher implements Serializable {
        private String _id;
        private String title;
        private String content;
        private String price;
        private String fromDate;
        private String toDate;
        private String userId;
        private String status;
        private String idAll;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        public String getToDate() {
            return toDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIdAll() {
            return idAll;
        }

        public void setIdAll(String idAll) {
            this.idAll = idAll;
        }
    }

    public class Root implements Serializable{
        private String message;
        private List<ListVoucher> listVoucher;
        private int code;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<ListVoucher> getListVoucher() {
            return listVoucher;
        }

        public void setListVoucher(List<ListVoucher> listVoucher) {
            this.listVoucher = listVoucher;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

}
