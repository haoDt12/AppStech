package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.datn.shopsale.R;
import com.datn.shopsale.adapter.VoucherAdapter;
import com.datn.shopsale.models.Voucher;

import java.util.ArrayList;
import java.util.List;

public class VoucherActivity extends AppCompatActivity {
    private Toolbar toolbarVoucher;
    private RecyclerView rcvMyVoucher;
    private VoucherAdapter voucherAdapter;
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

        List<Voucher> voucherList = getVoucher();
        voucherAdapter = new VoucherAdapter(voucherList, this);
        rcvMyVoucher.setAdapter(voucherAdapter);
        rcvMyVoucher.setLayoutManager(new LinearLayoutManager(this));
    }
    private List<Voucher> getVoucher(){
        List<Voucher> list = new ArrayList<>();
        list.add(new Voucher("1", "Mã giảm giá","Giảm giá Điện thoại","15%","12-5-2023"));
        list.add(new Voucher("2", "Mã giảm giá","Giảm giá Laptop","5%","12-5-2023"));
        list.add(new Voucher("3", "Mã vận chuyển","Miễn phí vận chuyển với đơn","300k","12-5-2023"));
        list.add(new Voucher("4", "Mã vận chuyển","Miễn phí vận chuyển với đơn trên","1k","12-5-2023"));

        return  list;
    }
}