package com.datn.shopsale.pay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;

import com.datn.shopsale.R;

public class ChooseBankActivity extends AppCompatActivity {
    private Toolbar toolbarChoseBank;
    private ImageView imgVietcombank;
    private ImageView imgViettinbank;
    private ImageView imgBidv;
    private ImageView imgAgribank;
    private ImageView imgTpbank;
    private ImageView imgMbbank;
    private ImageView imgTechcombank;
    private ImageView imgAcbank;
    private ImageView imgBacabank;
    private ImageView imgBaovietbank;
    private ImageView imgNâmbank;
    private ImageView imgVpbank;
    private ImageView imgHsbcbank;
    private ImageView imgVibank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bank);

        init();
    }
    void init(){
        toolbarChoseBank = (Toolbar) findViewById(R.id.toolbar_chose_bank);
        imgVietcombank = (ImageView) findViewById(R.id.img_vietcombank);
        imgViettinbank = (ImageView) findViewById(R.id.img_viettinbank);
        imgBidv = (ImageView) findViewById(R.id.img_bidv);
        imgAgribank = (ImageView) findViewById(R.id.img_agribank);
        imgTpbank = (ImageView) findViewById(R.id.img_tpbank);
        imgMbbank = (ImageView) findViewById(R.id.img_mbbank);
        imgTechcombank = (ImageView) findViewById(R.id.img_techcombank);
        imgAcbank = (ImageView) findViewById(R.id.img_acbank);
        imgBacabank = (ImageView) findViewById(R.id.img_bacabank);
        imgBaovietbank = (ImageView) findViewById(R.id.img_baovietbank);
        imgNâmbank = (ImageView) findViewById(R.id.img_nâmbank);
        imgVpbank = (ImageView) findViewById(R.id.img_vpbank);
        imgHsbcbank = (ImageView) findViewById(R.id.img_hsbcbank);
        imgVibank = (ImageView) findViewById(R.id.img_vibank);
        setSupportActionBar(toolbarChoseBank);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarChoseBank.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }
}