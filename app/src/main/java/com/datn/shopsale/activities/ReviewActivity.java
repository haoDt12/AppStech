package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ReviewAdapter;
import com.datn.shopsale.models.ResponeFeedBack;
import com.datn.shopsale.modelsv2.FeedBack;
import com.datn.shopsale.responsev2.FeedBackResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {
    private Toolbar toolbarReview;
    private TextView TBC;
    private float sumTBC;
    private RatingBar ratingBar;
    private TextView tvReview;
    private LinearLayout lnlAllFeedBack;
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
    private RecyclerView recyReview;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private List<com.datn.shopsale.modelsv2.FeedBack> listFb;
    private ReviewAdapter adapter;
    private String id;
    private double cout1 = 0;
    private double cout2 = 0;
    private double cout3 = 0;
    private double cout4 = 0;
    private double cout5 = 0;
    private double progress_1 = 0;
    private double progress_2 = 0;
    private double progress_3 = 0;
    private double progress_4 = 0;
    private double progress_5 = 0;

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
        listFb = new ArrayList<>();
        Call<FeedBackResponse> call = apiService.getAllFeedBack(preferenceManager.getString("token"), id);
        call.enqueue(new Callback<FeedBackResponse>() {
            @Override
            public void onResponse(Call<FeedBackResponse> call, Response<FeedBackResponse> response) {
                if (response.body().getCode() == 1) {
                    for (FeedBack objFeedBack : response.body().getListFeedBack()) {
//
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
                        recyReview.setLayoutManager(linearLayoutManager);
                        recyReview.setAdapter(adapter);
                        tvReview.setText(listFb.size() + " Reviews");
                        for (int i = 0; i < listFb.size(); i++) {

                            if (listFb.get(i).getRating().equals("1.0")) {
                                cout1++;
                            }
                            if (listFb.get(i).getRating().equals("2.0")) {
                                cout2++;
                            }
                            if (listFb.get(i).getRating().equals("3.0")) {
                                cout3++;
                            }
                            if (listFb.get(i).getRating().equals("4.0")) {
                                cout4++;
                            }
                            if (listFb.get(i).getRating().equals("5.0")) {
                                cout5++;
                            }
                        }
                        String formattedText = String.format("%.1f", sumTBC);
                        TBC.setText(formattedText+"/5");

                        tv1.setText(Math.round(cout1) + "");
                        tv2.setText(Math.round(cout2) + "");
                        tv3.setText(Math.round(cout3) + "");
                        tv4.setText(Math.round(cout4) + "");
                        tv5.setText(Math.round(cout5) + "");
                        progress1.setProgress((int) Math.round((cout1 / listFb.size()) * 100));
                        progress2.setProgress((int) Math.round((cout2 / listFb.size()) * 100));
                        progress3.setProgress((int) Math.round((cout3 / listFb.size()) * 100));
                        progress4.setProgress((int) Math.round((cout4 / listFb.size()) * 100));
                        progress5.setProgress((int) Math.round((cout5 / listFb.size()) * 100));

                    });
                } else {
                    Toast.makeText(ReviewActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FeedBackResponse> call, Throwable t) {

            }
        });

    }

    private void initUi() {
        TBC = (TextView) findViewById(R.id.TBC);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tvReview = (TextView) findViewById(R.id.tv_review);
        lnlAllFeedBack = (LinearLayout) findViewById(R.id.lnl_all_feed_back);
        progress5 = (ProgressBar) findViewById(R.id.progress5);
        tv5 = (TextView) findViewById(R.id.tv5);
        progress4 = (ProgressBar) findViewById(R.id.progress4);
        tv4 = (TextView) findViewById(R.id.tv4);
        progress3 = (ProgressBar) findViewById(R.id.progress3);
        tv3 = (TextView) findViewById(R.id.tv3);
        progress2 = (ProgressBar) findViewById(R.id.progress2);
        tv2 = (TextView) findViewById(R.id.tv2);
        progress1 = (ProgressBar) findViewById(R.id.progress1);
        tv1 = (TextView) findViewById(R.id.tv1);
        recyReview = (RecyclerView) findViewById(R.id.recy_review);
        toolbarReview = (Toolbar) findViewById(R.id.toolbar_review);
        setSupportActionBar(toolbarReview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarReview.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
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