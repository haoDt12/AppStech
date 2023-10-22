package com.datn.shopsale.retrofit;

import com.datn.shopsale.Interface.UserService;
import com.datn.shopsale.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnection {

    public static UserService getUserService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.URL_DUCTUNG)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(UserService.class);
    }
}
