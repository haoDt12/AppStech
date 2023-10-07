package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.datn.shopsale.R;

public class DetailProductActivity extends AppCompatActivity {
    private ImageButton imgBack;
    private Button btnDanhgia;
    private Button btnAddToCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        imgBack = (ImageButton) findViewById(R.id.img_back);
        btnDanhgia = (Button) findViewById(R.id.btn_danhgia);
        btnAddToCart = (Button) findViewById(R.id.btn_addToCart);

        imgBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
        btnDanhgia.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),ReviewActivity.class));
        });
        btnAddToCart.setOnClickListener(view -> {
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }
}