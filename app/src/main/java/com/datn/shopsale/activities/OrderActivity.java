package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datn.shopsale.adapter.OrderAdapter;
import com.datn.shopsale.R;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.ui.dashboard.address.AddressActivity;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout lnlAddressOrder;
    private RecyclerView recyclerView;
    private TextView tvName,tvPhone,tvCity,tvStreet;
    private static final int REQUEST_SELECT_ADDRESS = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        init();

    }
    private void init(){
        lnlAddressOrder = (LinearLayout) findViewById(R.id.lnl_address_order);
        recyclerView = findViewById(R.id.rcv_order);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvStreet = (TextView) findViewById(R.id.tv_street);
        loadSelectedAddressFromPreferences();
        lnlAddressOrder.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.lnl_address_order){
            Intent intent = new Intent(this, AddressActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_ADDRESS);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_ADDRESS && resultCode == RESULT_OK) {
            loadSelectedAddressFromPreferences();
        }
    }

    private void loadSelectedAddressFromPreferences() {
        SharedPreferences preferences = getSharedPreferences("SelectedAddress", Context.MODE_PRIVATE);
        String selectedAddressName = preferences.getString("selectedAddressName", "");
        String selectedAddressPhone = preferences.getString("selectedAddressPhone", "");
        String selectedAddressCity = preferences.getString("selectedAddressCity", "");
        String selectedAddressStreet = preferences.getString("selectedAddressStreet", "");

        // Update TextViews with the selected address from SharedPreferences
        tvName.setText(selectedAddressName);
        tvPhone.setText(selectedAddressPhone);
        tvCity.setText(selectedAddressCity);
        tvStreet.setText(selectedAddressStreet);
    }
}