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
import com.datn.shopsale.MainActivity;
import com.datn.shopsale.R;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.response.UserVerifyLoginResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

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
    private String OTP;
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
            if(validateOTP()){
                onClickVerify();
            }
        });
    }
    private  void onClickVerify(){
        btnVerify.setVisibility(View.INVISIBLE);
        idProgress.setVisibility(View.VISIBLE);
        try {
            OTP = edNumber1.getText().toString().trim()
                    + edNumber2.getText().toString().trim()
                    + edNumber3.getText().toString().trim()
                    + edNumber4.getText().toString().trim()
                    + edNumber5.getText().toString().trim()
                    + edNumber6.getText().toString().trim();
            Call<UserVerifyLoginResponse.Root> call = apiService.verifyOTPSignIn(idUser,OTP);
            call.enqueue(new Callback<UserVerifyLoginResponse.Root>() {
                @Override
                public void onResponse(Call<UserVerifyLoginResponse.Root> call, Response<UserVerifyLoginResponse.Root> response) {
                    if (response.body().getCode() == 1) {
                        idProgress.setVisibility(View.INVISIBLE);
                        btnVerify.setVisibility(View.VISIBLE);
                        Toast.makeText(VerifyOTPSignInActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        preferenceManager.putString("token",response.body().getToken());
                        preferenceManager.putString("userId",response.body().getUser().get_id());
                        preferenceManager.putString("avatarLogin",response.body().getUser().getAvatar());
                        preferenceManager.putString("nameLogin",response.body().getUser().getFull_name());
                        Intent intent = new Intent(VerifyOTPSignInActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        runOnUiThread(() -> {
                            idProgress.setVisibility(View.INVISIBLE);
                            btnVerify.setVisibility(View.VISIBLE);
                            Toast.makeText(VerifyOTPSignInActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onFailure(Call<UserVerifyLoginResponse.Root> call, Throwable t) {
                    runOnUiThread(()->{
                        idProgress.setVisibility(View.INVISIBLE);
                        btnVerify.setVisibility(View.VISIBLE);
                        Log.e("Error", "onFailure: " + t);
                        Toast.makeText(VerifyOTPSignInActivity.this, "error: " + t, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }catch (Exception e){
            idProgress.setVisibility(View.INVISIBLE);
            btnVerify.setVisibility(View.VISIBLE);
            Log.e("Exception", "onFailure: " + e);
            Toast.makeText(VerifyOTPSignInActivity.this, "Exception: " + e, Toast.LENGTH_SHORT).show();
        }


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
    private  void initUI(){
        edNumber1 = (EditText) findViewById(R.id.ed_number1);
        edNumber2 = (EditText) findViewById(R.id.ed_number2);
        edNumber3 = (EditText) findViewById(R.id.ed_number3);
        edNumber4 = (EditText) findViewById(R.id.ed_number4);
        edNumber5 = (EditText) findViewById(R.id.ed_number5);
        edNumber6 = (EditText) findViewById(R.id.ed_number6);
        idProgress = (ProgressBar) findViewById(R.id.id_progress);
        btnVerify = (Button) findViewById(R.id.btn_verify);

    }
}