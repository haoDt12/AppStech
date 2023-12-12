package com.datn.shopsale.ui.dashboard.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.request.EditPassRequest;
import com.datn.shopsale.response.EditPasswordResponse;
import com.datn.shopsale.response.VerifyOtpEditPassResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassActivity extends AppCompatActivity {
    private TextInputEditText edPass,edPassNew,edRepassNew;
    private TextView tvError;
    private Toolbar toolbarChangePass;
    private Button btnSend;
    private ApiService apiService;
    private PreferenceManager preferenceManager;

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
        toolbarChangePass = (Toolbar) findViewById(R.id.toolbar_change_pass);
        edPass = (TextInputEditText) findViewById(R.id.ed_pass);
        edPassNew = (TextInputEditText) findViewById(R.id.ed_passNew);
        edRepassNew = (TextInputEditText) findViewById(R.id.ed_RepassNew);
        btnSend = (Button) findViewById(R.id.btn_send);
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        setSupportActionBar(toolbarChangePass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarChangePass.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        onClickSend();
    }
    // check rỗng
    private boolean isFieldEmpty(TextInputEditText editText) {
        String text = editText.getText().toString().trim();
        return TextUtils.isEmpty(text);
    }
    private static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    private void editPassword(String currentPass,String newPass){
        LoadingDialog.showProgressDialog(this,"Loading...");
        EditPassRequest request = new EditPassRequest();
        request.setUserId(preferenceManager.getString("userId"));
        request.setCurrentPass(currentPass);
        request.setNewPass(newPass);
        Call<EditPasswordResponse> call = apiService.editPassword(preferenceManager.getString("token"),request);
        call.enqueue(new Callback<EditPasswordResponse>() {
            @Override
            public void onResponse(@NonNull Call<EditPasswordResponse> call, @NonNull Response<EditPasswordResponse> response) {
                runOnUiThread(() -> {
                    assert response.body() != null;
                    LoadingDialog.dismissProgressDialog();
                    if(response.body().getCode() == 1){
                        Dialog dialog = new Dialog(ChangePassActivity.this);
                        dialog.setContentView(R.layout.dialog_otp_change_password);
                        Window window = dialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(ChangePassActivity.this.getDrawable(R.drawable.dialog_bg));
                        window.getAttributes().windowAnimations = R.style.DialogAnimation;
                        WindowManager.LayoutParams windowAttributes = window.getAttributes();
                        window.setAttributes(windowAttributes);
                        windowAttributes.gravity = Gravity.BOTTOM;
                        TextInputEditText edOtp = dialog.findViewById(R.id.ed_otp);
                        Button btnSendOtp = dialog.findViewById(R.id.btn_sendOtp);
                        ImageButton btnCancel = dialog.findViewById(R.id.btn_cancel);
                        btnCancel.setOnClickListener(view -> {
                            dialog.dismiss();
                        });
                        btnSendOtp.setOnClickListener(view -> {
                            if(isFieldEmpty(edOtp)){
                                Toast.makeText(ChangePassActivity.this, "otp không được để trống", Toast.LENGTH_SHORT).show();
                            }
                            senOtpPassword(edOtp.getText().toString());
                        });
                        dialog.show();
                    }else {
                        AlertDialogUtil.showSimpleAlertDialog(ChangePassActivity.this,response.body().getMessage());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<EditPasswordResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(ChangePassActivity.this,t.getMessage());
                });
            }
        });
    }
    private void senOtpPassword(String otp){
        LoadingDialog.showProgressDialog(this,"Loading...");
        EditPassRequest request = new EditPassRequest();
        request.setUserId(preferenceManager.getString("userId"));
        request.setOtp(otp);
        Call<VerifyOtpEditPassResponse> call = apiService.sendOtpPassword(preferenceManager.getString("token"),request);
        call.enqueue(new Callback<VerifyOtpEditPassResponse>() {
            @Override
            public void onResponse(@NonNull Call<VerifyOtpEditPassResponse> call, @NonNull Response<VerifyOtpEditPassResponse> response) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    assert response.body() != null;
                    if(response.body().getCode() == 1){
                        new AlertDialog.Builder(ChangePassActivity.this)
                                .setTitle("Notification")
                                .setMessage(response.body().getMessage())
                                .setPositiveButton("OK", (dialog1, which) -> finish())
                                .show();
                    }else {
                        AlertDialogUtil.showSimpleAlertDialog(ChangePassActivity.this,response.body().getMessage());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<VerifyOtpEditPassResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialogWithOk(ChangePassActivity.this,t.getMessage());
                });
            }
        });
    }
    private void onClickSend(){
        btnSend.setOnClickListener(v->{
            if(isFieldEmpty(edPass) || isFieldEmpty(edPassNew) || isFieldEmpty(edRepassNew)){
                Toast.makeText(this, "Không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!isValidPassword(edPassNew.getText().toString())){
                Toast.makeText(this, "Mật khẩu tối thiểu 8 ký tự, ít nhất 1 chữ in hoa, 1 số và 1 ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!edPassNew.getText().toString().equals(edRepassNew.getText().toString())){
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
//            else{
//                changePassword();
//            }
            editPassword(edPass.getText().toString(),edPassNew.getText().toString());
        });
    }
}