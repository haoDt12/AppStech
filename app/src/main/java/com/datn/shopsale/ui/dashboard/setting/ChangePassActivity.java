package com.datn.shopsale.ui.dashboard.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.datn.shopsale.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends AppCompatActivity {
    private TextInputEditText edPass,edPassNew,edRepassNew;
    private TextView tvError;
    private ImageButton imgBack;

    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        init();
    }
    private void changePassword() {
        String currentPassword = edPass.getText().toString();
        String newPassword = edPassNew.getText().toString();
        String reNewPassword = edRepassNew.getText().toString();

        if (newPassword.equals(reNewPassword)) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                // xác minh mật khẩu
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // xác minh thành công
                                    user.updatePassword(newPassword)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ChangePassActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(ChangePassActivity.this, "Mật khẩu phải có ký tự >= 6", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // Mật khẩu hiện tại xác minh thất bại
                                    Toast.makeText(ChangePassActivity.this, "Xác minh mật khẩu hiện tại thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } else {
            // mật khẩu mới và nhập lại
            Toast.makeText(ChangePassActivity.this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
        }
    }
    private void init(){
        imgBack = (ImageButton) findViewById(R.id.img_back);
        edPass = (TextInputEditText) findViewById(R.id.ed_pass);
        edPassNew = (TextInputEditText) findViewById(R.id.ed_passNew);
        edRepassNew = (TextInputEditText) findViewById(R.id.ed_RepassNew);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(v->{
            if(isFieldEmpty(edPass) || isFieldEmpty(edPassNew) || isFieldEmpty(edRepassNew)){
                Toast.makeText(this, "Không được để trống", Toast.LENGTH_SHORT).show();
            }else{
                changePassword();
            }
        });
        imgBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }
    // check rỗng
    private boolean isFieldEmpty(TextInputEditText editText) {
        String text = editText.getText().toString().trim();
        return TextUtils.isEmpty(text);
    }
}