package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.datn.shopsale.R;

public class VoucherActivity extends AppCompatActivity {
    private Toolbar toolbarVoucher;
    private RecyclerView rcvMyVoucher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        toolbarVoucher = (Toolbar) findViewById(R.id.toolbar_voucher);
        rcvMyVoucher = (RecyclerView) findViewById(R.id.rcv_my_voucher);

        setSupportActionBar(toolbarVoucher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarVoucher.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

    }
}