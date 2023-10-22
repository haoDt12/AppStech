package com.datn.shopsale.ui.login;

import androidx.annotation.NonNull;
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

import com.datn.shopsale.Interface.UserService;
import com.datn.shopsale.R;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inutUI();
        userService = RetrofitConnection.getUserService();


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
            Call<ResApi>call = userService.register(emailRequestBody,nameRequestBody,passwdRequestBody,phoneRequestBody);
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
                    Log.e("Error", "onFailure: " + t);
                    Toast.makeText(SignUpActivity.this, "error: "+t, Toast.LENGTH_SHORT).show();
                }
            });


        }catch (Exception e){
            Log.e("Error", "onFailure: " + e);
            Toast.makeText(SignUpActivity.this, "error: "+e, Toast.LENGTH_SHORT).show();
        }

    }

    private Boolean validateSignUp() {
        if (edEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(edEmail.getText().toString()).matches()) {
            Toast.makeText(this, "Định dạng email không chính xác", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edFullname.getText().toString().isEmpty()) {
            Toast.makeText(this, "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(this, "Số điện thoại không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edConfirmPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Xác nhận mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!edConfirmPassword.getText().toString().trim().equals(edPassword.getText().toString().trim())) {
            Toast.makeText(this, "Xác nhận mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void inutUI() {
        imgLogo = findViewById(R.id.img_logo);
        edEmail = findViewById(R.id.ed_email);
        edFullname = findViewById(R.id.ed_fullname);
        edPassword = findViewById(R.id.ed_password);
        edConfirmPassword = findViewById(R.id.ed_confirm_password);
        tvLogin = findViewById(R.id.tv_login);
        progressbar = findViewById(R.id.progressbar);
        btnSignUp = findViewById(R.id.btn_sign_up);
        edPhoneNumber = findViewById(R.id.ed_phoneNumber);
    }
}