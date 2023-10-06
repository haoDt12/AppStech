package com.datn.shopsale.ui.dashboard.address;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.datn.shopsale.R;

public class AddressActivity extends AppCompatActivity {
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        imgBack = findViewById(R.id.img_back);

        imgBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }
}