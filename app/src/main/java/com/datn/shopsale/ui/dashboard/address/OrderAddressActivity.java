package com.datn.shopsale.ui.dashboard.address;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.AddressAdapter;
import com.datn.shopsale.adapter.OrderAddressAdapter;
import com.datn.shopsale.adapter.ProductAdapter;
import com.datn.shopsale.models.Address;
import com.datn.shopsale.response.ResponseAddress;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAddressActivity extends AppCompatActivity {
    private ArrayList<Address> dataList = new ArrayList<>();
    PreferenceManager preferenceManager;
    private ApiService apiService;
    private OrderAddressAdapter adapter;
    private Toolbar toolbarAddress;
    private RecyclerView rcvOrderaddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_address);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        toolbarAddress = (Toolbar) findViewById(R.id.toolbar_address);
        rcvOrderaddress = (RecyclerView) findViewById(R.id.rcv_Orderaddress);

        setSupportActionBar(toolbarAddress);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarAddress.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        getDataAddress();
    }
    private void getDataAddress() {
        dataList.clear();
        String idUser = preferenceManager.getString("userId");

        Call<ResponseAddress.Root> call = apiService.getAddress(preferenceManager.getString("token"), idUser);
        call.enqueue(new Callback<ResponseAddress.Root>() {
            @Override
            public void onResponse(Call<ResponseAddress.Root> call, Response<ResponseAddress.Root> response) {
                if (response.body().getCode() == 1) {
                    for (ResponseAddress.Address item : response.body().getUser().getAddress()) {
                        dataList.add(new Address(item.get_id(), item.getUserId(), item.getName(), item.getCity(), item.getStreet(), item.getPhone_number()));
                    }
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            adapter = new OrderAddressAdapter(dataList,OrderAddressActivity.this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderAddressActivity.this, RecyclerView.VERTICAL, false);
                            rcvOrderaddress.setLayoutManager(linearLayoutManager);
                            rcvOrderaddress.setAdapter(adapter);
                        }
                    });
                } else {
                    Toast.makeText(OrderAddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseAddress.Root> call, Throwable t) {
                Toast.makeText(OrderAddressActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}