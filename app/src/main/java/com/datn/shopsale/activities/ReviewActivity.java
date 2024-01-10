package com.datn.shopsale.activities;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ReviewAdapter;
import com.datn.shopsale.modelsv2.FeedBack;
import com.datn.shopsale.responsev2.FeedBackResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {
    private TextView TBC;
    private float sumTBC;
    private TextView tvReview;
    private ProgressBar progress5;
    private TextView tv5;
    private ProgressBar progress4;
    private TextView tv4;
    private ProgressBar progress3;
    private TextView tv3;
    private ProgressBar progress2;
    private TextView tv2;
    private ProgressBar progress1;
    private TextView tv1;
    private RecyclerView rcvReview;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private List<com.datn.shopsale.modelsv2.FeedBack> listFb;
    private ReviewAdapter adapter;
    private String id;
    private double count1 = 0;
    private double count2 = 0;
    private double count3 = 0;
    private double count4 = 0;
    private double count5 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        initUi();
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        id = getIntent().getStringExtra("id");
        getCmt();
    }

    private void getCmt() {
        LoadingDialog.showProgressDialog(this,"Loading...");
        listFb = new ArrayList<>();
        Call<FeedBackResponse> call = apiService.getAllFeedBack(preferenceManager.getString("token"), id);
        call.enqueue(new Callback<FeedBackResponse>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<FeedBackResponse> call, @NonNull Response<FeedBackResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    for (FeedBack objFeedBack : response.body().getListFeedBack()) {
                        FeedBack feedBack = new FeedBack(
                                objFeedBack.getCustomer_id(),
                                objFeedBack.getProduct_id(), objFeedBack.getRating(),
                                objFeedBack.getComment(),
                                objFeedBack.getCreate_time()
                        );

                        listFb.add(feedBack);
                    }
                    runOnUiThread(() -> {
                        float tong = 0;
                        for (FeedBack objFeedBack : listFb) {
                            tong += Float.parseFloat(objFeedBack.getRating());
                        }
                        if (listFb.size() == 0) {
                            sumTBC = 0;
                        } else {
                            sumTBC = tong / listFb.size();
                        }
                        adapter = new ReviewAdapter(listFb, getApplicationContext());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        rcvReview.setLayoutManager(linearLayoutManager);
                        rcvReview.setAdapter(adapter);
                        tvReview.setText(String.format("%d Reviews", listFb.size()));
                        for (int i = 0; i < listFb.size(); i++) {

                            if (listFb.get(i).getRating().equals("1.0")) {
                                count1++;
                            }
                            if (listFb.get(i).getRating().equals("2.0")) {
                                count2++;
                            }
                            if (listFb.get(i).getRating().equals("3.0")) {
                                count3++;
                            }
                            if (listFb.get(i).getRating().equals("4.0")) {
                                count4++;
                            }
                            if (listFb.get(i).getRating().equals("5.0")) {
                                count5++;
                            }
                        }
                        String formattedText = String.format("%.1f", sumTBC);
                        TBC.setText(String.format("%s/5", formattedText));

                        tv1.setText(String.format("%d", Math.round(count1)));
                        tv2.setText(String.format("%d", Math.round(count2)));
                        tv3.setText(String.format("%d", Math.round(count3)));
                        tv4.setText(String.format("%d", Math.round(count4)));
                        tv5.setText(String.format("%d", Math.round(count5)));
                        progress1.setProgress((int) Math.round((count1 / listFb.size()) * 100));
                        progress2.setProgress((int) Math.round((count2 / listFb.size()) * 100));
                        progress3.setProgress((int) Math.round((count3 / listFb.size()) * 100));
                        progress4.setProgress((int) Math.round((count4 / listFb.size()) * 100));
                        progress5.setProgress((int) Math.round((count5 / listFb.size()) * 100));

                    });
                } else {
                    runOnUiThread(() -> {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(ReviewActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(ReviewActivity.this, response.body().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<FeedBackResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialogWithOk(ReviewActivity.this, t.getMessage());
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });

    }

    private void initUi() {
        TBC = findViewById(R.id.TBC);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        tvReview = findViewById(R.id.tv_review);
        progress5 = findViewById(R.id.progress5);
        tv5 = findViewById(R.id.tv5);
        progress4 = findViewById(R.id.progress4);
        tv4 = findViewById(R.id.tv4);
        progress3 = findViewById(R.id.progress3);
        tv3 = findViewById(R.id.tv3);
        progress2 = findViewById(R.id.progress2);
        tv2 = findViewById(R.id.tv2);
        progress1 = findViewById(R.id.progress1);
        tv1 = findViewById(R.id.tv1);
        rcvReview = findViewById(R.id.recy_review);
        Toolbar toolbarReview = findViewById(R.id.toolbar_review);
        setSupportActionBar(toolbarReview);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarReview.setNavigationOnClickListener(v -> onBackPressed());
        LayerDrawable starsDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
        LayerDrawable progressDrawable1 = (LayerDrawable) progress1.getProgressDrawable();
        LayerDrawable progressDrawable2 = (LayerDrawable) progress2.getProgressDrawable();
        LayerDrawable progressDrawable3 = (LayerDrawable) progress3.getProgressDrawable();
        LayerDrawable progressDrawable4 = (LayerDrawable) progress4.getProgressDrawable();
        LayerDrawable progressDrawable5 = (LayerDrawable) progress5.getProgressDrawable();
        starsDrawable.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        starsDrawable.getDrawable(0).setColorFilter(getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);
        progressDrawable1.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        progressDrawable1.getDrawable(0).setColorFilter(getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);
        progressDrawable2.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        progressDrawable2.getDrawable(0).setColorFilter(getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);
        progressDrawable3.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        progressDrawable3.getDrawable(0).setColorFilter(getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);
        progressDrawable4.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        progressDrawable4.getDrawable(0).setColorFilter(getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);
        progressDrawable5.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        progressDrawable5.getDrawable(0).setColorFilter(getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);
    }
}