package com.datn.shopsale.ui.dashboard.address;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.models.Address;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_CITY = 123;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Toolbar toolbarCreAddress;
    private EditText edName, edPhoneNumber, edCity, edStreet;
    private Button btnSave;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private  Address address = new Address();
    private boolean isCurrentLocationSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        init();
    }

    private void init() {
        toolbarCreAddress = (Toolbar) findViewById(R.id.toolbar_cre_address);
        edName = (EditText) findViewById(R.id.ed_name);
        edPhoneNumber = (EditText) findViewById(R.id.ed_phone_number);
        edCity = (EditText) findViewById(R.id.ed_city);
        edStreet = (EditText) findViewById(R.id.ed_street);
        btnSave = (Button) findViewById(R.id.btn_save);

        btnSave.setOnClickListener(this);
        edCity.setOnClickListener(this);
        setSupportActionBar(toolbarCreAddress);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarCreAddress.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            if (validate()) {
                addAddress();
            }
        }else if(view.getId() == R.id.ed_city){
            startActivityForResult(new Intent(this, CityActivity.class), REQUEST_CODE_CITY);
        }
    }


    private void addAddress() {
        String token = preferenceManager.getString("token");
        String idUser = preferenceManager.getString("userId");

        String name = edName.getText().toString().trim();
        String phone = edPhoneNumber.getText().toString().trim();
        String street = edStreet.getText().toString().trim();


        address.setUserId(idUser);
        address.setPhone_number(phone);
        address.setName(name);
        address.setStreet(street);

        try {
            Call<ResApi> call = apiService.addAddress(token, address);
            call.enqueue(new Callback<ResApi>() {
                @Override
                public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                    if (response.body().code == 1) {
                        Toast.makeText(AddAddressActivity.this, "Thêm địa chỉ thành công", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CITY && resultCode == Activity.RESULT_OK) {
            // Nhận dữ liệu từ CityActivity
            isCurrentLocationSelected = data.getBooleanExtra("isCurrentLocationSelected", false);

            if (isCurrentLocationSelected) {
                // Nếu đã chọn vị trí hiện tại
                String currentLocation = data.getStringExtra("currentLocation");
                edCity.setText(currentLocation);
                Log.d("TAG", "onActivityResult1: "+currentLocation);
                address.setCity(currentLocation);
                // Thực hiện các xử lý khác cho vị trí hiện tại nếu cần
            } else {
                // Nếu chọn vị trí từ danh sách
                String selectedCity = data.getStringExtra("selectedCity");
                String selectedDistrict = data.getStringExtra("selectedDistrict");
                String selectedWard = data.getStringExtra("selectedWard");

                // Hiển thị dữ liệu trong EditText
                if (selectedCity != null && selectedDistrict != null && selectedWard != null) {
                    String addressText = selectedCity + ", " + selectedDistrict + ", " + selectedWard;
                    edCity.setText(addressText);
                    Log.d("TAG", "onActivityResult2: "+addressText);
                    address.setCity(addressText);
                }
            }
        }
    }

}