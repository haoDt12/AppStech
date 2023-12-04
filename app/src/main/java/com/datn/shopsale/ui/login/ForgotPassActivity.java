package com.datn.shopsale.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.datn.shopsale.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {
    private TextInputEditText edEmail;
    private Button btnSend;
    private Toolbar toolbarForgotPass;
    private ProgressBar progressBar;
    private static final int PROGRESS_DELAY = 5000; // Thời gian đặt trước 5 giây
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        init();
    }
    public void resetUserPassword(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar khi bắt đầu

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Sử dụng Handler để đặt trễ 5 giây trước khi ẩn ProgressBar
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE); // Ẩn ProgressBar khi hoàn thành
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Đã gửi xác nhận đến Email",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Email không tồn tại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, PROGRESS_DELAY);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE); // Ẩn ProgressBar nếu xảy ra lỗi
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void init(){
        toolbarForgotPass = (Toolbar) findViewById(R.id.toolbar_forgot_pass);
        edEmail = (TextInputEditText) findViewById(R.id.ed_email);
        btnSend = (Button) findViewById(R.id.btn_send);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSend.setOnClickListener(v->{
            String email = edEmail.getText().toString().trim();
            if(email.isEmpty()){
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            }else{
                resetUserPassword(email);
            }
        });
        setSupportActionBar(toolbarForgotPass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarForgotPass.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }
}