package com.datn.shopsale.utils;

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

    public static final String URL_DUCTUNG = "http://192.168.250.85:3000";
    public static final String URL_VUNG = "http://192.168.0.105:3000";
    public static final String HEX_CHAR = "0123456789ABCDEF";
    public static final String KEY_PREFERENCE_ACC = "logged_acc";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASS = "pass";
    public static final String KEY_REMEMBER = "remember";
    public static final String CONTEXT_LOGIN_FACEBOOK_EN = "Continue with Facebook";
    public static final String CONTEXT_LOGIN_FACEBOOK_VI = "Tiếp tục với Facebook";
    public static final String CONTEXT_LOGOUT_FACEBOOK_EN = "Log out";
    public static final String CONTEXT_LOGOUT_FACEBOOK_VI = "Đăng xuất";

}
