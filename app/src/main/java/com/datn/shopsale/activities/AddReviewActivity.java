package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.models.FeedBack;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.responsev2.FeedBackResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReviewActivity extends AppCompatActivity {
    private Toolbar toolbarFeedback;
    private TextView tvSubmit;
    private ImageView imgProduct;
    private TextView tvName;
    private TextView tvDescription;
    private RatingBar ratingBar;
    private EditText edComment;
    private String idProduct;
    private ApiService apiService;
    private TextView tvRating;
    PreferenceManager preferenceManager;
    private double rating_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        findId();
        preferenceManager = new PreferenceManager(this);
        apiService = RetrofitConnection.getApiService();
        idProduct = getIntent().getStringExtra("id");
        String url = getIntent().getStringExtra("image");
        Glide.with(this).load(GetImgIPAddress.convertLocalhostToIpAddress(url)).into(imgProduct);
        tvName.setText(getIntent().getStringExtra("name"));
        String color = getIntent().getStringExtra("color");
        String option = getIntent().getStringExtra("ram");
        if (option == null) {
            option = "";
        }
        tvDescription.setText(color + "\n" + option);
        Toast.makeText(this, "" + getIntent().getStringExtra("name"), Toast.LENGTH_SHORT).show();

        tvSubmit.setOnClickListener(view -> {
            addFeedback();
            finish();
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Xử lý sự kiện khi giá trị đánh giá thay đổi
                rating_result = ratingBar.getRating();
                int text = (int) rating_result;
                tvRating.setText(String.valueOf(text) + "/5");

            }
        });
    }

    private void addFeedback() {
        try {
            Call<FeedBackResponse> call = apiService.addFeedback(preferenceManager.getString("token"),
                    preferenceManager.getString("userId"), idProduct, String.valueOf(rating_result), edComment.getText().toString().trim()
            );
            call.enqueue(new Callback<FeedBackResponse>() {
                @Override
                public void onResponse(Call<FeedBackResponse> call, Response<FeedBackResponse> response) {
                    if (response.body().getCode() == 1) {
                        Toast.makeText(AddReviewActivity.this, "Đánh giá sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(AddReviewActivity.this, response.body().getMessage());

                    }
                }

                @Override
                public void onFailure(Call<FeedBackResponse> call, Throwable t) {
                    runOnUiThread(() -> AlertDialogUtil.showAlertDialogWithOk(AddReviewActivity.this, t.getMessage()));

                }
            });
        } catch (Exception e) {
            Log.e("Error", "onFailure: " + e);
            Toast.makeText(AddReviewActivity.this, "error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void findId() {
        toolbarFeedback = (Toolbar) findViewById(R.id.toolbar_feedback);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);
        imgProduct = (ImageView) findViewById(R.id.img_product);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvRating = (TextView) findViewById(R.id.tv_rating);
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