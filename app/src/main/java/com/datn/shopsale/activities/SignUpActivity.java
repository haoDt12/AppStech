package com.datn.shopsale.activities;

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

import com.datn.shopsale.MainActivity;
import com.datn.shopsale.R;
import com.datn.shopsale.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private ImageView imgLogo;
    private EditText edEmail;
    private EditText edPassword;
    private EditText edConfirmPassword;
    private ProgressBar progressbar;
    private TextView tvLogin;
    private Button btnSignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inutUI();
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(view -> {
          if(validateSignUp())
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
                                        Toast.makeText(SignUpActivity.this, "Đã gửi xác nhận đến Email", Toast.LENGTH_SHORT).show();
                                        progressbar.setVisibility(View.INVISIBLE);
                                        btnDangky.setVisibility(View.VISIBLE);
                                    } else {
                                        progressbar.setVisibility(View.INVISIBLE);
                                        btnDangky.setVisibility(View.VISIBLE);
                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("errr",task.getException().getMessage());
                                    }
                                }
                            });
                        } else {
                            Log.e("errr",task.getException().getMessage());
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.INVISIBLE);
                            btnDangky.setVisibility(View.VISIBLE);

                        }
                    }
                });

    }

    private Boolean validateSignUp(){
        if(edEmail.getText().toString().isEmpty()){
            Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(edEmail.getText().toString()).matches()){
            Toast.makeText(this, "Định dạng email không chính xác", Toast.LENGTH_SHORT).show();
            return false;
        }else if(edPasswd.getText().toString().isEmpty()){
            Toast.makeText(this, "Mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        } else if(edConfirmPasswd.getText().toString().isEmpty()){
            Toast.makeText(this, "Xác nhận mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!edConfirmPasswd.getText().toString().trim().equals(edPasswd.getText().toString().trim())){
            Toast.makeText(this, "Xác nhận mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    private void inutUI() {
        imgLogo = findViewById(R.id.img_logo);
        edEmail = findViewById(R.id.ed_email);
        edPassword = findViewById(R.id.ed_password);
        edConfirmPassword = findViewById(R.id.ed_confirm_password);
        tvLogin = findViewById(R.id.tv_login);
        progressbar = findViewById(R.id.progressbar);
        btnSignUp = findViewById(R.id.btn_sign_up);
    }
}