package com.datn.shopsale.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.ListOder;
import com.datn.shopsale.request.OderRequest;
import com.datn.shopsale.request.OrderVnPayRequest;
import com.datn.shopsale.response.VnPayResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EBankingPayActivity extends AppCompatActivity {
    private WebView webViewPay;
    private PreferenceManager preferenceManager;
    private ListOder listOder;
    private String address;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebanking_pay);
        listOder = new ListOder();
        preferenceManager = new PreferenceManager(this);
        apiService = RetrofitConnection.getApiService();
        webViewPay = (WebView) findViewById(R.id.web_view_pay);
        address = preferenceManager.getString("addressOrder");
        Intent intent = getIntent();
        if (intent.hasExtra("listOder")) {
            listOder = (ListOder) intent.getSerializableExtra("listOder");
        }
        WebSettings webSettings = webViewPay.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        onCallApiPay();
    }

    private void onCallApiPay() {
        List<OderRequest.Product> listProduct = new ArrayList<>();
        for (Cart item : listOder.getList()) {
            listProduct.add(new OderRequest.Product(item.getProductId(), item.getColor(), item.getRam_rom(), item.getQuantity()));
        }
        OrderVnPayRequest.Root request = new OrderVnPayRequest.Root();
        request.setProduct(listProduct);
        request.setUserId(preferenceManager.getString("userId"));
        request.setAddress(address);
        request.setAmount("");
        request.setBankCode("");
        request.setLanguage("vn");
        Call<VnPayResponse> call = apiService.createOrderVnPay(preferenceManager.getString("token"), request);
        call.enqueue(new Callback<VnPayResponse>() {
            @Override
            public void onResponse(@NonNull Call<VnPayResponse> call, @NonNull Response<VnPayResponse> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    runOnUiThread(() -> {
                        Toast.makeText(EBankingPayActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
                        Log.d("url pay", "onResponse: " + response.body().getUrl());
                        webViewPay.loadUrl(response.body().getUrl());
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(EBankingPayActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<VnPayResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(EBankingPayActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showAlertDialog(String mess, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Noification");
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