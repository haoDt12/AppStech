package com.datn.shopsale.ui.dashboard.address;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.AddressAdapter;
import com.datn.shopsale.modelsv2.Address;
import com.datn.shopsale.request.DeleteAddressRequest;
import com.datn.shopsale.request.EditAddressRequest;
import com.datn.shopsale.responsev2.DeleteAddressResponse;
import com.datn.shopsale.responsev2.EditAddressResponse;
import com.datn.shopsale.responsev2.GetDeliveryAddressResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_EDIT_CITY = 1;
    private RecyclerView rcvAddress;
    private final ArrayList<Address> dataList = new ArrayList<>();
    private static final int REQUEST_ADD_ADDRESS = 123;
    private AddressAdapter addressAdapter;
    PreferenceManager preferenceManager;
    private ApiService apiService;
    private TextView edCity;
    String addressText;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        init();
        getDataAddress();
    }

    private void init() {
        Toolbar toolbarAddress = findViewById(R.id.toolbar_address);
        rcvAddress = findViewById(R.id.rcv_address);
        LinearLayout lnlAddAddress = findViewById(R.id.lnl_add_address);
        lnlAddAddress.setOnClickListener(this);
        setSupportActionBar(toolbarAddress);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);

        toolbarAddress.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.lnl_add_address) {
            startActivityForResult(new Intent(getApplicationContext(), AddAddressActivity.class), REQUEST_ADD_ADDRESS);
        }
    }

    private void getDataAddress() {
        LoadingDialog.showProgressDialog(AddressActivity.this, "Loading...");
        dataList.clear();
        Call<GetDeliveryAddressResponse> call = apiService.getDeliveryAddress(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetDeliveryAddressResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetDeliveryAddressResponse> call, @NonNull Response<GetDeliveryAddressResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        for (Address item : response.body().getAddress()) {
                            Address address = new Address();
                            address.set_id(item.get_id());
                            address.setCustomer_id(item.getCustomer_id());
                            address.setName(item.getName());
                            address.setCity(item.getCity());
                            address.setStreet(item.getStreet());
                            address.setPhone(item.getPhone());
                            dataList.add(address);
                        }
                        runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                addressAdapter = new AddressAdapter(dataList, AddressActivity.this, new AddressAdapter.Callback() {
                                    @Override
                                    public void editAddress(Address address) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(AddressActivity.this, R.style.FullScreenDialogTheme);
                                        LayoutInflater inflater = getLayoutInflater();
                                        View dialogView = inflater.inflate(R.layout.activity_add_address, null);
                                        builder.setView(dialogView);

                                        final AlertDialog dialog = builder.create();
                                        dialog.show();
                                        Toolbar toolbarCreAddress = dialog.findViewById(R.id.toolbar_cre_address);
                                        EditText edName = dialog.findViewById(R.id.ed_name);
                                        EditText edPhoneNumber = dialog.findViewById(R.id.ed_phone_number);
                                        edCity = dialog.findViewById(R.id.ed_city);
                                        EditText edStreet = dialog.findViewById(R.id.ed_street);
                                        Button btnSave = dialog.findViewById(R.id.btn_save);
                                        setSupportActionBar(toolbarCreAddress);
                                        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                                        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
                                        btnSave.setText("Sửa");
                                        toolbarCreAddress.setTitle("Sửa địa chỉ");
                                        edName.setText(address.getName());
                                        edPhoneNumber.setText(address.getPhone());
                                        edCity.setText(address.getCity());
                                        edStreet.setText(address.getStreet());

                                        edCity.setOnClickListener(v -> startActivityForResult(new Intent(AddressActivity.this, CityActivity.class), REQUEST_CODE_EDIT_CITY));
                                        btnSave.setOnClickListener(v -> {
                                            String name = edName.getText().toString().trim();
                                            String phone = edPhoneNumber.getText().toString().trim();
                                            String street = edStreet.getText().toString().trim();
                                            if (name.isEmpty() || phone.isEmpty() || street.isEmpty()) {
                                                Toast.makeText(AddressActivity.this, getResources().getString(R.string.khong_duoc_de_trong), Toast.LENGTH_SHORT).show();
                                            } else {
                                                updateObj(address.get_id(), name, addressText, street, phone);
                                                dialog.dismiss();
                                            }
                                        });

                                        toolbarCreAddress.setNavigationOnClickListener(v -> dialog.dismiss());
                                    }

                                    @Override
                                    public void deleteAddress(Address address) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(AddressActivity.this);
                                        builder.setTitle("Xóa địa chỉ");
                                        builder.setMessage(getResources().getString(R.string.ban_co_muon_xoa_dia_chi));
                                        builder.setPositiveButton("Xóa", (dialog, which) -> delete(address.get_id()));
                                        builder.setNegativeButton("Hủy", null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                });

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddressActivity.this, RecyclerView.VERTICAL, false);
                                rcvAddress.setLayoutManager(linearLayoutManager);
                                Intent resultIntent = getIntent();
                                String value = resultIntent.getStringExtra("select");
                                addressAdapter.setIsAddress(value != null && value.equals("oke"));
                                rcvAddress.setAdapter(addressAdapter);
                            }
                        });
                    } else {
                        runOnUiThread(() -> {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(AddressActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(AddressActivity.this, response.body().getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetDeliveryAddressResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(AddressActivity.this, t.getMessage());
                });
            }
        });
    }

    private void updateObj(String id, String name, String city, String street, String phone_number) {
        LoadingDialog.showProgressDialog(AddressActivity.this, "Loading...");
        EditAddressRequest address = new EditAddressRequest();
        address.setName(name);
        address.setCity(city);
        address.setStreet(street);
        address.setPhone_number(phone_number);
        address.setAddressId(id);

        Call<EditAddressResponse> call = apiService.editDeliveryAddress(preferenceManager.getString("token"), address);
        call.enqueue(new Callback<EditAddressResponse>() {
            @Override
            public void onResponse(@NonNull Call<EditAddressResponse> call, @NonNull Response<EditAddressResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        Toast.makeText(AddressActivity.this,getResources().getString(R.string.sua_thanh_cong), Toast.LENGTH_SHORT).show();
                        getDataAddress();
                    } else {
                        runOnUiThread(() -> {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(AddressActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(AddressActivity.this, response.body().getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<EditAddressResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(AddressActivity.this, t.getMessage());
                });
            }
        });
    }

    private void delete(String id) {
        LoadingDialog.showProgressDialog(AddressActivity.this, "Loading...");
        DeleteAddressRequest address = new DeleteAddressRequest();
        address.setAddressId(id);
        Call<DeleteAddressResponse> call = apiService.deleteDeliveryAddress(preferenceManager.getString("token"), address);
        call.enqueue(new Callback<DeleteAddressResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeleteAddressResponse> call, @NonNull Response<DeleteAddressResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        Toast.makeText(AddressActivity.this, getResources().getString(R.string.xoa_thanh_cong), Toast.LENGTH_SHORT).show();
                        getDataAddress();
                    } else {
                        runOnUiThread(() -> {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(AddressActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(AddressActivity.this, response.body().getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeleteAddressResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(AddressActivity.this, t.getMessage());
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ADDRESS && resultCode == Activity.RESULT_OK) {
            getDataAddress();
        } else if (requestCode == REQUEST_CODE_EDIT_CITY && resultCode == Activity.RESULT_OK) {
            boolean isCurrentLocationSelected = data.getBooleanExtra("isCurrentLocationSelected", false);
            if (isCurrentLocationSelected) {
                addressText = data.getStringExtra("currentLocation");
                edCity.setText(addressText);
            } else {
                String selectedCity = data.getStringExtra("selectedCity");
                String selectedDistrict = data.getStringExtra("selectedDistrict");
                String selectedWard = data.getStringExtra("selectedWard");
                if (selectedCity != null && selectedDistrict != null && selectedWard != null) {
                    addressText = selectedCity + ", " + selectedDistrict + ", " + selectedWard;
                    edCity.setText(addressText);
                }
            }
        }
    }
}
