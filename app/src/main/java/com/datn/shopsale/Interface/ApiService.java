package com.datn.shopsale.Interface;

import com.datn.shopsale.models.Address;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.models.ResponseCart;
import com.datn.shopsale.request.AddressRequest;
import com.datn.shopsale.request.OderRequest;
import com.datn.shopsale.response.GetListCategoryResponse;
import com.datn.shopsale.response.GetListOrderResponse;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.response.GetOrderResponse;
import com.datn.shopsale.response.GetProductResponse;
import com.datn.shopsale.response.GetUserByIdResponse;
import com.datn.shopsale.response.ResponseAddress;
import com.datn.shopsale.response.UserVerifyLoginResponse;

import okhttp3.MultipartBody;
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
    Call<ResApi> addToCart(@Header("Authorization") String token,
                           @Body Cart objCart
    );

    @FormUrlEncoded
    @POST("/api/getCartByCartIdUser")
    Call<ResponseCart> getDataCart(@Header("Authorization") String token,
                                   @Field("id") String id
    );

    @FormUrlEncoded
    @POST("/api/editCart")
    Call<ResApi> editCart(@Header("Authorization") String token,
                          @Field("userId") String id,
                          @Field("productId") String productId,
                          @Field("caculation") String caculation
    );

    @POST("/api/addAddress")
    Call<ResApi> addAddress(@Header("Authorization") String token,
                            @Body Address objAddress
    );

    @POST("/api/editAddress")
    Call<ResApi> editAddress(@Header("Authorization") String token,
                             @Body AddressRequest objAddress
    );

    @FormUrlEncoded
    @POST("/api/deleteAddress")
    Call<ResApi> deleteAddress(@Header("Authorization") String token,
                               @Field("userId") String id,
                               @Field("addressId") String addressId

    );

    @FormUrlEncoded
    @POST("/api/getUserById")
    Call<ResponseAddress.Root> getAddress(@Header("Authorization") String token,
                                          @Field("userId") String id
    );

    @POST("/api/createOrder")
    Call<ResApi> createOrder(@Header("Authorization") String token, @Body OderRequest.Root request);

    @FormUrlEncoded
    @POST("/api/getOrderByUserId")
    Call<GetListOrderResponse.Root> getOrderByUserId(@Header("Authorization") String token,
                                                     @Field("userId") String userId);

    @FormUrlEncoded
    @POST("/api/getOrderByOrderId")
    Call<GetOrderResponse.Root> getOrderByOrderId(@Header("Authorization") String token,
                                                  @Field("orderId") String orderId);

    @FormUrlEncoded
    @POST("/api/getProductById")
    Call<GetProductResponse.Root> getProductById(@Header("Authorization") String token,
                                                 @Field("productId") String productId);

    @FormUrlEncoded
    @POST("/api/getUserById")
    Call<GetUserByIdResponse.Root> getUserById(@Header("Authorization") String token,
                                               @Field("userId") String id
    );

    @Multipart
    @POST("/api/editUser")
    Call<ResApi> editUserImg(
            @Header("Authorization") String token,
            @Part("email") RequestBody email,
            @Part("full_name") RequestBody fullName,
            @Part("phone_number") RequestBody phoneNumber,
            @Part MultipartBody.Part file,
            @Part("userId") RequestBody userId
    );
    @Multipart
    @POST("/api/editUser")
    Call<ResApi> editUser(
            @Header("Authorization") String token,
            @Part("email") RequestBody email,
            @Part("full_name") RequestBody fullName,
            @Part("phone_number") RequestBody phoneNumber,
            @Part("userId") RequestBody userId
    );
    @FormUrlEncoded
    @POST("/api/addFCM")
    Call<ResApi> addFCM(@Header("Authorization") String token,
                                               @Field("userId") String id,
                                          @Field("fcm") String fcm);
}
