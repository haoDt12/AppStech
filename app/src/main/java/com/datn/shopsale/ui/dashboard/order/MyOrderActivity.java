package com.datn.shopsale.ui.dashboard.order;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ViewPagerAdapter;
import com.datn.shopsale.databinding.ActivityMyOrderBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class MyOrderActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.datn.shopsale.databinding.ActivityMyOrderBinding binding = ActivityMyOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        binding.vpgMyOrder.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tlMyOrder, binding.vpgMyOrder, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Chờ xác nhận");
                    break;
                case 1:
                    tab.setText("Chờ lấy hàng");
                    break;
                case 2:
                    tab.setText("Đang giao");
                    break;
                case 3:
                    tab.setText("Đã thanh toán");
                    break;
                case 4:
                    tab.setText("Đã hủy");
                    break;
            }
        });
        tabLayoutMediator.attach();
        setSupportActionBar(binding.toolbarMyOder);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        binding.toolbarMyOder.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onClick(View view) {
    }
}