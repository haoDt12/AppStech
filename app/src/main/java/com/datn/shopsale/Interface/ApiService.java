package com.datn.shopsale.Interface;

import com.datn.shopsale.models.Address;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.FeedBack;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.models.ResponeFeedBack;
import com.datn.shopsale.models.ResponseCart;
import com.datn.shopsale.request.AddressRequest;
import com.datn.shopsale.request.EditPassRequest;
import com.datn.shopsale.request.OderRequest;
import com.datn.shopsale.request.OrderVnPayRequest;
import com.datn.shopsale.response.EditPasswordResponse;
import com.datn.shopsale.response.GetBannerResponse;
import com.datn.shopsale.response.GetConversationResponse;
import com.datn.shopsale.response.GetListCategoryResponse;
import com.datn.shopsale.response.GetListOrderResponse;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.response.GetListVoucher;
import com.datn.shopsale.response.GetMessageResponse;
import com.datn.shopsale.response.GetNotificationResponse;
import com.datn.shopsale.response.GetOrderResponse;
import com.datn.shopsale.response.GetPassResponse;
import com.datn.shopsale.response.GetPriceZaloPayResponse;
import com.datn.shopsale.response.GetProductResponse;
import com.datn.shopsale.response.GetUserByIdResponse;
import com.datn.shopsale.response.ResponseAddress;
import com.datn.shopsale.response.UserVerifyLoginResponse;
import com.datn.shopsale.response.VerifyOtpEditPassResponse;
import com.datn.shopsale.response.VnPayResponse;
import com.datn.shopsale.ui.dashboard.address.Address.AddressCDW;
import com.datn.shopsale.ui.dashboard.address.Address.DistrictRespone;
import com.datn.shopsale.ui.dashboard.address.Address.WardsRespone;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @FormUrlEncoded
    @POST("/api/getProductByIdCate")
    Call<GetListProductResponse.Root> getListProductByIdCate(@Header("Authorization") String token,
                                                             @Field("categoryId") String categoryId);

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
    @POST("/api/editOrder")
    Call<GetOrderResponse.Root> editOrderStatus(@Header("Authorization") String token,
                                                @Field("orderId") String orderId,
                                                @Field("userId") String userId,
                                                @Field("addressId") String addressId,
                                                @Field("status") String status);

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

    @POST("/api/getListBanner")
    Call<GetBannerResponse.Root> getListBanner(@Header("Authorization") String token
    );

    @POST("/api/getPublicNotification")
    Call<GetNotificationResponse.Root> getNotification(@Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("/api/getPrivateNotification")
    Call<GetNotificationResponse.Root> getNotificationPrivate(@Header("Authorization") String token,
                                                              @Field("userId") String id
    );

    @GET("/api/p/")
    Call<List<AddressCDW.City>> getCities();

    @GET("/api/p/{code}")
    Call<DistrictRespone> getDistrict(@Path("code") int code, @Query("depth") int depth);

    @GET("/api/d/{code}")
    Call<WardsRespone> getWard(@Path("code") int code, @Query("depth") int depth);

    @FormUrlEncoded
    @POST("/api/addFCM")
    Call<ResApi> addFCM(@Header("Authorization") String token,
                        @Field("userId") String id,
                        @Field("fcm") String fcm);

    @POST("/api/createPaymentUrl")
    Call<VnPayResponse> createOrderVnPay(@Header("Authorization") String token, @Body OrderVnPayRequest.Root request);

    @POST("/api/addFeedBack")
    Call<ResApi> addCmt(@Header("Authorization") String token, @Body FeedBack objFeedBack);

    @FormUrlEncoded
    @POST("/api/getFeedBackByProductId")
    Call<ResponeFeedBack> getFeedBackByProductId(@Header("Authorization") String token, @Field("productId") String productId);

    @FormUrlEncoded
    @POST("/api/getAllFeedBackByProductId")
    Call<ResponeFeedBack> getAllFeedBackByProductId(@Header("Authorization") String token, @Field("productId") String productId);

    @POST("/api/editPassword")
    Call<EditPasswordResponse> editPassword(@Header("Authorization") String token, @Body EditPassRequest request);

    @POST("/api/verifyOtpEditPass")
    Call<VerifyOtpEditPassResponse> sendOtpPassword(@Header("Authorization") String token, @Body EditPassRequest request);

    @FormUrlEncoded
    @POST("/api/getPassWord")
    Call<GetPassResponse> getPassWord(@Header("Authorization") String token, @Field("username") String username);

    @FormUrlEncoded
    @POST("/api/getVoucherByUserId")
    Call<GetListVoucher.Root> getListVoucher(@Header("Authorization") String token, @Field("userId") String userId);

    @POST("/api/getPriceZaloPay")
    Call<GetPriceZaloPayResponse> getPriceOrderZaloPay(@Header("Authorization") String token, @Body OderRequest.Root request);

    @POST("/api/creatOrderZaloPay")
    Call<ResApi> createOrderZaloPay(@Header("Authorization") String token, @Body OderRequest.Root request);

    @FormUrlEncoded
    @POST("/api/getConversationByIDUser")
    Call<GetConversationResponse.Root> getConversationByIDUser(@Header("Authorization") String token,
                                                               @Field("idUser") String idUser);

    @FormUrlEncoded
    @POST("/api/getMessageLatest")
    Call<GetMessageResponse.Root> getMessageLatest(@Header("Authorization") String token,
                                                   @Field("conversationIDs[]") ArrayList<String> conversationIDs);

    @FormUrlEncoded
    @POST("/api/getAnyUserById")
    Call<GetUserByIdResponse.Root> getAnyUserById(@Header("Authorization") String token,
                                                  @Field("userId") String userId);

    @Multipart
    @POST("/api/addMessage")
    Call<GetMessageResponse.ResponseMessage> addMessage(
            @Header("Authorization") String token,
            @Part("conversation") RequestBody conversation,
            @Part("senderId") RequestBody senderId,
            @Part("receiverId") RequestBody receiverId,
            @Part("message") RequestBody message,
            @Part MultipartBody.Part images,
            @Part MultipartBody.Part video
    );

    @FormUrlEncoded
    @POST("/api/getMessageByIDConversation")
    Call<GetMessageResponse.Root> getMessageByIDConversation(@Header("Authorization") String token,
                                                             @Field("conversationID") String conversationID);


}
