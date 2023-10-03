package com.datn.shopsale.ui.dashboard.store.user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.datn.shopsale.R;

public class ListUsersActivity extends AppCompatActivity {
    private ImageButton imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        imgBack = (ImageButton) findViewById(R.id.img_back);
        imgBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }
}