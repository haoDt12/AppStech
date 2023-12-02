package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.datn.shopsale.R;

public class ReviewActivity extends AppCompatActivity {
    private Toolbar toolbarReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        init();
    }
    private void init(){
        toolbarReview = (Toolbar) findViewById(R.id.toolbar_review);
        setSupportActionBar(toolbarReview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarReview.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }
}