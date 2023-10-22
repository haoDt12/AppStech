package com.datn.shopsale.Interface;

import com.datn.shopsale.models.ResApi;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserService {
    @Multipart
    @POST("/api/registerUser")
    Call<ResApi> register(@Part("email") RequestBody email,
                          @Part("full_name") RequestBody fullName,
                          @Part("password") RequestBody password,
                          @Part("phone_number") RequestBody phoneNumber
    );

    @FormUrlEncoded
    @POST("/api/verifyOtpRegister")
    Call<ResApi> verifyOTPRegister(@Field("userTempId") String idUserTemp,
                                   @Field("otp") String otp
    );
}
