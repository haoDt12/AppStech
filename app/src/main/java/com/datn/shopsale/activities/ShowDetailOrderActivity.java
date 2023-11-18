package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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

        preferenceManager = new PreferenceManager(this);
        apiService = RetrofitConnection.getApiService();

        String orderId = getIntent().getStringExtra("orderId");

        getOrder(orderId);
        binding.imgBack.setOnClickListener(view -> {
            finish();
        });
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
                    } else if (response.body().order.status.equals("PayComplete")){
                        binding.tvOrderStatus.setText("Đã thanh toán");
                    } else if (response.body().order.status.equals("WaitingGet")){
                        binding.tvOrderStatus.setText("Chờ lấy hàng");
                    }
                    binding.tvOrderTotal.setText(formatCurrency(response.body().order.total+""));
                    binding.tvNameAddress.setText(response.body().order.addressId.name);
                    binding.tvPhoneAddress.setText(response.body().order.addressId.phone_number);
                    binding.tvCityAddress.setText(response.body().order.addressId.city);
                    binding.tvStreetAddress.setText(response.body().order.addressId.street);
                    dataProduct = response.body().order.product;
                    Log.d("wwwwww", "onResponse: "+dataProduct.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.rcvProductOfOrder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            adapter = new ListProductOfOrderAdapter(dataProduct,getApplicationContext());
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
    public String formatCurrency(String price) {
        // Tạo một đối tượng DecimalFormat với mẫu số mong muốn
        long number = Long.parseLong(price);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setGroupingSeparator('.'); // Set '.' as the grouping separator
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###.###", formatSymbols);
        String formattedNumber = decimalFormat.format(number);
        return formattedNumber;
    }
}