package com.datn.shopsale.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.OrderAdapter;
import com.datn.shopsale.adapter.SpinnerAddressAdapter;
import com.datn.shopsale.models.Address;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.ListOder;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.request.OderRequest;
import com.datn.shopsale.response.ResponseAddress;
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
    private Spinner spinnerAddress;
    private Button btnOder;
    private ArrayList<Address> dataList = new ArrayList<>();
    private int sumMoney = 0;
    private String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        getDataAddress();
        onClickOder();
    }
    private void initView(){
        spinnerAddress = (Spinner) findViewById(R.id.spinner_address);
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
            sumMoney = sumMoney +  item.getPrice() * item.getQuantity();
        }
        tvSumMoney.setText(String.valueOf(sumMoney));
        tvTotal.setText(String.valueOf(sumMoney));
        OrderAdapter adapter = new OrderAdapter(listOder);
        recyclerView.setAdapter(adapter);
    }
    private void onClickOder(){
        btnOder.setOnClickListener(v -> {
            List<OderRequest.Product> listProduct = new ArrayList<>();
            for (Cart item: listOder.getList()) {
                listProduct.add(new OderRequest.Product(item.getProductId(),item.getColor(),item.getRam_rom(),item.getQuantity()));
            }
            OderRequest.Root request = new OderRequest.Root();
            request.setProduct(listProduct);
            request.setUserId(preferenceManager.getString("userId"));
            request.setAddress(address);
            LoadingDialog.showProgressDialog(this, "Đang Tải");
            Call<ResApi> call = apiService.createOrder(preferenceManager.getString("token"),request);
            call.enqueue(new Callback<ResApi>() {
                @Override
                public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                    if(response.body().code == 1){
                        runOnUiThread(() -> {
                            Toast.makeText(OrderActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            LoadingDialog.dismissProgressDialog();
                            setResult(Activity.RESULT_OK);
                            finish();
                        });
                    }else {
                        runOnUiThread(() -> {
                            Toast.makeText(OrderActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            LoadingDialog.dismissProgressDialog();
                        });
                    }
                }

                @Override
                public void onFailure(Call<ResApi> call, Throwable t) {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                    });
                }
            });
        });
    }
    private void getDataAddress() {
        String idUser = preferenceManager.getString("userId");

        Call<ResponseAddress.Root> call = apiService.getAddress(preferenceManager.getString("token"), idUser);
        call.enqueue(new Callback<ResponseAddress.Root>() {
            @Override
            public void onResponse(Call<ResponseAddress.Root> call, Response<ResponseAddress.Root> response) {
                if (response.body().getCode() == 1) {
                    runOnUiThread(() -> {
                        for (ResponseAddress.Address item : response.body().getUser().getAddress()) {
                            dataList.add(new Address(item.get_id(),item.getUserId() ,item.getName(), item.getCity(), item.getStreet(), item.getPhone_number()));
                        }
                        SpinnerAddressAdapter adapter = new SpinnerAddressAdapter(OrderActivity.this, android.R.layout.simple_spinner_item, dataList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerAddress.setAdapter(adapter);
                        spinnerAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                Address selectedItem = dataList.get(position);
                                address = selectedItem.get_id();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {
                            }
                        });
                    });

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
            @Override
            public void onFailure(Call<ResponseAddress.Root> call, Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}