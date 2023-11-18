package com.datn.shopsale.ui.dashboard.address;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.AddressAdapter;
import com.datn.shopsale.models.Address;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.request.AddressRequest;
import com.datn.shopsale.response.ResponseAddress;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView imgBack;
    private LinearLayout lnlAddAddress;
    private RecyclerView rcvAddress;
    private ArrayList<Address> dataList = new ArrayList<>();
    private static final int REQUEST_ADD_ADDRESS = 123;
    private AddressAdapter addressAdapter;
    PreferenceManager preferenceManager;
    private ApiService apiService;

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
        imgBack = findViewById(R.id.img_back);
        rcvAddress = (RecyclerView) findViewById(R.id.rcv_address);
        lnlAddAddress = findViewById(R.id.lnl_add_address);
        imgBack.setOnClickListener(this);
        lnlAddAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back) {
            super.onBackPressed();
        } else if (view.getId() == R.id.lnl_add_address) {
            startActivityForResult(new Intent(getApplicationContext(), AddAddressActivity.class), REQUEST_ADD_ADDRESS);
        }
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
                        dataList.add(new Address(item.get_id(),item.getUserId() ,item.getName(), item.getCity(), item.getStreet(), item.getPhone_number()));
                    }
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            addressAdapter = new AddressAdapter(dataList,getApplicationContext(), new AddressAdapter.Callback() {
                                @Override
                                public void editAddress(Address address) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AddressActivity.this,R.style.FullScreenDialogTheme);
                                    LayoutInflater inflater = getLayoutInflater();
                                    View dialogView = inflater.inflate(R.layout.activity_add_address, null);
                                    builder.setView(dialogView);

                                    final AlertDialog dialog = builder.create();
                                    dialog.show();
                                    ImageButton imgBack = (ImageButton) dialog.findViewById(R.id.img_back);
                                    TextView add_adr = dialog.findViewById(R.id.add_adr);
                                    EditText edName = (EditText) dialog.findViewById(R.id.ed_name);
                                    EditText  edPhoneNumber = (EditText) dialog.findViewById(R.id.ed_phone_number);
                                    EditText  edCity = (EditText) dialog.findViewById(R.id.ed_city);
                                    EditText  edStreet = (EditText) dialog.findViewById(R.id.ed_street);
                                    Button  btnSave = (Button) dialog.findViewById(R.id.btn_save);

                                    btnSave.setText("Sửa");
                                    add_adr.setText("Sửa địa chỉ");
                                    edName.setText(address.getName());
                                    edPhoneNumber.setText(address.getPhone_number());
                                    edCity.setText(address.getCity());
                                    edStreet.setText(address.getStreet());

                                    btnSave.setOnClickListener(v -> {
                                        String name = edName.getText().toString().trim();
                                        String phone = edPhoneNumber.getText().toString().trim();
                                        String city = edCity.getText().toString().trim();
                                        String street = edStreet.getText().toString().trim();
                                        if (name.isEmpty() || phone.isEmpty() || city.isEmpty() || street.isEmpty()) {
                                            Toast.makeText(AddressActivity.this, "Không được để trống", Toast.LENGTH_SHORT).show();
                                        }else {
                                            updateObj(address.get_id(),name, city, street, phone);
                                            dialog.dismiss();
                                        }
                                    });
                                    imgBack.setOnClickListener(v->{
                                        dialog.dismiss();
                                    });
                                }

                                @Override
                                public void deleteAddress(Address address) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AddressActivity.this);
                                    builder.setTitle("Xóa sản phẩm");
                                    builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?");
                                    builder.setPositiveButton("Xóa", (dialog, which) -> {
                                        delete(address.get_id());
                                    });
                                    builder.setNegativeButton("Hủy", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddressActivity.this, RecyclerView.VERTICAL, false);
                            rcvAddress.setLayoutManager(linearLayoutManager);
                            rcvAddress.setAdapter(addressAdapter);
                        }
                    });
                } else {
                    Toast.makeText(AddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseAddress.Root> call, Throwable t) {
                Toast.makeText(AddressActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateObj(String id,String name,String city,String street,String phone_number) {
        AddressRequest address = new AddressRequest();
        address.setName(name);
        address.setCity(city);
        address.setStreet(street);
        address.setPhone_number(phone_number);
        address.setAddressId(id);

        Call<ResApi> call = apiService.editAddress(preferenceManager.getString("token"),address);
        call.enqueue(new Callback<ResApi>() {
            @Override
            public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                if (response.body().code == 1) {
                    Toast.makeText(AddressActivity.this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                    getDataAddress();
                } else {
                    Toast.makeText(AddressActivity.this, response.body().message, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResApi> call, Throwable t) {
                Log.d("MAIN", "Respone Fail" + t.getMessage());
            }
        });
    }
    private void delete(String id) {
        AddressRequest address = new AddressRequest();
        address.setAddressId(id);
        Call<ResApi> call = apiService.deleteAddress(preferenceManager.getString("token"),preferenceManager.getString("userId"),id);
        call.enqueue(new Callback<ResApi>() {
            @Override
            public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                if (response.body().code == 1) {
                    Toast.makeText(AddressActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                   getDataAddress();
                } else {
                    Toast.makeText(AddressActivity.this, " "+response.body().message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResApi> call, Throwable t) {
                Toast.makeText(AddressActivity.this, " "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ADDRESS && resultCode == Activity.RESULT_OK) {
            // Refresh address list
            getDataAddress();
        }
    }
}
