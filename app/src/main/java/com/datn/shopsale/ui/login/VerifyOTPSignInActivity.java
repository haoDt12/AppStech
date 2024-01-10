package com.datn.shopsale.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.MainActivity;
import com.datn.shopsale.R;
import com.datn.shopsale.request.AddFcmRequest;
import com.datn.shopsale.request.CusVerifyLoginRequest;
import com.datn.shopsale.responsev2.AddFcmResponse;
import com.datn.shopsale.responsev2.CusVerifyLoginResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOTPSignInActivity extends AppCompatActivity {
    private EditText edNumber1;
    private EditText edNumber2;
    private EditText edNumber3;
    private EditText edNumber4;
    private EditText edNumber5;
    private EditText edNumber6;
    private ProgressBar idProgress;
    private Button btnVerify;
    private String idUser;
    private ApiService apiService;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpsign_in);
        initUI();
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        fillInputOTP();
        Intent intent = getIntent();
        idUser = intent.getStringExtra("idUser");
        btnVerify.setOnClickListener(view -> {
            if (validateOTP()) {
                onClickVerify();
            }
        });
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String token = task.getResult();
            preferenceManager.putString("fcm", token);
        });
    }

    private void onClickVerify() {
        btnVerify.setVisibility(View.INVISIBLE);
        idProgress.setVisibility(View.VISIBLE);
        try {
            String OTP = edNumber1.getText().toString().trim()
                    + edNumber2.getText().toString().trim()
                    + edNumber3.getText().toString().trim()
                    + edNumber4.getText().toString().trim()
                    + edNumber5.getText().toString().trim()
                    + edNumber6.getText().toString().trim();
            CusVerifyLoginRequest request = new CusVerifyLoginRequest();
            request.setOtp(OTP);
            request.setCusId(idUser);
            Call<CusVerifyLoginResponse> call = apiService.verifyCusLogin(request);
            call.enqueue(new Callback<CusVerifyLoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<CusVerifyLoginResponse> call, @NonNull Response<CusVerifyLoginResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
                                Toast.makeText(VerifyOTPSignInActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                preferenceManager.putString("token", response.body().getToken());
                                preferenceManager.putString("userId", response.body().getCus().get_id());
                                preferenceManager.putString("avatarLogin", response.body().getCus().getAvatar());
                                preferenceManager.putString("nameLogin", response.body().getCus().getFull_name());
                                addTokenFMC(preferenceManager.getString("token"), preferenceManager.getString("userId"), preferenceManager.getString("fcm"));
                            });
                        } else {
                            runOnUiThread(() -> {
                                idProgress.setVisibility(View.INVISIBLE);
                                btnVerify.setVisibility(View.VISIBLE);
                                if (response.body().getMessage().equals("wrong token")) {
                                    CheckLoginUtil.gotoLogin(VerifyOTPSignInActivity.this, response.body().getMessage());
                                } else {
                                    AlertDialogUtil.showAlertDialogWithOk(VerifyOTPSignInActivity.this, response.body().getMessage());
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CusVerifyLoginResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        idProgress.setVisibility(View.INVISIBLE);
                        btnVerify.setVisibility(View.VISIBLE);
                        AlertDialogUtil.showAlertDialogWithOk(VerifyOTPSignInActivity.this, t.getMessage());
                    });
                }
            });
        } catch (Exception e) {
            idProgress.setVisibility(View.INVISIBLE);
            btnVerify.setVisibility(View.VISIBLE);
            AlertDialogUtil.showAlertDialogWithOk(this, e.getMessage());
        }


    }

    private boolean validateOTP() {
        if (edNumber1.getText().toString().trim().isEmpty() ||
                edNumber2.getText().toString().trim().isEmpty() ||
                edNumber3.getText().toString().trim().isEmpty() ||
                edNumber4.getText().toString().trim().isEmpty() ||
                edNumber5.getText().toString().trim().isEmpty() ||
                edNumber6.getText().toString().trim().isEmpty()) {
            AlertDialogUtil.showAlertDialogWithOk(this, getResources().getString(R.string.ma_otp_khong_hop_le));
            return false;
        }
        return true;
    }

    private void fillInputOTP() {

        edNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edNumber2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edNumber3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edNumber4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edNumber4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edNumber5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edNumber5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edNumber6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initUI() {
        edNumber1 = findViewById(R.id.ed_number1);
        edNumber2 = findViewById(R.id.ed_number2);
        edNumber3 = findViewById(R.id.ed_number3);
        edNumber4 = findViewById(R.id.ed_number4);
        edNumber5 = findViewById(R.id.ed_number5);
        edNumber6 = findViewById(R.id.ed_number6);
        idProgress = findViewById(R.id.id_progress);
        btnVerify = findViewById(R.id.btn_verify);

    }

    private void addTokenFMC(String token, String userId, String fcm) {
        AddFcmRequest request = new AddFcmRequest();
        request.setCusId(userId);
        request.setFcm(fcm);
        Call<AddFcmResponse> call = apiService.addFCMCus(token, request);
        call.enqueue(new Callback<AddFcmResponse>() {
            @Override
            public void onResponse(@NonNull Call<AddFcmResponse> call, @NonNull Response<AddFcmResponse> response) {
                if(response.body() != null){
                    if (response.body().getCode() == 1) {
                        idProgress.setVisibility(View.INVISIBLE);
                        btnVerify.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(VerifyOTPSignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        runOnUiThread(() -> {
                            idProgress.setVisibility(View.INVISIBLE);
                            btnVerify.setVisibility(View.VISIBLE);
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(VerifyOTPSignInActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(VerifyOTPSignInActivity.this, response.body().getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddFcmResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    idProgress.setVisibility(View.INVISIBLE);
                    btnVerify.setVisibility(View.VISIBLE);
                    AlertDialogUtil.showAlertDialogWithOk(VerifyOTPSignInActivity.this, t.getMessage());
                });
            }
        });
    }
}