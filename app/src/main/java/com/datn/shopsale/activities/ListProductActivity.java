package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;

import com.datn.shopsale.R;

public class ListProductActivity extends AppCompatActivity {
    private Toolbar toolbarListPro;
    private ImageView imgCart;
    private ImageView imgMore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);
        init();
    }
    private void init(){
        toolbarListPro = (Toolbar) findViewById(R.id.toolbar_list_pro);
        imgCart = (ImageView) findViewById(R.id.img_cart);
        imgMore = (ImageView) findViewById(R.id.img_more);
        setSupportActionBar(toolbarListPro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarListPro.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }
}