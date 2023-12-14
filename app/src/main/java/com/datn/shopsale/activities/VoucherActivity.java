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
import com.datn.shopsale.response.GetListVoucher;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.List;

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
        Log.d("zzz", "onCreate: " + action);
        setSupportActionBar(toolbarVoucher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarVoucher.setNavigationOnClickListener(v -> onBackPressed());
        getListVoucher();

    }
    private void getListVoucher(){
        LoadingDialog.showProgressDialog(this,"Loading...");
        Call<GetListVoucher.Root> call = apiService.getListVoucher(preferenceManager.getString("token"),preferenceManager.getString("userId"));
        call.enqueue(new Callback<GetListVoucher.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetListVoucher.Root> call, @NonNull Response<GetListVoucher.Root> response) {
                assert response.body() != null;
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    if(response.body().getCode() == 1){
                        List<GetListVoucher.ListVoucher> voucherList = response.body().getListVoucher();
                        voucherAdapter = new VoucherAdapter(voucherList, VoucherActivity.this,action);
                        rcvMyVoucher.setAdapter(voucherAdapter);
                        rcvMyVoucher.setLayoutManager(new LinearLayoutManager(VoucherActivity.this));
                    }else {
                        AlertDialogUtil.showAlertDialogWithOk(VoucherActivity.this,response.body().getMessage());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GetListVoucher.Root> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(VoucherActivity.this,t.getMessage());
                });
            }
        });
    }
}