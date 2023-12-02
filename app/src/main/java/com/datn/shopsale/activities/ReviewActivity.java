package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ReviewAdapter;
import com.datn.shopsale.models.FeedBack;
import com.datn.shopsale.models.ResponeFeedBack;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {
    private TextView TBC;
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
    private ImageView img_back;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private List<FeedBack> listFb;
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
        img_back.setOnClickListener(view -> {
            finish();
        });
        getCmt();
    }

    private void getCmt() {
        listFb = new ArrayList<>();
        Call<ResponeFeedBack> call = apiService.getAllFeedBackByProductId(preferenceManager.getString("token"), id);
        call.enqueue(new Callback<ResponeFeedBack>() {
            @Override
            public void onResponse(Call<ResponeFeedBack> call, Response<ResponeFeedBack> response) {
                if (response.body().getCode() == 1) {
                    for (FeedBack objFeedBack : response.body().getListFeedBack()) {
                        FeedBack feedBack = new FeedBack(
                                objFeedBack.getUserId(),
                                objFeedBack.getProductId(),
                                objFeedBack.getRating(),
                                objFeedBack.getComment(),
                                objFeedBack.getNameUser(),
                                objFeedBack.getAvtUser(),
                                objFeedBack.getDate()

                        );
                        listFb.add(feedBack);
                    }
                    runOnUiThread(() -> {
                        adapter = new ReviewAdapter(listFb, getApplicationContext());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        recyReview.setLayoutManager(linearLayoutManager);
                        recyReview.setAdapter(adapter);
                        tvReview.setText(listFb.size() + " Reviews");
                        for (int i = 0; i < listFb.size(); i++) {
                            if (listFb.get(i).getRating() == 1) {
                                cout1++;
                            }
                            if (listFb.get(i).getRating() == 2) {
                                cout2++;
                            }
                            if (listFb.get(i).getRating() == 3) {
                                cout3++;
                            }
                            if (listFb.get(i).getRating() == 4) {
                                cout4++;
                            }
                            if (listFb.get(i).getRating() == 5) {
                                cout5++;
                            }
                        }
//                        progress_1 = Math.round((cout1/listFb.size())*100);
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
            public void onFailure(Call<ResponeFeedBack> call, Throwable t) {

            }
        });

    }

    private void initUi() {
        img_back = findViewById(R.id.img_back);
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

    }
}