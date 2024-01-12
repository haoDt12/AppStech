package com.datn.shopsale.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.retrofit.RetrofitConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOTPActivity extends AppCompatActivity {
    private static final String TAG= VerifyOTPActivity.class.getSimpleName();
    private String idUserTemp;
    private EditText edNumber1;
    private EditText edNumber2;
    private EditText edNumber3;
    private EditText edNumber4;
    private EditText edNumber5;
    private EditText edNumber6;
    private Button btnVerify;
    private ProgressBar progressBar;
    private ApiService apiService;
    private String OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);
        initUI();
        apiService = RetrofitConnection.getApiService();
        fillInputOTP();
        btnVerify.setOnClickListener(view -> {
            if (validateOTP()) {
                onCLickVerifyOTP();
            }
        });
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

    private void onCLickVerifyOTP() {
        btnVerify.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        try {
            Intent intent = getIntent();
            idUserTemp = intent.getStringExtra("idUserTemp");
            OTP = edNumber1.getText().toString().trim()
                    + edNumber2.getText().toString().trim()
                    + edNumber3.getText().toString().trim()
                    + edNumber4.getText().toString().trim()
                    + edNumber5.getText().toString().trim()
                    + edNumber6.getText().toString().trim();


            Call<ResApi> call = apiService.verifyOTPRegister(idUserTemp, OTP);
            call.enqueue(new Callback<ResApi>() {
                @Override
                public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                    if (response.body().code == 1) {
                        progressBar.setVisibility(View.INVISIBLE);
                        btnVerify.setVisibility(View.VISIBLE);
                        Toast.makeText(VerifyOTPActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(VerifyOTPActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        btnVerify.setVisibility(View.VISIBLE);
                        Toast.makeText(VerifyOTPActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResApi> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    btnVerify.setVisibility(View.VISIBLE);
                    Log.e("Error", "onFailure: " + t);
                    Toast.makeText(VerifyOTPActivity.this, "error: " + t, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
            btnVerify.setVisibility(View.VISIBLE);
            Log.e("Exception", "onFailure: " + e);
            Toast.makeText(VerifyOTPActivity.this, "Exception: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        edNumber1 = (EditText) findViewById(R.id.ed_number1);
        edNumber2 = (EditText) findViewById(R.id.ed_number2);
        edNumber3 = (EditText) findViewById(R.id.ed_number3);
        edNumber4 = (EditText) findViewById(R.id.ed_number4);
        edNumber5 = (EditText) findViewById(R.id.ed_number5);
        edNumber6 = (EditText) findViewById(R.id.ed_number6);
        btnVerify = (Button) findViewById(R.id.btn_verify);
        progressBar = findViewById(R.id.id_progress);

    }

    private boolean validateOTP() {
        if (edNumber1.getText().toString().trim().isEmpty() ||
                edNumber2.getText().toString().trim().isEmpty() ||
                edNumber3.getText().toString().trim().isEmpty() ||
                edNumber4.getText().toString().trim().isEmpty() ||
                edNumber5.getText().toString().trim().isEmpty() ||
                edNumber6.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Mã OTP không hợp lệ vui lòng thử lại", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}