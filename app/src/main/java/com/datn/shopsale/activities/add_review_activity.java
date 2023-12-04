package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

public class add_review_activity extends AppCompatActivity{
    private Toolbar toolbarFeedback;
    private TextView tvSubmit;
    private ImageView imgProduct;
    private TextView tvName;
    private TextView tvDescription;
    private RatingBar ratingBar;
    private EditText edComment;
    private String idProduct;
    private ApiService apiService;
    PreferenceManager preferenceManager ;
    private double rating_result;
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

        tvSubmit.setOnClickListener(view -> {
            addFeedback();
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Xử lý sự kiện khi giá trị đánh giá thay đổi
                rating_result = ratingBar.getRating();
            }
        });
    }
    private void addFeedback() {
        String idUser = preferenceManager.getString("userId");
        FeedBack objFeedBack = new FeedBack();
        objFeedBack.setComment(edComment.getText().toString());
        objFeedBack.setRating((int) rating_result);
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
        toolbarFeedback = (Toolbar) findViewById(R.id.toolbar_feedback);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);
        imgProduct = (ImageView) findViewById(R.id.img_product);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        edComment = (EditText) findViewById(R.id.ed_comment);

        setSupportActionBar(toolbarFeedback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarFeedback.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        LayerDrawable starsDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
        starsDrawable.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        starsDrawable.getDrawable(0).setColorFilter(getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);
    }
}