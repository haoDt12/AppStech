package com.datn.shopsale.Interface;

import com.datn.shopsale.models.ResApi;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserService {
    @Multipart
    @POST("/api/registerUser")
    Call<ResApi> register(@Part("email") RequestBody email,
                          @Part("fullName") RequestBody fullName,
                          @Part("password") RequestBody password,
                          @Part("phoneNumber") RequestBody phoneNumber,
                          @Part("role") RequestBody role

    );
}
