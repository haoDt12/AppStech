package com.datn.shopsale.ui.dashboard.address;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.datn.shopsale.R;
import com.datn.shopsale.adapter.AddressAdapter;
import com.datn.shopsale.adapter.UserAdapter;
import com.datn.shopsale.models.Address;
import com.datn.shopsale.models.User;
import com.datn.shopsale.ui.dashboard.setting.ChangePassActivity;

import java.util.ArrayList;

public class AddressActivity extends AppCompatActivity {
    private ImageView imgBack;
    private LinearLayout lnlAddAddress;
    private RecyclerView rcvAddress;
    private ArrayList<Address> dataList = new ArrayList<>();
    private AddressAdapter addressAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        imgBack = findViewById(R.id.img_back);
        lnlAddAddress = (LinearLayout) findViewById(R.id.lnl_add_address);
        rcvAddress = (RecyclerView) findViewById(R.id.rcv_address);

        imgBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
        lnlAddAddress.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), AddAddressActivity.class));
        });
        dataList.add(new Address("1", "Minh", "Nam Từ Liêm","0961xxxxxx"));
        dataList.add(new Address("2", "Minh", "Nam Từ Liêm","0961xxxxxx"));
        addressAdapter = new AddressAdapter(dataList);
        rcvAddress.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        rcvAddress.setAdapter(addressAdapter);
    }
}