package com.datn.shopsale.activities;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.datn.shopsale.R;

public class EBankingPayActivity extends AppCompatActivity {
    private WebView webViewPay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebanking_pay);
        webViewPay = (WebView) findViewById(R.id.web_view_pay);
        webViewPay.setVisibility(View.VISIBLE);
        WebSettings webSettings = webViewPay.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webViewPay.setWebViewClient(new WebViewClient());
        webViewPay.loadUrl("https://www.youtube.com");
    }
}