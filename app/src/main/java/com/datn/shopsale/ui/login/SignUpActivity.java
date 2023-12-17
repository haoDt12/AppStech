package com.datn.shopsale.ui.login;

import androidx.appcompat.app.AppCompatActivity;

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

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.google.android.material.textfield.TextInputLayout;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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
            RequestBody emailRequestBody = RequestBody.create(MediaType.parse("text/plain"),edEmail.getText().toString().trim());
            RequestBody nameRequestBody = RequestBody.create(MediaType.parse("text/plain"),edFullname.getText().toString().trim());
            RequestBody phoneRequestBody = RequestBody.create(MediaType.parse("text/plain"),edPhoneNumber.getText().toString().trim());
            RequestBody passwdRequestBody = RequestBody.create(MediaType.parse("text/plain"),edPassword.getText().toString().trim());
            Call<ResApi>call = apiService.register(emailRequestBody,nameRequestBody,passwdRequestBody,phoneRequestBody);
            call.enqueue(new Callback<ResApi>() {
                @Override
                public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                    if (response.body().code ==1){
                        progressbar.setVisibility(View.INVISIBLE);
                        btnSignUp.setVisibility(View.VISIBLE);
                        Toast.makeText(SignUpActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        String idUserTemp = response.body().id;
                        Intent i  = new Intent(SignUpActivity.this, VerifyOTPActivity.class);
                        i.putExtra("idUserTemp",idUserTemp);
                        startActivity(i);

                    }else {
                        progressbar.setVisibility(View.INVISIBLE);
                        btnSignUp.setVisibility(View.VISIBLE);
                        Toast.makeText(SignUpActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResApi> call, Throwable t) {
                    progressbar.setVisibility(View.INVISIBLE);
                    btnSignUp.setVisibility(View.VISIBLE);
                    Log.e("Error", "onFailure: " + t);
                    Toast.makeText(SignUpActivity.this, "error: "+t, Toast.LENGTH_SHORT).show();
                }
            });


        }catch (Exception e){
            progressbar.setVisibility(View.INVISIBLE);
            btnSignUp.setVisibility(View.VISIBLE);
            Log.e("Error", "onFailure: " + e);
            Toast.makeText(SignUpActivity.this, "error: "+e, Toast.LENGTH_SHORT).show();
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