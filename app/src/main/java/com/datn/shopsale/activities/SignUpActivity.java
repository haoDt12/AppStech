package com.datn.shopsale.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
            onClickSignUp();
        });
    }

    private void onClickSignUp() {
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
                                    } else {
                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void inutUI() {
        edEmail = (EditText) findViewById(R.id.ed_email);
        edPasswd = (EditText) findViewById(R.id.ed_passwd);
        edConfirmPasswd = (EditText) findViewById(R.id.ed_confirmPasswd);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        btnDangky = (Button) findViewById(R.id.btn_dangky);

    }
}