package com.datn.shopsale.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.AddressAdapter;
import com.datn.shopsale.models.Address;
import com.datn.shopsale.response.ResponseAddress;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.dashboard.address.AddressActivity;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformationUserActivity extends AppCompatActivity {
    private ImageButton imgBack;
    private ImageView imgCamera;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvLocation;
    private ImageView imgUser;

    private EditText edName;
    private EditText edEmail;
    private EditText edPhone;
    private EditText edLocation;
    private Button btnSave;
    private ImageView imgUpdate;
    private LinearLayout lnlLayoutText;
    private LinearLayout lnlLayoutEdit;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private ArrayList<ResponseAddress.User> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        FindViewById();
        getDataUser();
        imgBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
        imgUpdate.setOnClickListener(view -> {
            imgUpdate.setVisibility(View.INVISIBLE);
            lnlLayoutText.setVisibility(View.INVISIBLE);
            lnlLayoutEdit.setVisibility(View.VISIBLE);
            imgCamera.setVisibility(View.VISIBLE);
        });
    }

    private void FindViewById() {
        imgBack = (ImageButton) findViewById(R.id.img_back);
        imgCamera = (ImageView) findViewById(R.id.img_camera);
        imgUser = (ImageView) findViewById(R.id.img_user);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        edName = (EditText) findViewById(R.id.ed_name);
        edEmail = (EditText) findViewById(R.id.ed_email);
        edPhone = (EditText) findViewById(R.id.ed_phone);
        edLocation = (EditText) findViewById(R.id.ed_location);
        btnSave = (Button) findViewById(R.id.btn_save);
        imgUpdate = (ImageView) findViewById(R.id.img_update);
        lnlLayoutText = (LinearLayout) findViewById(R.id.lnl_layout_text);
        lnlLayoutEdit = (LinearLayout) findViewById(R.id.lnl_layout_edit);

    }

    private void getDataUser() {
        list.clear();
        String idUser = preferenceManager.getString("userId");

        Call<ResponseAddress.Root> call = apiService.getAddress(preferenceManager.getString("token"), idUser);
        call.enqueue(new Callback<ResponseAddress.Root>() {
            @Override
            public void onResponse(Call<ResponseAddress.Root> call, Response<ResponseAddress.Root> response) {
                if (response.body().getCode() == 1) {
                    ResponseAddress.User user = response.body().getUser();
                    Glide.with(getApplicationContext()).load(user.getAvatar()).into(imgUser);
                    tvLocation.setText(user.getAddress().get(0).getCity());
                    tvEmail.setText(user.getEmail());
                    tvName.setText(user.getFull_name());
                    tvPhone.setText(user.getPhone_number());
                } else {
                    Toast.makeText(InformationUserActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseAddress.Root> call, Throwable t) {
                Toast.makeText(InformationUserActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getUpdateUser(){

    }
}