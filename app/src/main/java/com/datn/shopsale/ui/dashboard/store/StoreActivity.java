package com.datn.shopsale.ui.dashboard.store;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.datn.shopsale.R;
import com.datn.shopsale.ui.dashboard.store.user.ListUsersActivity;

public class StoreActivity extends AppCompatActivity {
    private LinearLayout idListUsers;
    private ImageButton imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        imgBack = findViewById(R.id.img_back);
        idListUsers = findViewById(R.id.id_list_users);
        idListUsers.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ListUsersActivity.class));
        });

        imgBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }
}