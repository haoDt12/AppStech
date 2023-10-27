package com.datn.shopsale.Interface;

import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.CartRequest;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.response.GetListCategoryResponse;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.response.ResApiNew;
import com.datn.shopsale.response.UserVerifyLoginResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    // Register
    @Multipart
    @POST("/api/registerUser")
    Call<ResApi> register(@Part("email") RequestBody email,
                          @Part("full_name") RequestBody fullName,
                          @Part("password") RequestBody password,
                          @Part("phone_number") RequestBody phoneNumber
    );

    // Verify OTP SignUp
    @FormUrlEncoded
    @POST("/api/verifyOtpRegister")
    Call<ResApi> verifyOTPRegister(@Field("userTempId") String idUserTemp,
                                   @Field("otp") String otp
    );

    // SignIn
    @FormUrlEncoded
    @POST("/api/loginUser")
    Call<ResApi> signin(@Field("username") String username,
                        @Field("password") String passwd
    );

    @FormUrlEncoded
    @POST("/api/verifyOtpLogin")
    Call<UserVerifyLoginResponse.Root> verifyOTPSignIn(@Field("userId") String idUserTemp,
                                                  @Field("otp") String otp
    );
    @POST("/api/getListCategory")
    Call<GetListCategoryResponse.Root> getListCategory(@Header("Authorization") String token);
    @POST("/api/getListProduct")
    Call<GetListProductResponse.Root> getListProduct(@Header("Authorization") String token);

    @POST("/api/addCart")
    Call<ResApiNew> addToCart(@Header("Authorization") String token,
                              @Body CartRequest.Root cartRequest
                         );
}
