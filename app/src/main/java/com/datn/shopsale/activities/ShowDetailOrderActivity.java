package com.datn.shopsale.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.datn.shopsale.response.GetOrderResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
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
//        assert bundle != null;
        if (bundle != null) {
            ListDetailOrder listOrderDetail = (ListDetailOrder) bundle.getSerializable("detail_order");
            if (listOrderDetail != null) {
                getOrder(listOrderDetail);
            }
        }
        binding.btnCancelOrder.setOnClickListener(view -> {
//            editOrder();
        });
    }

    private void editOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailOrderActivity.this);
        builder.setTitle("Hủy đơn hàng");
        builder.setMessage("Bạn có thực sự muốn hủy đơn hàng?");
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            String token = preferenceManager.getString("token");
            String orderId = getIntent().getStringExtra("orderId");
            String userId = binding.tvValueUserId.getText().toString().trim();
            String addressId = binding.tvValueAdressId.getText().toString().trim();
            Call<GetOrderResponse.Root> call = apiService.editOrderStatus(token, orderId, userId, addressId, "Cancel");
            call.enqueue(new Callback<GetOrderResponse.Root>() {
                @Override
                public void onResponse(@NonNull Call<GetOrderResponse.Root> call, @NonNull Response<GetOrderResponse.Root> response) {
                    if (response.body() != null) {
                        if (response.body().code == 1) {
                            Log.d("jjjjjjjjj", "onResponse: " + response.body().message);
                            dialogInterface.cancel();
//                        getOrder(orderId);
                        } else {
                            Log.d("jjjjjjjjj", "onResponse: " + response.body().message);
                            dialogInterface.cancel();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GetOrderResponse.Root> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    dialogInterface.cancel();
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