package com.datn.shopsale.ui.dashboard.address.Address;

import java.util.ArrayList;

public class AddressCDW {
    public class City {
        private String name;
        private  int code;
        private ArrayList<District> districts;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public ArrayList<District> getDistricts() {
            return districts;
        }

        public void setDistricts(ArrayList<District> districts) {
            this.districts = districts;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    public class District {
        private String name;
        private  int code;
        private ArrayList<Ward> wards;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public ArrayList<Ward> getWards() {
            return wards;
        }

        public void setWards(ArrayList<Ward> wards) {
            this.wards = wards;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    public class Ward {
        private String name;
        private  int code;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
