package com.datn.shopsale.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ProductAdapter;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.login.LoginActivity;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListProductActivity extends AppCompatActivity {
    private ImageView imgCart;
    private ImageView imgMore, imgChangeLayout;

    private RecyclerView rcvProduct;
    private LinearLayout lnlSearch;

    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private ProductAdapter productAdapter;
    private boolean isLoadProduct = false;
    private boolean isGirdView = true;
    private int layoutItemProduct = R.layout.item_product;

    private ArrayList<GetListProductResponse.Product> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);
        init();

    }

    private void init() {
        Toolbar toolbarListPro = (Toolbar) findViewById(R.id.toolbar_list_pro);
        lnlSearch = (LinearLayout) findViewById(R.id.lnl_search);
        imgCart = (ImageView) findViewById(R.id.img_cart);
        imgMore = (ImageView) findViewById(R.id.img_more);
        imgChangeLayout = findViewById(R.id.img_change_layout);
        rcvProduct = findViewById(R.id.rcv_list_product);
        setSupportActionBar(toolbarListPro);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarListPro.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        lnlSearch.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
        });
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);

        setEventClick();
        String idCategorySelected = getIntent().getStringExtra("categoryId");
        getListProductByIdCate(idCategorySelected);
    }

    private void showToast(String message) {
        Toast.makeText(ListProductActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void setEventClick() {
        imgCart.setOnClickListener(v -> {
        });
        imgMore.setOnClickListener(v -> {
        });
        imgChangeLayout.setOnClickListener(v -> {
            isGirdView = !isGirdView;
            if (isGirdView) {
                imgChangeLayout.setImageResource(R.drawable.icon_grid_view_24);
                layoutItemProduct = R.layout.item_product;
            } else {
                imgChangeLayout.setImageResource(R.drawable.icon_view_list_24);
                layoutItemProduct = R.layout.item_product_vertical;
            }
            if (dataList.size() > 0) {
                displayProduct(dataList);
            } else {
                showToast("no data");
            }

        });
    }

    private Animation loadAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(10000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        return rotateAnimation;
    }

    private void setAnimationRecyclerview(int animResource) {
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, animResource);
        rcvProduct.setLayoutAnimation(animationController);
    }

    private void displayProduct(ArrayList<GetListProductResponse.Product> dataList) {
        if (isLoadProduct) {
            LoadingDialog.dismissProgressDialog();
        }
        setAnimationRecyclerview(R.anim.layout_animation_right_to_left);

        if (layoutItemProduct == R.layout.item_product) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(ListProductActivity.this, 2);
            gridLayoutManager.setSmoothScrollbarEnabled(true);
            rcvProduct.setLayoutManager(gridLayoutManager);
            productAdapter = new ProductAdapter(dataList, ListProductActivity.this, layoutItemProduct);
        } else if (layoutItemProduct == R.layout.item_product_vertical) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListProductActivity.this, LinearLayoutManager.VERTICAL, false);
            linearLayoutManager.setSmoothScrollbarEnabled(true);
            rcvProduct.setLayoutManager(linearLayoutManager);
            productAdapter = new ProductAdapter(dataList, ListProductActivity.this, layoutItemProduct);
        }

        rcvProduct.setAdapter(productAdapter);
        isLoadProduct = true;
    }

    private void getListProductByIdCate(String categoryId) {
        dataList = new ArrayList<>();
        Call<GetListProductResponse.Root> call = apiService.getListProductByIdCate(preferenceManager.getString("token"), categoryId);
        call.enqueue(new Callback<GetListProductResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetListProductResponse.Root> call, @NonNull Response<GetListProductResponse.Root> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        dataList = response.body().getProduct();
                        runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                displayProduct(dataList);
                            }
                        });
                    } else {
                        runOnUiThread(() -> {
                            showToast(response.body().getMessage());
                            if (isLoadProduct) {
                                LoadingDialog.dismissProgressDialog();
                            }
                        });
                    }
                    if (response.body().getMessage().equals("wrong token")) {
                        runOnUiThread(() -> {
                            LoadingDialog.dismissProgressDialog();
                            preferenceManager.clear();
                            startActivity(new Intent(ListProductActivity.this, LoginActivity.class));
                            finish();
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetListProductResponse.Root> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    showToast(t.getMessage());
                    if (isLoadProduct) {
                        LoadingDialog.dismissProgressDialog();
                    }
                });
            }
        });
    }
}