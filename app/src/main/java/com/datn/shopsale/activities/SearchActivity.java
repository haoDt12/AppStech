package com.datn.shopsale.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.HistoryInfoAdapter;
import com.datn.shopsale.adapter.ProductAdapter;
import com.datn.shopsale.databinding.ActivitySearchBinding;
import com.datn.shopsale.modelsv2.Product;
import com.datn.shopsale.request.SearchProductByNameRequest;
import com.datn.shopsale.responsev2.GetAllProductResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private HistoryInfoAdapter adapter;
    private ActivitySearchBinding binding;
    private ProductAdapter productAdapter;
    private LinearLayout lnlResult;
    private EditText idSearch;
    private TextView tvResult;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private List<Product> dataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RecyclerView rcvForYou = findViewById(R.id.rcv_foyyou);
        idSearch = findViewById(R.id.id_search);
        TextView tvSearch = findViewById(R.id.tv_search);
        tvResult = findViewById(R.id.tv_result_search);
        lnlResult = findViewById(R.id.lnl_result_search);
        setSupportActionBar(binding.toolbarSearch);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        binding.toolbarSearch.setNavigationOnClickListener(v -> onBackPressed());
        preferenceManager = new PreferenceManager(this);
        apiService = RetrofitConnection.getApiService();
        List<String> list = new ArrayList<>();
        adapter = new HistoryInfoAdapter(list, txt -> {
            idSearch.setText(txt);
            doSearch(txt);
            showSearchHistory();
        });
        binding.rcvHistory.setAdapter(adapter);
        binding.rcvHistory.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false));
        rcvForYou.setAdapter(productAdapter);
        displayProduct();
        showSearchHistory();
        tvSearch.setOnClickListener(v -> {
            String queryText = idSearch.getText().toString().trim();
            if (queryText.length() == 0) return;
            doSearch(queryText);
            saveSearchHistory(queryText);
            showSearchHistory();
        });

    }

    private void displayDataSearch(List<Product> dataList) {
        if (dataList.size() == 0) {
            lnlResult.setVisibility(View.GONE);
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText(getResources().getText(R.string.not_found));
        } else {
            lnlResult.setVisibility(View.VISIBLE);
            tvResult.setVisibility(View.GONE);
            productAdapter = new ProductAdapter(dataList, this, R.layout.item_product);
            binding.rcvResultSearch.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
            binding.rcvResultSearch.setAdapter(productAdapter);
        }
    }

    private void doSearch(String query) {
        LoadingDialog.showProgressDialog(this, "Loading...");
        String token = preferenceManager.getString("token");
        SearchProductByNameRequest request = new SearchProductByNameRequest();
        request.setTxtSearch(query);
        Call<GetAllProductResponse> call = apiService.searchProductByName(token, request);
        call.enqueue(new Callback<GetAllProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetAllProductResponse> call, @NonNull Response<GetAllProductResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        runOnUiThread(() -> {
                            dataList = response.body().getProduct();
                            displayDataSearch(dataList);
                        });
                    } else {
                        runOnUiThread(() -> {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(SearchActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(SearchActivity.this, response.body().getMessage());
                            }
                        });
                    }
                }
                LoadingDialog.dismissProgressDialog();
            }

            @Override
            public void onFailure(@NonNull Call<GetAllProductResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showSimpleAlertDialog(SearchActivity.this, t.getMessage());
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });

    }

    private void displayProduct() {
        Call<GetAllProductResponse> call = apiService.getAllProduct(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetAllProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetAllProductResponse> call, @NonNull Response<GetAllProductResponse> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            productAdapter = new ProductAdapter(response.body().getProduct(), SearchActivity.this, R.layout.item_product);
                            binding.rcvFoyyou.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
                            binding.rcvFoyyou.setAdapter(productAdapter);
                            LoadingDialog.dismissProgressDialog();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(SearchActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(SearchActivity.this, response.body().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetAllProductResponse> call, @NonNull Throwable t) {
                AlertDialogUtil.showAlertDialogWithOk(SearchActivity.this, t.getMessage());
            }
        });
    }

    @SuppressLint("MutatingSharedPrefs")
    private void saveSearchHistory(String query) {
        SharedPreferences sharedPreferences = getSharedPreferences("search_history", Context.MODE_PRIVATE);
        Set<String> searchSet = sharedPreferences.getStringSet("history", new HashSet<>());
        searchSet.add(query);
        sharedPreferences.edit().putStringSet("history", searchSet).apply();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showSearchHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("search_history", Context.MODE_PRIVATE);
        Set<String> searchSet = sharedPreferences.getStringSet("history", new HashSet<>());
        adapter.setData(new ArrayList<>(searchSet));
        adapter.notifyDataSetChanged();
    }

}