package com.datn.shopsale.utils;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;

public class Constants {
    public enum STATUS_CART {
        DEFAULT(1),
        DELETE(0),
        DONE(2);
        private final int value;

        STATUS_CART(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }

    public static final String btnReduce = "reduce";
    public static final String idUserAdmin = "654b1ca8a39405e39cad703b";
    public static final String btnIncrease = "increase";
    public static final String URL_API = "http://192.168.68.115:3000";
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
    public static String getOtherId(List<String> list,String id){
        if(list.get(0).equals(id)){
            return list.get(1);
        }else {
            return list.get(0);
        }
    }
    public static String timestamptoString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

}
