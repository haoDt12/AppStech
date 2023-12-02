package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.models.FeedBack;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.login.SignUpActivity;
import com.datn.shopsale.ui.login.VerifyOTPActivity;
import com.datn.shopsale.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class add_review_activity extends AppCompatActivity {
    private LinearLayout rating1;
    private LinearLayout rating2;
    private LinearLayout rating3;
    private LinearLayout rating4;
    private LinearLayout rating5;
    private LinearLayout lnlToolBar;
    private ImageButton imgBack;
    private TextView tvSubmit;
    private RelativeLayout layoutInformation;
    private ImageView imgProduct;
    private TextView tvName;
    private TextView tvDescription;
    private TextView tvFeedback;
    private LinearLayout layoutStar;
    private TextView tvComment;
    private EditText edComment;
    private String idProduct;
    private ApiService apiService;
    PreferenceManager preferenceManager ;
    private int rating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        findId();
        preferenceManager = new PreferenceManager(this);
        apiService = RetrofitConnection.getApiService();
        idProduct = getIntent().getStringExtra("id");
        Glide.with(this).load(getIntent().getStringExtra("image")).into(imgProduct);
        tvName.setText(getIntent().getStringExtra("name"));
        tvDescription.setText(getIntent().getStringExtra("color")+"    "+ getIntent().getStringExtra("ram"));
        Toast.makeText(this, ""+getIntent().getStringExtra("name"), Toast.LENGTH_SHORT).show();
        imgBack.setOnClickListener(view -> {
            finish();
        });
        tvSubmit.setOnClickListener(view -> {
            addFeedback();
        });
        rating1.setOnClickListener(view -> {
            rating = 5;
        });
        rating2.setOnClickListener(view -> {
            rating = 4;
        });
        rating3.setOnClickListener(view -> {
            rating = 3;
        });
        rating4.setOnClickListener(view -> {
            rating = 2;
        });
        rating5.setOnClickListener(view -> {
            rating = 1;
        });
    }



    private void addFeedback() {
        String idUser = preferenceManager.getString("userId");
        FeedBack objFeedBack = new FeedBack();
        objFeedBack.setComment(edComment.getText().toString());
        objFeedBack.setRating(rating);
        objFeedBack.setProductId(idProduct);
        objFeedBack.setUserId(idUser);
        objFeedBack.setAvtUser(preferenceManager.getString("avatarLogin"));
        objFeedBack.setNameUser(preferenceManager.getString("nameLogin"));
        Call<ResApi> call = apiService.addCmt(preferenceManager.getString("token"),objFeedBack);
        call.enqueue(new Callback<ResApi>() {
            @Override
            public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                if (response.body().code ==1){
                    Toast.makeText(add_review_activity.this, "FeedBack thanh cong", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(add_review_activity.this, response.body().message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResApi> call, Throwable t) {
                Log.e("Error", "onFailure: " + t);
                Toast.makeText(add_review_activity.this, "error: "+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findId() {



        rating1 = (LinearLayout) findViewById(R.id.rating1);
        rating2 = (LinearLayout) findViewById(R.id.rating2);
        rating3 = (LinearLayout) findViewById(R.id.rating3);
        rating4 = (LinearLayout) findViewById(R.id.rating4);
        rating5 = (LinearLayout) findViewById(R.id.rating5);

        lnlToolBar = (LinearLayout) findViewById(R.id.lnl_tool_bar);
        imgBack = (ImageButton) findViewById(R.id.img_back);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);
        layoutInformation = (RelativeLayout) findViewById(R.id.layout_information);
        imgProduct = (ImageView) findViewById(R.id.img_product);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        tvFeedback = (TextView) findViewById(R.id.tv_feedback);
        layoutStar = (LinearLayout) findViewById(R.id.layout_star);
        tvComment = (TextView) findViewById(R.id.tv_comment);
        edComment = (EditText) findViewById(R.id.ed_comment);

    }
}