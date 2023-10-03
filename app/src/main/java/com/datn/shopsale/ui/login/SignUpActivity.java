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
    private EditText edEmail,edFullname,edPhoneNumber;
    private EditText edPassword;
    private EditText edConfirmPassword;
    private ProgressBar progressbar;
    private TextView tvLogin;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inutUI();
        userService = RetrofitConnection.getUserService();

        mAuth = FirebaseAuth.getInstance();

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
        mAuth.createUserWithEmailAndPassword(edEmail.getText().toString().trim(), edPassword.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        RequestBody requestBodyEmail = RequestBody.create(MediaType.parse("text/plain"), edEmail.getText().toString().trim());
                                        RequestBody requestBodyPasswd = RequestBody.create(MediaType.parse("text/plain"), edPassword.getText().toString().trim());
                                        RequestBody requestBodyFullname = RequestBody.create(MediaType.parse("text/plain"), edFullname.getText().toString());
                                        RequestBody requestBodyphoneNumber= RequestBody.create(MediaType.parse("text/plain"),edPhoneNumber.getText().toString());
                                        RequestBody requestBodyrole= RequestBody.create(MediaType.parse("text/plain"), "user");
                                        Call<ResApi> call = userService.register(requestBodyEmail,requestBodyFullname,requestBodyPasswd,requestBodyphoneNumber,requestBodyrole);
                                        call.enqueue(new Callback<ResApi>() {
                                            @Override
                                            public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                                                if(response.body().code==1){
                                                    progressbar.setVisibility(View.INVISIBLE);
                                                    btnSignUp.setVisibility(View.VISIBLE);
                                                    Toast.makeText(SignUpActivity.this, "Đã gửi xác nhận đến Email", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                    finish();
                                                }else {
                                                    Toast.makeText(SignUpActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                                    progressbar.setVisibility(View.INVISIBLE);
                                                    btnSignUp.setVisibility(View.VISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResApi> call, Throwable t) {
                                                progressbar.setVisibility(View.INVISIBLE);
                                                btnSignUp.setVisibility(View.VISIBLE);
                                                Log.e("Err", "onFailure: " + t);
                                                Toast.makeText(SignUpActivity.this, "onFailure: " + t, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        progressbar.setVisibility(View.INVISIBLE);
                                        btnSignUp.setVisibility(View.VISIBLE);
                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("errr", task.getException().getMessage());
                                    }
                                }
                            });
                        } else {
                            Log.e("errr", task.getException().getMessage());
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.INVISIBLE);
                            btnSignUp.setVisibility(View.VISIBLE);

                        }
                    }
                });


    }

    private Boolean validateSignUp() {
        if (edEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(edEmail.getText().toString()).matches()) {
            Toast.makeText(this, "Định dạng email không chính xác", Toast.LENGTH_SHORT).show();
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