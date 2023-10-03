package com.datn.shopsale.retrofit;

import com.datn.shopsale.Interface.UserService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnection {
    private static final String URL = "http://192.168.250.85:3000";

    public static UserService getUserService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(UserService.class);
    }
}
