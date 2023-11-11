package com.datn.shopsale.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.OrderAdapter;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.ListOder;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.request.OderRequest;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private ListOder listOder;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private TextView tvQuantity;
    private TextView tvTotal;
    private TextView tvShipPrice;
    private TextView tvSumMoney;
    private Button btnOder;
    private int sumMoney = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        onClickOder();
    }
    private void initView(){
        tvQuantity = (TextView) findViewById(R.id.tv_quantity);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvShipPrice = (TextView) findViewById(R.id.tv_ship_price);
        tvSumMoney = (TextView) findViewById(R.id.tv_sum_money);
        btnOder = (Button) findViewById(R.id.btn_oder);
        RecyclerView recyclerView = findViewById(R.id.rcv_order);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        Intent intent = getIntent();
        if (intent.hasExtra("listOder")) {
            listOder = (ListOder) intent.getSerializableExtra("listOder");
        }
        tvQuantity.setText(String.valueOf(listOder.getList().size()));
        tvShipPrice.setText("0đ");
        for (Cart item: listOder.getList()) {
            sumMoney += item.getPrice();
        }
        tvSumMoney.setText(String.valueOf(sumMoney));
        tvTotal.setText(String.valueOf(sumMoney));
        OrderAdapter adapter = new OrderAdapter(listOder);
        recyclerView.setAdapter(adapter);
    }
    private void onClickOder(){
        List<OderRequest.Product> listProduct = new ArrayList<>();
        for (Cart item: listOder.getList()) {
            listProduct.add(new OderRequest.Product(item.getProductId(),item.getColor(),item.getRam_rom(),item.getQuantity()));
        }
        OderRequest.Root request = new OderRequest.Root();
        request.setProduct(listProduct);
        request.setUserId(preferenceManager.getString("userId"));
        request.setAddress("123");
        btnOder.setOnClickListener(v -> {
            LoadingDialog.showProgressDialog(this, "Đang Tải");
            Call<ResApi> call = apiService.createOrder(preferenceManager.getString("token"),request);
            call.enqueue(new Callback<ResApi>() {
                @Override
                public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                    if(response.body().code == 1){
                        runOnUiThread(() -> {
                            Toast.makeText(OrderActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        });
                    }else {
                        runOnUiThread(() -> {
                            Toast.makeText(OrderActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onFailure(Call<ResApi> call, Throwable t) {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }
}