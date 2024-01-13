package com.datn.shopsale.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.request.CreateOrderRequest;
import com.datn.shopsale.response.VnPayResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EBankingPayActivity extends AppCompatActivity {
    private WebView webViewPay;
    private PreferenceManager preferenceManager;
    private CreateOrderRequest request;
    private ApiService apiService;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebanking_pay);
        preferenceManager = new PreferenceManager(this);
        apiService = RetrofitConnection.getApiService();
        webViewPay = findViewById(R.id.web_view_pay);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        request = (CreateOrderRequest) bundle.getSerializable("requestOrderVnPay");
        WebSettings webSettings = webViewPay.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        onCallApiPay();
    }

    private void onCallApiPay() {
        LoadingDialog.showProgressDialog(this, "Loading...");
        Call<VnPayResponse> call = apiService.createOrderVnPayV2(preferenceManager.getString("token"), request);
        call.enqueue(new Callback<VnPayResponse>() {
            @Override
            public void onResponse(@NonNull Call<VnPayResponse> call, @NonNull Response<VnPayResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    runOnUiThread(() -> {
                        webViewPay.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                String url = request.getUrl().toString();
                                if (url.contains("/paySuccess")) {
                                    showAlertDialog("Pay success", "1");
                                    return true;
                                }
                                if (url.contains("/payFail")) {
                                    showAlertDialog("Pay fail", "0");
                                    finish();
                                    return true;
                                }
                                return super.shouldOverrideUrlLoading(view, request);
                            }
                        });
                        webViewPay.loadUrl(response.body().getUrl());
                    });
                } else {
                    if (response.body().getMessage().equals("wrong token")) {
                        CheckLoginUtil.gotoLogin(EBankingPayActivity.this, response.body().getMessage());
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(EBankingPayActivity.this, response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<VnPayResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialogWithOk(EBankingPayActivity.this, t.getMessage());
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
    }

    private void showAlertDialog(String mess, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification");
        builder.setMessage(mess);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("action", action);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}