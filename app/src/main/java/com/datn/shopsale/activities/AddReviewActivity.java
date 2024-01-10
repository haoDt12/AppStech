package com.datn.shopsale.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.responsev2.FeedBackResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReviewActivity extends AppCompatActivity {
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
        assert url != null;
        Glide.with(this).load(GetImgIPAddress.convertLocalhostToIpAddress(url)).into(imgProduct);
        tvName.setText(getIntent().getStringExtra("name"));
        String color = getIntent().getStringExtra("color");
        String option = getIntent().getStringExtra("ram");
        if (option == null) {
            option = "";
        }
        tvDescription.setText(String.format("%s\n%s", color, option));
        Toast.makeText(this, String.format("%s", getIntent().getStringExtra("name")), Toast.LENGTH_SHORT).show();

        tvSubmit.setOnClickListener(view -> {
            addFeedback();
            finish();
        });
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            rating_result = ratingBar.getRating();
            int text = (int) rating_result;
            tvRating.setText(String.format("%s/5", text));
        });
    }

    private void addFeedback() {
        LoadingDialog.showProgressDialog(this, "Loading...");
        Call<FeedBackResponse> call = apiService.addFeedback(preferenceManager.getString("token"),
                preferenceManager.getString("userId"), idProduct, String.valueOf(rating_result), edComment.getText().toString().trim()
        );
        call.enqueue(new Callback<FeedBackResponse>() {
            @Override
            public void onResponse(@NonNull Call<FeedBackResponse> call, @NonNull Response<FeedBackResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    Toast.makeText(AddReviewActivity.this, "Đánh giá sản phẩm thành công", Toast.LENGTH_SHORT).show();
                } else {
                    runOnUiThread(() -> {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(AddReviewActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(AddReviewActivity.this, response.body().getMessage());
                        }
                    });

                }
            }

            @Override
            public void onFailure(@NonNull Call<FeedBackResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialogWithOk(AddReviewActivity.this, t.getMessage());
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
    }

    private void findId() {
        Toolbar toolbarFeedback = findViewById(R.id.toolbar_feedback);
        ratingBar = findViewById(R.id.ratingBar);
        tvSubmit = findViewById(R.id.tv_submit);
        imgProduct = findViewById(R.id.img_product);
        tvName = findViewById(R.id.tv_name);
        tvRating = findViewById(R.id.tv_rating);
        tvDescription = findViewById(R.id.tv_description);
        edComment = findViewById(R.id.ed_comment);
        setSupportActionBar(toolbarFeedback);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarFeedback.setNavigationOnClickListener(v -> onBackPressed());
        LayerDrawable starsDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
        starsDrawable.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        starsDrawable.getDrawable(0).setColorFilter(getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);
    }
}