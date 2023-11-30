package com.datn.shopsale.utils;

public class GetImgIPAddress {
    public static String convertLocalhostToIpAddress(String originalUrl) {
        return originalUrl.replace("http://localhost:3000", Constants.URL_API);
    }
}
