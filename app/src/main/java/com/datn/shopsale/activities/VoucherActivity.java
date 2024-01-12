package com.datn.shopsale.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.VoucherAdapter;
import com.datn.shopsale.modelsv2.MapVoucherCus;
import com.datn.shopsale.responsev2.GetVoucherResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherActivity extends AppCompatActivity {
    private Toolbar toolbarVoucher;
    private RecyclerView rcvMyVoucher;
    private VoucherAdapter voucherAdapter;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private int action = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        toolbarVoucher = (Toolbar) findViewById(R.id.toolbar_voucher);
        rcvMyVoucher = (RecyclerView) findViewById(R.id.rcv_my_voucher);
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        if(getIntent().hasExtra("action")){
            action = getIntent().getIntExtra("action",0);
        }
        setSupportActionBar(toolbarVoucher);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarVoucher.setNavigationOnClickListener(v -> onBackPressed());
        getListVoucher();

    }
    private void getListVoucher(){
        LoadingDialog.showProgressDialog(this,"Loading...");
        Call<GetVoucherResponse> call = apiService.getVoucherByIdV2(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetVoucherResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetVoucherResponse> call, @NonNull Response<GetVoucherResponse> response) {
                assert response.body() != null;
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    if(response.body().getCode() == 1){
                        List<MapVoucherCus> voucherList = response.body().getVoucher();
                        voucherAdapter = new VoucherAdapter(voucherList, VoucherActivity.this,action);
                        rcvMyVoucher.setAdapter(voucherAdapter);
                        rcvMyVoucher.setLayoutManager(new LinearLayoutManager(VoucherActivity.this));
                    }else {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(VoucherActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(VoucherActivity.this, response.body().getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GetVoucherResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(VoucherActivity.this,t.getMessage());
                });
            }
        });
    }
}