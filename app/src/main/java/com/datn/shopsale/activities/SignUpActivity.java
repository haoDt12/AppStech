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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.datn.shopsale.MainActivity;
import com.datn.shopsale.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private EditText edEmail;
    private EditText edPasswd;
    private EditText edConfirmPasswd;
    private ProgressBar progressbar;
    private Button btnDangky;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        inutUI();
        mAuth = FirebaseAuth.getInstance();
        btnDangky.setOnClickListener(view -> {
          if(validateSignUp()){
              onClickSignUp();
          }
        });
    }

    private void onClickSignUp() {
        progressbar.setVisibility(View.VISIBLE);
        btnDangky.setVisibility(View.INVISIBLE);
        mAuth.createUserWithEmailAndPassword(edEmail.getText().toString().trim(), edPasswd.getText().toString().trim())
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
        edEmail = (EditText) findViewById(R.id.ed_email);
        edPasswd = (EditText) findViewById(R.id.ed_passwd);
        edConfirmPasswd = (EditText) findViewById(R.id.ed_confirmPasswd);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        btnDangky = (Button) findViewById(R.id.btn_dangky);

    }
}