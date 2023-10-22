package com.datn.shopsale.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datn.shopsale.R;

public class InformationUserActivity extends AppCompatActivity {
    private ImageButton imgBack;
    private ImageView imgCamera;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvLocation;
    private EditText edName;
    private EditText edEmail;
    private EditText edPhone;
    private EditText edLocation;
    private Button btnSignUp;
    private ImageView imgUpdate;
    private LinearLayout lnlLayoutText;
    private LinearLayout lnlLayoutEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);

        FindViewById();
        imgBack.setOnClickListener(view -> {
            super.onBackPressed();
        });
        imgUpdate.setOnClickListener(view -> {
            imgUpdate.setVisibility(View.INVISIBLE);
            lnlLayoutText.setVisibility(View.INVISIBLE);
            lnlLayoutEdit.setVisibility(View.VISIBLE);
        });
    }

    private void FindViewById() {
        imgBack = (ImageButton) findViewById(R.id.img_back);
        imgCamera = (ImageView) findViewById(R.id.img_camera);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        edName = (EditText) findViewById(R.id.ed_name);
        edEmail = (EditText) findViewById(R.id.ed_email);
        edPhone = (EditText) findViewById(R.id.ed_phone);
        edLocation = (EditText) findViewById(R.id.ed_location);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        imgUpdate = (ImageView) findViewById(R.id.img_update);
        lnlLayoutText = (LinearLayout) findViewById(R.id.lnl_layout_text);
        lnlLayoutEdit = (LinearLayout) findViewById(R.id.lnl_layout_edit);

    }
}