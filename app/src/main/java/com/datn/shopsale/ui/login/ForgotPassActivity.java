package com.datn.shopsale.ui.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
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
import com.datn.shopsale.response.GetPassResponse;
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

public class ForgotPassActivity extends AppCompatActivity {
    private TextInputEditText edEmail;
    private PreferenceManager preferenceManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        init();
    }

    private void init() {
        Toolbar toolbarForgotPass = findViewById(R.id.toolbar_forgot_pass);
        edEmail = findViewById(R.id.ed_email);
        Button btnSend = findViewById(R.id.btn_send);
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        btnSend.setOnClickListener(v -> {
            String email = Objects.requireNonNull(edEmail.getText()).toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidEmail(email) && !isNumeric(email)) {
                Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidPhoneNumber(email) && isNumeric(email)) {
                Toast.makeText(this, "Số điện thoại không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }
            onResetPass(email);
        });
        setSupportActionBar(toolbarForgotPass);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarForgotPass.setNavigationOnClickListener(v -> onBackPressed());
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        String phoneNumberRegex = "^(?:\\+84|0)[1-9]\\d{8}$";
        Pattern pattern = Pattern.compile(phoneNumberRegex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void onResetPass(String username) {
        LoadingDialog.showProgressDialog(ForgotPassActivity.this, "Loading...");
        Call<GetPassResponse> call = apiService.getPassWord(preferenceManager.getString("token"), username);
        call.enqueue(new Callback<GetPassResponse>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onResponse(@NonNull Call<GetPassResponse> call, @NonNull Response<GetPassResponse> response) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    assert response.body() != null;
                    if (response.body().getCode() == 1) {
                        Dialog dialog = new Dialog(ForgotPassActivity.this);
                        dialog.setContentView(R.layout.dialog_check_email);
                        Window window = dialog.getWindow();
                        assert window != null;
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(ForgotPassActivity.this.getDrawable(R.drawable.dialog_bg));
                        window.getAttributes().windowAnimations = R.style.DialogAnimation;
                        WindowManager.LayoutParams windowAttributes = window.getAttributes();
                        window.setAttributes(windowAttributes);
                        windowAttributes.gravity = Gravity.BOTTOM;
                        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
                        ImageButton btnCancel = dialog.findViewById(R.id.btn_cancel);
                        btnCancel.setOnClickListener(view -> dialog.dismiss());
                        btnConfirm.setOnClickListener(view -> {
                            dialog.dismiss();
                            finish();
                        });
                        dialog.show();
                    } else {
                        if(response.body().getMessage().equals("wrong token")){
                            CheckLoginUtil.gotoLogin(ForgotPassActivity.this,response.body().getMessage());
                        }else {
                            AlertDialogUtil.showAlertDialogWithOk(ForgotPassActivity.this, response.body().getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GetPassResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(ForgotPassActivity.this, t.getMessage());
                });
            }
        });
    }
}