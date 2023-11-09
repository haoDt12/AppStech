package com.datn.shopsale.ui.dashboard.address;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.DetailProductActivity;
import com.datn.shopsale.models.Address;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CITY_SELECTION = 1; // Mã yêu cầu cho CitySelectionActivity
    private ImageButton imgBack;
    private EditText edName,edPhoneNumber,edCity,edStreet;
    private Button btnSave;
    private ApiService apiService;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        init();
    }

    private void init(){
        imgBack = (ImageButton) findViewById(R.id.img_back);
        edName = (EditText) findViewById(R.id.ed_name);
        edPhoneNumber = (EditText) findViewById(R.id.ed_phone_number);
        edCity = (EditText) findViewById(R.id.ed_city);
        edStreet = (EditText) findViewById(R.id.ed_street);
        btnSave = (Button) findViewById(R.id.btn_save);

        imgBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.img_back){
            super.onBackPressed();
        } else if (view.getId() == R.id.btn_save) {
            if (validate()) {
                addAddress();
            }

        }
    }
    private void addAddress() {
        String token = preferenceManager.getString("token");
        String idUser = preferenceManager.getString("userId");
        Address address = new Address();
        String name = edName.getText().toString().trim();
        String phone = edPhoneNumber.getText().toString().trim();
        String city = edCity.getText().toString().trim();
        String street = edStreet.getText().toString().trim();


        address.setUserId(idUser);
        address.setPhone_number(phone);
        address.setName(name);
        address.setCity(city);
        address.setStreet(street);

        try {
            Call<ResApi> call = apiService.addAddress(token, address);
            call.enqueue(new Callback<ResApi>() {
                @Override
                public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                    if (response.body().code == 1) {
                        Toast.makeText(AddAddressActivity.this, "Thêm địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddAddressActivity.this, response.body().message, Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<ResApi> call, Throwable t) {
                    Log.e("error", t.getMessage());
                    Toast.makeText(AddAddressActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            Log.e("error", e.getMessage());
            Toast.makeText(AddAddressActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private boolean validate() {
        if (edName.equals("")) {
            Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edCity.equals("")) {
            Toast.makeText(this, "Vui lòng nhập tỉnh,huyện,xã", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edStreet.equals("")) {
            Toast.makeText(this, "Vui lòng nhập số đường/nhà", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}