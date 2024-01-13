package com.datn.shopsale.utils;

import android.annotation.SuppressLint;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;

public class Constants {

    public static final String btnReduce = "reduce";
    public static final String idUserAdmin = "656f36de45994cafbb170c11";
    public static final String btnIncrease = "increase";
    public static final String URL_API = "https://stech.onrender.com";
    public static final String URL_API2 = "http://192.168.1.11:3000";
    public static final String ENCRYPTION_KEY = "df3b11a996831dee0ed12cc93bbc0532-32bd11ad-9a6c-4e2c-8fe9-330ee305b96a";
    public static final String ALGORITHM = "AES/CBC/PKCS7Padding";
    public static final String HASH_ALGORITHM = "SHA-1";
    public static final String HEX_CHAR = "0123456789ABCDEF";
    public static final String KEY_PREFERENCE_ACC = "logged_acc";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASS = "pass";
    public static final String KEY_REMEMBER = "remember";
    public static final String CONTEXT_LOGIN_FACEBOOK_EN = "Continue with Facebook";
    public static final String CONTEXT_LOGIN_FACEBOOK_VI = "Tiếp tục với Facebook";
    public static final String CONTEXT_LOGOUT_FACEBOOK_EN = "Log out";
    public static final String CONTEXT_LOGOUT_FACEBOOK_VI = "Đăng xuất";

    public static String getChatRoomId(String idUser1, String idUser2) {
        if (idUser1.hashCode() < idUser2.hashCode()) {
            return idUser1 + "_" + idUser2;
        } else {
            return idUser2 + "_" + idUser1;
        }
    }

    public static String getOtherId(List<String> list, String id) {
        if (list.get(0).equals(id)) {
            return list.get(1);
        } else {
            return list.get(0);
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String timestamptoString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

}
