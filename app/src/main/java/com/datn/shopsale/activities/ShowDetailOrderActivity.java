package com.datn.shopsale.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ListOrderAdapter;
import com.datn.shopsale.adapter.ListProductOfOrderAdapter;
import com.datn.shopsale.databinding.ActivityShowDetailOrderBinding;
import com.datn.shopsale.models.Orders;
import com.datn.shopsale.response.GetListOrderResponse;
import com.datn.shopsale.response.GetOrderResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.PreferenceManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowDetailOrderActivity extends AppCompatActivity {
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private ActivityShowDetailOrderBinding binding;
    private ArrayList<GetOrderResponse.Product> dataProduct = new ArrayList<>();
    private ListProductOfOrderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowDetailOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarDetailOder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        binding.toolbarDetailOder.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        preferenceManager = new PreferenceManager(this);
        apiService = RetrofitConnection.getApiService();

        String orderId = getIntent().getStringExtra("orderId");

        getOrder(orderId);

        binding.btnCancelOrder.setOnClickListener(view -> {
            editOrder();
        });
    }

    private void editOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowDetailOrderActivity.this);
        builder.setTitle("Hủy đơn hàng");
        builder.setMessage("Bạn có thực sự muốn hủy đơn hàng?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String token = preferenceManager.getString("token");
                String orderId = getIntent().getStringExtra("orderId");
                String userId = binding.tvValueUserId.getText().toString().trim();
                String addressId = binding.tvValueAdressId.getText().toString().trim();
                Call<GetOrderResponse.Root> call = apiService.editOrderStatus(token,orderId,userId,addressId,"Cancel");
                call.enqueue(new Callback<GetOrderResponse.Root>() {
                    @Override
                    public void onResponse(Call<GetOrderResponse.Root> call, Response<GetOrderResponse.Root> response) {
                        if (response.body().code == 1){
                            Log.d("jjjjjjjjj", "onResponse: "+response.body().message);
                            dialogInterface.cancel();
                            getOrder(orderId);
                        } else {
                            Log.d("jjjjjjjjj", "onResponse: "+response.body().message);
                            dialogInterface.cancel();
                        }
                    }
                    @Override
                    public void onFailure(Call<GetOrderResponse.Root> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getOrder(String orderId){
        String token = preferenceManager.getString("token");

        Call<GetOrderResponse.Root> call = apiService.getOrderByOrderId(token,orderId);
        call.enqueue(new Callback<GetOrderResponse.Root>() {
            @Override
            public void onResponse(Call<GetOrderResponse.Root> call, Response<GetOrderResponse.Root> response) {
                if (response.body().code == 1) {
                    Log.d("jjjjjjjjj", "onResponse: "+response.body().order);

                    if (response.body().order.status.equals("InTransit")){
                        binding.tvOrderStatus.setText("Đang giao hàng");
                    } else if (response.body().order.status.equals("WaitConfirm")){
                        binding.tvOrderStatus.setText("Chờ xác nhận");
                        binding.btnCancelOrder.setVisibility(View.VISIBLE);
                    } else if (response.body().order.status.equals("PayComplete")){
                        binding.tvOrderStatus.setText("Đã thanh toán");
                    } else if (response.body().order.status.equals("WaitingGet")){
                        binding.tvOrderStatus.setText("Chờ lấy hàng");
                    } else if (response.body().order.status.equals("Cancel")){
                        binding.tvOrderStatus.setText("Đã hủy");
                        binding.btnCancelOrder.setVisibility(View.GONE);
                    }
                    binding.tvOrderTotal.setText(CurrencyUtils.formatCurrency(response.body().order.total+""));
                    binding.tvNameAddress.setText(response.body().order.addressId.name);
                    binding.tvPhoneAddress.setText(response.body().order.addressId.phone_number);
                    binding.tvCityAddress.setText(response.body().order.addressId.city);
                    binding.tvStreetAddress.setText(response.body().order.addressId.street);
                    binding.tvValueUserId.setText(response.body().order.userId);
                    binding.tvValueAdressId.setText(response.body().order.addressId._id);
                    dataProduct = response.body().order.product;
                    Log.d("wwwwww", "onResponse: "+dataProduct.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.rcvProductOfOrder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            adapter = new ListProductOfOrderAdapter(dataProduct,getApplicationContext(),response.body().order.status);
                            binding.rcvProductOfOrder.setAdapter(adapter);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<GetOrderResponse.Root> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}