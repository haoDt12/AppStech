package com.datn.shopsale.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.request.RegisterCusRequest;
import com.datn.shopsale.responsev2.RegisterCustomerResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private ImageView imgLogo;
    private EditText edEmail, edFullname, edPhoneNumber;
    private EditText edPassword;
    private EditText edConfirmPassword;
    private ProgressBar progressbar;
    private TextView tvLogin;
    private Button btnSignUp;
    ApiService apiService;
    private TextInputLayout tilEmail;
    private TextInputLayout tilFullName;
    private TextInputLayout tilPhoneNumberRegister;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inutUI();
        apiService = RetrofitConnection.getApiService();


        btnSignUp.setOnClickListener(view -> {
            if (validateSignUp())
                onClickSignUp();
        });
        tvLogin.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    private void onClickSignUp() {
        progressbar.setVisibility(View.VISIBLE);
        btnSignUp.setVisibility(View.INVISIBLE);
        try {
            RegisterCusRequest request = new RegisterCusRequest();
            request.setPassword(edPassword.getText().toString().trim());
            request.setEmail(edEmail.getText().toString().trim());
            request.setFull_name(edFullname.getText().toString().trim());
            request.setPhone_number(edPhoneNumber.getText().toString().trim());
            Call<RegisterCustomerResponse> call = apiService.registerCustomer(request);
            call.enqueue(new Callback<RegisterCustomerResponse>() {
                @Override
                public void onResponse(@NonNull Call<RegisterCustomerResponse> call, @NonNull Response<RegisterCustomerResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            progressbar.setVisibility(View.INVISIBLE);
                            btnSignUp.setVisibility(View.VISIBLE);
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("Notification")
                                    .setMessage(response.body().getMessage())
                                    .setPositiveButton("OK", (dialog, which) -> finish())
                                    .show();
                        } else {
                            progressbar.setVisibility(View.INVISIBLE);
                            btnSignUp.setVisibility(View.VISIBLE);
                            runOnUiThread(() -> AlertDialogUtil.showAlertDialogWithOk(SignUpActivity.this, response.body().getMessage()));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RegisterCustomerResponse> call, @NonNull Throwable t) {
                    progressbar.setVisibility(View.INVISIBLE);
                    btnSignUp.setVisibility(View.VISIBLE);
                    runOnUiThread(() -> AlertDialogUtil.showAlertDialogWithOk(SignUpActivity.this, t.getMessage()));
                }
            });


        } catch (Exception e) {
            progressbar.setVisibility(View.INVISIBLE);
            btnSignUp.setVisibility(View.VISIBLE);
            Log.e("Error", "onFailure: " + e);
            Toast.makeText(SignUpActivity.this, "error: " + e, Toast.LENGTH_SHORT).show();
        }

    }

    private Boolean validateSignUp() {
        if (edEmail.getText().toString().isEmpty()) {
            tilEmail.setError("Email không được để trống");
//            Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(edEmail.getText().toString()).matches()) {
            tilEmail.setError("Định dạng email không chính xác");
//            Toast.makeText(this, "Định dạng email không chính xác", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edFullname.getText().toString().isEmpty()) {
            tilFullName.setError("Họ tên không được để trống");
//            Toast.makeText(this, , Toast.LENGTH_SHORT).show();
            return false;
        } else if (edPhoneNumber.getText().toString().isEmpty()) {
            tilPhoneNumberRegister.setError("Số điện thoại không được để trống");
//            Toast.makeText(this, , Toast.LENGTH_SHORT).show();
            return false;
        } else if (edPassword.getText().toString().isEmpty()) {
            tilPassword.setError("Mật khẩu không được để trống");
//            Toast.makeText(this, , Toast.LENGTH_SHORT).show();
            return false;
        } else if (edConfirmPassword.getText().toString().isEmpty()) {
            tilConfirmPassword.setError("Xác nhận mật khẩu không được để trống");
//            Toast.makeText(this, , Toast.LENGTH_SHORT).show();
            return false;
        } else if (!edConfirmPassword.getText().toString().trim().equals(edPassword.getText().toString().trim())) {
            tilConfirmPassword.setError("Xác nhận mật khẩu không trùng khớp");
            Toast.makeText(this, "Xác nhận mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void inutUI() {
        tilEmail = (TextInputLayout) findViewById(R.id.til_email);
        tilFullName = (TextInputLayout) findViewById(R.id.til_full_name);
        tilPhoneNumberRegister = (TextInputLayout) findViewById(R.id.til_phone_number_register);
        tilPassword = (TextInputLayout) findViewById(R.id.til_password);
        tilConfirmPassword = (TextInputLayout) findViewById(R.id.til_confirm_password);
        imgLogo = findViewById(R.id.img_logo);
        edEmail = findViewById(R.id.ed_email);
        edFullname = findViewById(R.id.ed_full_name);
        edPassword = findViewById(R.id.ed_password);
        edConfirmPassword = findViewById(R.id.ed_confirm_password);
        tvLogin = findViewById(R.id.tv_login);
        progressbar = findViewById(R.id.progressbar);
        btnSignUp = findViewById(R.id.btn_sign_up);
        edPhoneNumber = findViewById(R.id.ed_phone_number_register);
        TextView scrollingTextView = findViewById(R.id.tv_msg);
        scrollingTextView.setSelected(true);
        scrollingTextView.setSingleLine(true);
    }
}