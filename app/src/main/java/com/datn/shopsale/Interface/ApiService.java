package com.datn.shopsale.Interface;

import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.request.AddAddressRequest;
import com.datn.shopsale.request.AddFcmRequest;
import com.datn.shopsale.request.CancelOrderRequest;
import com.datn.shopsale.request.ChangPassRequest;
import com.datn.shopsale.request.CreateConversationRequest;
import com.datn.shopsale.request.CreateOrderRequest;
import com.datn.shopsale.request.CusLoginRequest;
import com.datn.shopsale.request.CusVerifyLoginRequest;
import com.datn.shopsale.request.DeleteAddressRequest;
import com.datn.shopsale.request.EditAddressRequest;
import com.datn.shopsale.request.EditCusRequest;
import com.datn.shopsale.request.GetOrderByIdRequest;
import com.datn.shopsale.request.GetOrderByStatusRequest;
import com.datn.shopsale.request.GetProductByCateIdRequest;
import com.datn.shopsale.request.GetVoucherByIdRequest;
import com.datn.shopsale.request.RegisterCusRequest;
import com.datn.shopsale.request.SearchProductByNameRequest;
import com.datn.shopsale.responsev2.BaseResponse;
import com.datn.shopsale.responsev2.GetBannerResponse;
import com.datn.shopsale.responsev2.GetInfoUserResponse;
import com.datn.shopsale.responsev2.MessageResponse;
import com.datn.shopsale.responsev2.CreateConversationResponse;
import com.datn.shopsale.responsev2.GetNotificationResponse;
import com.datn.shopsale.response.GetPassResponse;
import com.datn.shopsale.response.GetUserGoogleResponse;
import com.datn.shopsale.response.VnPayResponse;
import com.datn.shopsale.responsev2.AddAddressResponse;
import com.datn.shopsale.responsev2.AddFcmResponse;
import com.datn.shopsale.responsev2.CancelOrderResponse;
import com.datn.shopsale.responsev2.ChangePassResponse;
import com.datn.shopsale.responsev2.CreateOrderResponse;
import com.datn.shopsale.responsev2.CusLoginResponse;
import com.datn.shopsale.responsev2.CusVerifyLoginResponse;
import com.datn.shopsale.responsev2.DeleteAddressResponse;
import com.datn.shopsale.responsev2.EditAddressResponse;
import com.datn.shopsale.responsev2.EditCusResponse;
import com.datn.shopsale.responsev2.FeedBackResponse;
import com.datn.shopsale.responsev2.GetAllProductResponse;
import com.datn.shopsale.responsev2.GetCategoryResponse;
import com.datn.shopsale.responsev2.GetCusInfoResponse;
import com.datn.shopsale.responsev2.GetDeliveryAddressResponse;
import com.datn.shopsale.responsev2.GetDetailProductResponse;
import com.datn.shopsale.responsev2.GetListConversationResponse;
import com.datn.shopsale.responsev2.GetListMessageResponse;
import com.datn.shopsale.responsev2.GetOrderResponseV2;
import com.datn.shopsale.responsev2.GetPriceZaloPayResponseV2;
import com.datn.shopsale.responsev2.GetVoucherByIdResponse;
import com.datn.shopsale.responsev2.GetVoucherResponse;
import com.datn.shopsale.responsev2.ProductCartResponse;
import com.datn.shopsale.responsev2.RegisterCustomerResponse;
import com.datn.shopsale.ui.dashboard.address.Address.AddressCDW;
import com.datn.shopsale.ui.dashboard.address.Address.DistrictRespone;
import com.datn.shopsale.ui.dashboard.address.Address.WardsRespone;

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
    @FormUrlEncoded
    @POST("/api/verifyOtpRegister")
    Call<ResApi> verifyOTPRegister(@Field("userTempId") String idUserTemp,
                                   @Field("otp") String otp
    );

    @FormUrlEncoded
    @POST("/api/loginWithGoogle")
    Call<GetUserGoogleResponse.Root> loginWithGoogle(@Field("email") String email,
                                                     @Field("id") String id,
                                                     @Field("displayName") String displayName,
                                                     @Field("expirationTime") String expirationTime,
                                                     @Field("photoUrl") String photoUrl
    );

    @POST("/apiv2/getListBanner")
    Call<GetBannerResponse> getListBanner(@Header("Authorization") String token
    );
    @GET("/api/p/")
    Call<List<AddressCDW.City>> getCities();

    @GET("/api/p/{code}")
    Call<DistrictRespone> getDistrict(@Path("code") int code, @Query("depth") int depth);

    @GET("/api/d/{code}")
    Call<WardsRespone> getWard(@Path("code") int code, @Query("depth") int depth);

    @FormUrlEncoded
    @POST("/apiv2/getPassWord")
    Call<GetPassResponse> getPassWord(@Header("Authorization") String token, @Field("username") String username);

    @POST("/apiv2/loginCustomer")
    Call<CusLoginResponse> loginCus(@Body CusLoginRequest request);

    @POST("/apiv2/verifyCusLogin")
    Call<CusVerifyLoginResponse> verifyCusLogin(@Body CusVerifyLoginRequest request);

    @POST("/apiv2/addFCM")
    Call<AddFcmResponse> addFCMCus(@Header("Authorization") String token,
                                   @Body AddFcmRequest request);

    @POST("/apiv2/getAllProduct")
    Call<GetAllProductResponse> getAllProduct(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("/apiv2/getDetailProduct")
    Call<GetDetailProductResponse> getDetailProduct(@Header("Authorization") String token,
                                                    @Field("productId") String productId);

    @POST("/apiv2/registerCustomer")
    Call<RegisterCustomerResponse> registerCustomer(@Body RegisterCusRequest request);

    @POST("/apiv2/getInfoCus")
    Call<GetCusInfoResponse> getInfoCus(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("/apiv2/getCartByIdCustomer")
    Call<ProductCartResponse> getProductCart(@Header("Authorization") String token,
                                             @Field("customer_id") String customer_id);

    @FormUrlEncoded
    @POST("/apiv2/addCart")
    Call<com.datn.shopsale.responsev2.BaseResponse> addProductCart(@Header("Authorization") String token,
                                                                   @Field("customer_id") String customer_id,
                                                                   @Field("product_id") String product_id,
                                                                   @Field("quantity") String quantity);

    @FormUrlEncoded
    @POST("/apiv2/updateCart")
    Call<com.datn.shopsale.responsev2.BaseResponse> updateCart(@Header("Authorization") String token,
                                                               @Field("customer_id") String customer_id,
                                                               @Field("product_id") String product_id,
                                                               @Field("calculation") String calculation);


    @POST("/apiv2/sendOtpEditCus")
    Call<EditCusResponse> sendOtpEditCus(@Header("Authorization") String token, @Body EditCusRequest request);

    @POST("/apiv2/editCus")
    Call<EditCusResponse> editCus(@Header("Authorization") String token, @Body EditCusRequest request);

    @POST("/apiv2/getDeliveryAddress")
    Call<GetDeliveryAddressResponse> getDeliveryAddress(@Header("Authorization") String token);

    @POST("/apiv2/addDeliveryAddress")
    Call<AddAddressResponse> addDeliveryAddress(@Header("Authorization") String token, @Body AddAddressRequest request);

    @POST("/apiv2/editDeliveryAddress")
    Call<EditAddressResponse> editDeliveryAddress(@Header("Authorization") String token, @Body EditAddressRequest request);

    @POST("/apiv2/deleteDeliveryAddress")
    Call<DeleteAddressResponse> deleteDeliveryAddress(@Header("Authorization") String token, @Body DeleteAddressRequest request);

    @POST("/apiv2/getVoucherByIdV2")
    Call<GetVoucherResponse> getVoucherByIdV2(@Header("Authorization") String token);

    @POST("/apiv2/createOrder")
    Call<CreateOrderResponse> createOrderV2(@Header("Authorization") String token, @Body CreateOrderRequest request);

    @POST("/apiv2/cancelOrder")
    Call<CancelOrderResponse> cancelOrder(@Header("Authorization") String token, @Body CancelOrderRequest request);

    @POST("/apiv2/getOrderByStatus")
    Call<GetOrderResponseV2> getOrderByStatus(@Header("Authorization") String token, @Body GetOrderByStatusRequest request);

    @FormUrlEncoded
    @POST("/apiv2/addFeedback")
    Call<FeedBackResponse> addFeedback(@Header("Authorization") String token,
                                       @Field("customer_id") String customer_id,
                                       @Field("product_id") String product_id,
                                       @Field("rating") String rating,
                                       @Field("comment") String comment);

    @FormUrlEncoded
    @POST("/apiv2/getFeedBackByProductId")
    Call<FeedBackResponse> getFeedBack(@Header("Authorization") String token,
                                       @Field("product_id") String product_id
    );

    @FormUrlEncoded
    @POST("/apiv2/getAllFeedBackByProductId")
    Call<FeedBackResponse> getAllFeedBack(@Header("Authorization") String token,
                                          @Field("product_id") String product_id
    );

    @POST("/apiv2/getCategory")
    Call<GetCategoryResponse> getCategory(@Header("Authorization") String token);

    @POST("/apiv2/getProductByCategoryId")
    Call<GetAllProductResponse> getProductByCategoryId(@Header("Authorization") String token, @Body GetProductByCateIdRequest request);

    @POST("/apiv2/getPriceOrderZaloPay")
    Call<GetPriceZaloPayResponseV2> getPriceOrderZaloPayV2(@Header("Authorization") String token, @Body CreateOrderRequest request);

    @POST("/apiv2/createOrderZaloPay")
    Call<CreateOrderResponse> createOrderZaloPay(@Header("Authorization") String token, @Body CreateOrderRequest request);

    @POST("/apiv2/createPaymentUrl")
    Call<VnPayResponse> createOrderVnPayV2(@Header("Authorization") String token, @Body CreateOrderRequest request);

    @POST("/apiv2/sendOtpEditPass")
    Call<ChangePassResponse> sendOtpEditPass(@Header("Authorization") String token, @Body ChangPassRequest request);

    @POST("/apiv2/editPass")
    Call<ChangePassResponse> editPass(@Header("Authorization") String token, @Body ChangPassRequest request);

    @POST("/apiv2/searchProductByName")
    Call<GetAllProductResponse> searchProductByName(@Header("Authorization") String token, @Body SearchProductByNameRequest request);

    @POST("/apiv2/createConversation")
    Call<CreateConversationResponse> createConversation(@Header("Authorization") String token,
                                                        @Body CreateConversationRequest request);

    @FormUrlEncoded
    @POST("/apiv2/getConversationByIDUser")
    Call<GetListConversationResponse> getConversationByIDUser(@Header("Authorization") String token,
                                                              @Field("idUser") String idUser);

    @FormUrlEncoded
    @POST("/apiv2/getMessageByIDConversation")
    Call<GetListMessageResponse> getMessageByIDConversation(@Header("Authorization") String token,
                                                            @Field("conversationID") String conversationID);

    @Multipart
    @POST("/apiv2/addMessage")
    Call<MessageResponse> addMessage(
            @Header("Authorization") String token,
            @Part("conversation_id") RequestBody conversation,
            @Part("sender_id") RequestBody senderId,
            @Part("message_type") RequestBody messageType,
            @Part("message") RequestBody message,
            @Part MultipartBody.Part images,
            @Part MultipartBody.Part video
    );

    @FormUrlEncoded
    @POST("/apiv2/updateStatusMessage")
    Call<MessageResponse> updateStatusMessage(@Header("Authorization") String token,
                                              @Field("msgID") String msgID,
                                              @Field("status") String status);

    @FormUrlEncoded
    @POST("/apiv2/deleteMessage")
    Call<BaseResponse> deleteMessage(@Header("Authorization") String token,
                                     @Field("userLoggedID") String userLoggedID,
                                     @Field("msgID") String msgID);
    @POST("/apiv2/getNotificationByUser")
    Call<GetNotificationResponse> getNotificationByUser(@Header("Authorization") String token);
    @POST("/apiv2/getVoucherByVoucherId")
    Call<GetVoucherByIdResponse> getVoucherByVoucherId(@Header("Authorization") String token, @Body GetVoucherByIdRequest request);
    @POST("/apiv2/getOrderByOrderId")
    Call<GetOrderResponseV2> getOrderByOrderId(@Header("Authorization") String token, @Body GetOrderByIdRequest request);

    @FormUrlEncoded
    @POST("/apiv2/getAnyUserById")
    Call<GetInfoUserResponse> getAnyUserById(@Header("Authorization") String token,
                                             @Field("userId") String userId);
}
