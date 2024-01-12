package com.datn.shopsale.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ListProductOfOrderAdapter;
import com.datn.shopsale.databinding.ActivityShowDetailOrderBinding;
import com.datn.shopsale.modelsv2.ListDetailOrder;
import com.datn.shopsale.modelsv2.Order;
import com.datn.shopsale.modelsv2.Product;
import com.datn.shopsale.request.CancelOrderRequest;
import com.datn.shopsale.responsev2.CancelOrderResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowDetailOrderActivity extends AppCompatActivity {
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private ActivityShowDetailOrderBinding binding;
    private ListDetailOrder listOrderDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowDetailOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarDetailOder);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        binding.toolbarDetailOder.setNavigationOnClickListener(v -> onBackPressed());
        preferenceManager = new PreferenceManager(this);
        apiService = RetrofitConnection.getApiService();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            listOrderDetail = (ListDetailOrder) bundle.getSerializable("detail_order");
            if (listOrderDetail != null) {
                getOrder(listOrderDetail);
            }
        }
        binding.btnCancelOrder.setOnClickListener(view -> {
            editOrder();
        });
    }

    private void editOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailOrderActivity.this);
        builder.setTitle("Hủy đơn hàng");
        builder.setMessage("Bạn có thực sự muốn hủy đơn hàng?");
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            String token = preferenceManager.getString("token");
            String orderId = listOrderDetail.getOrder().get_id();
            CancelOrderRequest request = new CancelOrderRequest();
            request.setOrderId(orderId);
            Call<CancelOrderResponse> call = apiService.cancelOrder(token, request);
            call.enqueue(new Callback<CancelOrderResponse>() {
                @Override
                public void onResponse(@NonNull Call<CancelOrderResponse> call, @NonNull Response<CancelOrderResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
                                dialogInterface.cancel();
                                AlertDialogUtil.showAlertDialogWithOk(ShowDetailOrderActivity.this,response.body().getMessage());
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> {
                                dialogInterface.cancel();
                                if (response.body().getMessage().equals("wrong token")) {
                                    CheckLoginUtil.gotoLogin(ShowDetailOrderActivity.this, response.body().getMessage());
                                } else {
                                    AlertDialogUtil.showAlertDialogWithOk(ShowDetailOrderActivity.this, response.body().getMessage());
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CancelOrderResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        dialogInterface.cancel();
                        AlertDialogUtil.showAlertDialogWithOk(ShowDetailOrderActivity.this,t.getMessage());
                    });;
                }
            });
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getOrder(ListDetailOrder listDetailOrder) {
        Order order = listDetailOrder.getOrder();
        List<Product> productList = listDetailOrder.getListProduct();
        switch (order.getStatus()) {
            case "InTransit":
                binding.tvOrderStatus.setText(getResources().getText(R.string.dang_giao_hang));
                break;
            case "WaitConfirm":
                binding.tvOrderStatus.setText(getResources().getText(R.string.doi_xac_nhan));
                binding.btnCancelOrder.setVisibility(View.VISIBLE);
                break;
            case "PayComplete":
                binding.tvOrderStatus.setText(getResources().getText(R.string.da_thanh_toan));
                break;
            case "WaitingGet":
                binding.tvOrderStatus.setText(getResources().getText(R.string.cho_lay_hang));
                break;
            case "Cancel":
                binding.tvOrderStatus.setText(getResources().getText(R.string.da_huy));
                binding.btnCancelOrder.setVisibility(View.GONE);
                break;
        }
        binding.tvOrderTotal.setText(CurrencyUtils.formatCurrency(order.getTotal_amount()));
        binding.tvPaymentMethod.setText(order.getPayment_methods());
        binding.tvNameAddress.setText(String.format("Người nhận: %s", order.getDelivery_address_id().getName()));
        binding.tvPhoneAddress.setText(String.format("Số điện thoại: %s", order.getDelivery_address_id().getPhone()));
        binding.tvCityAddress.setText(String.format("Địa chỉ nhận: %s", order.getDelivery_address_id().getCity()));
        binding.tvStreetAddress.setText(order.getDelivery_address_id().getStreet());
        binding.rcvProductOfOrder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ListProductOfOrderAdapter adapter = new ListProductOfOrderAdapter(productList, getApplicationContext(), order.getStatus());
        binding.rcvProductOfOrder.setAdapter(adapter);
    }
}