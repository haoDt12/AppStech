package com.datn.shopsale.ui.dashboard.setting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.request.ChangPassRequest;
import com.datn.shopsale.responsev2.ChangePassResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassActivity extends AppCompatActivity {
    private TextInputEditText edPass, edPassNew, edRepassNew;
    private Button btnSend;
    private ApiService apiService;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        init();
    }

    private void init() {
        Toolbar toolbarChangePass = findViewById(R.id.toolbar_change_pass);
        edPass = findViewById(R.id.ed_pass);
        edPassNew = findViewById(R.id.ed_passNew);
        edRepassNew = findViewById(R.id.ed_RepassNew);
        btnSend = findViewById(R.id.btn_send);
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        setSupportActionBar(toolbarChangePass);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarChangePass.setNavigationOnClickListener(v -> onBackPressed());
        onClickSend();
    }

    // check rỗng
    private boolean isFieldEmpty(TextInputEditText editText) {
        String text = Objects.requireNonNull(editText.getText()).toString().trim();
        return TextUtils.isEmpty(text);
    }

    private static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void editPassword(String currentPass, String newPass) {
        LoadingDialog.showProgressDialog(this, "Loading...");
        ChangPassRequest request = new ChangPassRequest();
        request.setCurrentPass(currentPass);
        request.setNewPass(newPass);
        Call<ChangePassResponse> call = apiService.sendOtpEditPass(preferenceManager.getString("token"), request);
        call.enqueue(new Callback<ChangePassResponse>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onResponse(@NonNull Call<ChangePassResponse> call, @NonNull Response<ChangePassResponse> response) {
                runOnUiThread(() -> {
                    assert response.body() != null;
                    LoadingDialog.dismissProgressDialog();
                    if (response.body().getCode() == 1) {
                        Dialog dialog = new Dialog(ChangePassActivity.this);
                        dialog.setContentView(R.layout.dialog_otp_change_password);
                        Window window = dialog.getWindow();
                        assert window != null;
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(ChangePassActivity.this.getDrawable(R.drawable.dialog_bg));
                        window.getAttributes().windowAnimations = R.style.DialogAnimation;
                        WindowManager.LayoutParams windowAttributes = window.getAttributes();
                        window.setAttributes(windowAttributes);
                        windowAttributes.gravity = Gravity.BOTTOM;
                        TextInputEditText edOtp = dialog.findViewById(R.id.ed_otp);
                        Button btnSendOtp = dialog.findViewById(R.id.btn_sendOtp);
                        ImageButton btnCancel = dialog.findViewById(R.id.btn_cancel);
                        btnCancel.setOnClickListener(view -> dialog.dismiss());
                        btnSendOtp.setOnClickListener(view -> {
                            if (isFieldEmpty(edOtp)) {
                                Toast.makeText(ChangePassActivity.this, "otp không được để trống", Toast.LENGTH_SHORT).show();
                            }
                            senOtpPassword(Objects.requireNonNull(edOtp.getText()).toString()
                                    , Objects.requireNonNull(edPass.getText()).toString()
                                    , Objects.requireNonNull(edPassNew.getText()).toString());
                        });
                        dialog.show();
                    } else {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(ChangePassActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(ChangePassActivity.this, response.body().getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ChangePassResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(ChangePassActivity.this, t.getMessage());
                });
            }
        });
    }

    private void senOtpPassword(String otp, String currentPass, String newPass) {
        LoadingDialog.showProgressDialog(this, "Loading...");
        ChangPassRequest request = new ChangPassRequest();
        request.setOtp(otp);
        request.setCurrentPass(currentPass);
        request.setNewPass(newPass);
        Call<ChangePassResponse> call = apiService.editPass(preferenceManager.getString("token"), request);
        call.enqueue(new Callback<ChangePassResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChangePassResponse> call, @NonNull Response<ChangePassResponse> response) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    assert response.body() != null;
                    if (response.body().getCode() == 1) {
                        new AlertDialog.Builder(ChangePassActivity.this)
                                .setTitle("Notification")
                                .setMessage(response.body().getMessage())
                                .setPositiveButton("OK", (dialog1, which) -> finish())
                                .show();
                    } else {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(ChangePassActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(ChangePassActivity.this, response.body().getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ChangePassResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialogWithOk(ChangePassActivity.this, t.getMessage());
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
    }

    private void onClickSend() {
        btnSend.setOnClickListener(v -> {
            if (isFieldEmpty(edPass) || isFieldEmpty(edPassNew) || isFieldEmpty(edRepassNew)) {
                Toast.makeText(this, "Không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidPassword(Objects.requireNonNull(edPassNew.getText()).toString())) {
                Toast.makeText(this, "Mật khẩu tối thiểu 8 ký tự, ít nhất 1 chữ in hoa, 1 số và 1 ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!edPassNew.getText().toString().equals(Objects.requireNonNull(edRepassNew.getText()).toString())) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
            editPassword(Objects.requireNonNull(edPass.getText()).toString(), edPassNew.getText().toString());
        });
    }
}