package com.datn.shopsale.ui.dashboard.address;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.request.AddAddressRequest;
import com.datn.shopsale.responsev2.AddAddressResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_CITY = 123;
    private EditText edName, edPhoneNumber, edStreet;
    private TextView edCity;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private final AddAddressRequest address = new AddAddressRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        init();
    }

    private void init() {
        Toolbar toolbarCreAddress = findViewById(R.id.toolbar_cre_address);
        edName = findViewById(R.id.ed_name);
        edPhoneNumber = findViewById(R.id.ed_phone_number);
        edCity = findViewById(R.id.ed_city);
        edStreet = findViewById(R.id.ed_street);
        Button btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(this);
        edCity.setOnClickListener(this);
        setSupportActionBar(toolbarCreAddress);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarCreAddress.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            if (validate()) {
                addAddress();
            }
        } else if (view.getId() == R.id.ed_city) {
            startActivityForResult(new Intent(this, CityActivity.class), REQUEST_CODE_CITY);
        }
    }


    private void addAddress() {
        LoadingDialog.showProgressDialog(AddAddressActivity.this, "Loading...");
        String token = preferenceManager.getString("token");
        String name = edName.getText().toString().trim();
        String phone = edPhoneNumber.getText().toString().trim();
        String street = edStreet.getText().toString().trim();
        address.setPhone_number(phone);
        address.setName(name);
        address.setStreet(street);
        Call<AddAddressResponse> call = apiService.addDeliveryAddress(token, address);
        call.enqueue(new Callback<AddAddressResponse>() {
            @Override
            public void onResponse(@NonNull Call<AddAddressResponse> call, @NonNull Response<AddAddressResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        Toast.makeText(AddAddressActivity.this, getResources().getString(R.string.them_dia_chi_thanh_cong), Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    } else {
                        runOnUiThread(() -> {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(AddAddressActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(AddAddressActivity.this, response.body().getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddAddressResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(AddAddressActivity.this, t.getMessage());
                });
            }
        });
    }

    private boolean validate() {
        if (edName.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.them_dia_chi_thanh_cong), Toast.LENGTH_SHORT).show();
            return false;
        } else if (edCity.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.vui_long_nhap_huye_tinh_xa), Toast.LENGTH_SHORT).show();
            return false;
        } else if (edStreet.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.vui_long_nhap_duong_so_nha), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CITY && resultCode == Activity.RESULT_OK) {
            boolean isCurrentLocationSelected = data.getBooleanExtra("isCurrentLocationSelected", false);

            if (isCurrentLocationSelected) {
                String currentLocation = data.getStringExtra("currentLocation");
                edCity.setText(currentLocation);
                address.setCity(currentLocation);
            } else {
                String selectedCity = data.getStringExtra("selectedCity");
                String selectedDistrict = data.getStringExtra("selectedDistrict");
                String selectedWard = data.getStringExtra("selectedWard");
                if (selectedCity != null && selectedDistrict != null && selectedWard != null) {
                    String addressText = selectedCity + ", " + selectedDistrict + ", " + selectedWard;
                    edCity.setText(addressText);
                    address.setCity(addressText);
                }
            }
        }
    }
}