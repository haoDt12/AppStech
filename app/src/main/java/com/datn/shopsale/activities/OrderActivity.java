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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
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
    private final int MONEY = 0;
    private final int E_BANKING = 1;
    private final int ZALO_PAY = 2;
    private int actionPAY = 0;
    private ListOder listOder;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private Toolbar toolbarOder;
    private TextView tvQuantity;
    private TextView tvTotal;
    private TextView tvShipPrice;
    private TextView tvSumMoney;
    private Spinner spinnerAddress;
    private Button btnOder;
    private ArrayList<Address> dataList = new ArrayList<>();
    private int sumMoney = 0;
    private String address;
    private Button btnMoney;
    private Button btnEBanking;
    private Button btnZaloPay;
    private static final int REQUEST_CODE = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        getDataAddress();
    }
    private void initView(){
        toolbarOder = (Toolbar) findViewById(R.id.toolbar_oder);
        spinnerAddress = (Spinner) findViewById(R.id.spinner_address);
        tvQuantity = (TextView) findViewById(R.id.tv_quantity);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvShipPrice = (TextView) findViewById(R.id.tv_ship_price);
        tvSumMoney = (TextView) findViewById(R.id.tv_sum_money);
        btnOder = (Button) findViewById(R.id.btn_oder);
        btnMoney = (Button) findViewById(R.id.btn_money);
        btnEBanking = (Button) findViewById(R.id.btn_e_banking);
        btnZaloPay = (Button) findViewById(R.id.btn_zalo_pay);
        RecyclerView recyclerView = findViewById(R.id.rcv_order);
        setSupportActionBar(toolbarOder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarOder.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
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
        onSelectPayAction(btnMoney);
        onMoney();
        onEBanking();
        onZaloPay();
        onPay();
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
                                preferenceManager.putString("addressOrder",address);
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
    private void onMoney(){
        btnMoney.setOnClickListener(v -> {
            actionPAY = MONEY;
            onSelectPayAction(btnMoney);
        });
    }
    private void onEBanking(){
        btnEBanking.setOnClickListener(v -> {
            actionPAY = E_BANKING;
            onSelectPayAction(btnEBanking);
        });
    }
    private void onZaloPay(){
        btnZaloPay.setOnClickListener(v -> {
            actionPAY = ZALO_PAY;
            onSelectPayAction(btnZaloPay);
        });
    }
    private void onPay(){
        btnOder.setOnClickListener(v -> {
            switch (actionPAY){
                case MONEY:
                    oderMoney();
                    break;
                case E_BANKING:
                    orderEBanking();
                    break;
                case ZALO_PAY:
                    orderZaloPay();
                    break;
            }
        });
    }

    private void oderMoney(){
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
            public void onResponse(@NonNull Call<ResApi> call, @NonNull Response<ResApi> response) {
                assert response.body() != null;
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
            public void onFailure(@NonNull Call<ResApi> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
    }
    private void orderEBanking(){
        Intent intent = new Intent(this, EBankingPayActivity.class);
        intent.putExtra("listOder",listOder);
        startActivityForResult(intent,REQUEST_CODE);
    }
    private void orderZaloPay(){

    }
    private void onSelectPayAction(Button btn){
        int backgroundColor = ContextCompat.getColor(this, R.color.white);
        int textColor = ContextCompat.getColor(this, R.color.black);

        ViewCompat.setBackgroundTintList(btnZaloPay, android.content.res.ColorStateList.valueOf(backgroundColor));
        ViewCompat.setBackgroundTintList(btnEBanking, android.content.res.ColorStateList.valueOf(backgroundColor));
        ViewCompat.setBackgroundTintList(btnMoney, android.content.res.ColorStateList.valueOf(backgroundColor));

        btnMoney.setTextColor(textColor);
        btnZaloPay.setTextColor(textColor);
        btnEBanking.setTextColor(textColor);

        ViewCompat.setBackgroundTintList(btn, android.content.res.ColorStateList.valueOf(textColor));
        btn.setTextColor(backgroundColor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                String resultValue = data.getStringExtra("action");
                assert resultValue != null;
                if(resultValue.equals("1")){
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        }
    }
}