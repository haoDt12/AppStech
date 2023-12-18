package com.datn.shopsale.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.HistoryInfoAdapter;
import com.datn.shopsale.adapter.ProductAdapter;
import com.datn.shopsale.adapter.SearchAdapter;
import com.datn.shopsale.databinding.ActivitySearchBinding;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
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
    private static final String TAG = SearchActivity.class.getSimpleName();

    private HistoryInfoAdapter adapter;
    private ActivitySearchBinding binding;
    private ArrayList<GetListProductResponse.Product> productList = new ArrayList<>();
    private ArrayList<Product> newList = new ArrayList<>();
    ProductAdapter productAdapter;
    SearchAdapter searchAdapter;
    private RecyclerView rcvFoyyou;
    private RecyclerView rcvResult;
    private LinearLayout lnlResult;
    private EditText idSearch;
    private TextView tvSearch, tvResult;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private ArrayList<GetListProductResponse.Product> dataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        rcvFoyyou = (RecyclerView) findViewById(R.id.rcv_foyyou);
        idSearch = findViewById(R.id.id_search);
        tvSearch = findViewById(R.id.tv_search);
        rcvResult = findViewById(R.id.rcv_result_search);
        tvResult = findViewById(R.id.tv_result_search);
        lnlResult = findViewById(R.id.lnl_result_search);

        setSupportActionBar(binding.toolbarSearch);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        binding.toolbarSearch.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        preferenceManager = new PreferenceManager(this);
        apiService = RetrofitConnection.getApiService();

        List<String> list = new ArrayList<>();
        list.add("lót chuột cỡ lớn");
        list.add("cam wifi");
        list.add("Chuột logitech");
        list.add("laptop gaming");
        list.add("bàn phím cơ");
        list.add("lót chuột cỡ lớn");

        adapter = new HistoryInfoAdapter(list, idSearch);
        binding.rcvHistory.setAdapter(adapter);
        binding.rcvHistory.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false));
        rcvFoyyou.setAdapter(productAdapter);
        displayProduct();
//        search();
        showSearchHistory();


        tvSearch.setOnClickListener(v -> {
            String queryText = idSearch.getText().toString().trim();
            if (queryText.length() == 0) return;
            doSearch(queryText);
            saveSearchHistory(queryText);
        });

    }

    private void displayDataSearch(ArrayList<GetListProductResponse.Product> dataList) {
        if (dataList.size() == 0) {
            lnlResult.setVisibility(View.GONE);
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText(getResources().getText(R.string.not_found));
        } else {
            lnlResult.setVisibility(View.VISIBLE);
            tvResult.setVisibility(View.GONE);
            productAdapter = new ProductAdapter(productList, SearchActivity.this, R.layout.item_product);
            binding.rcvResultSearch.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
            binding.rcvResultSearch.setAdapter(productAdapter);
        }
    }

    private void doSearch(String query) {
        LoadingDialog.showProgressDialog(this, "Loading...");
        String token = preferenceManager.getString("token");
        Call<GetListProductResponse.Root> call = apiService.searchProduct(token, query);
        call.enqueue(new Callback<GetListProductResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetListProductResponse.Root> call, @NonNull Response<GetListProductResponse.Root> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        runOnUiThread(() -> {
                            dataList = response.body().getProduct();
                            displayDataSearch(dataList);
                        });
                    } else {
                        AlertDialogUtil.showSimpleAlertDialog(SearchActivity.this, response.body().getMessage());
                    }
                }
                LoadingDialog.dismissProgressDialog();
            }

            @Override
            public void onFailure(@NonNull Call<GetListProductResponse.Root> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showSimpleAlertDialog(SearchActivity.this, t.getMessage());
                });
                LoadingDialog.dismissProgressDialog();
            }
        });

    }

    private void displayProduct() {
        productList.clear();
        Call<GetListProductResponse.Root> call = apiService.getListProduct(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetListProductResponse.Root>() {
            @Override
            public void onResponse(Call<GetListProductResponse.Root> call, Response<GetListProductResponse.Root> response) {
                if (response.body().getCode() == 1) {
                    productList = response.body().getProduct();
                    runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            productAdapter = new ProductAdapter(productList, SearchActivity.this, R.layout.item_product);
                            binding.rcvFoyyou.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
                            binding.rcvFoyyou.setAdapter(productAdapter);
                            LoadingDialog.dismissProgressDialog();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(SearchActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                    });
                }
            }

            @Override
            public void onFailure(Call<GetListProductResponse.Root> call, Throwable t) {
                Toast.makeText(new SearchActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSearchHistory(String query) {
        SharedPreferences sharedPreferences = getSharedPreferences("search_history", Context.MODE_PRIVATE);
        Set<String> searchSet = sharedPreferences.getStringSet("history", new HashSet<>());
        searchSet.add(query);
        sharedPreferences.edit().putStringSet("history", searchSet).apply();
    }

    // Hiển thị lịch sử tìm kiếm từ SharedPreferences
    private void showSearchHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("search_history", Context.MODE_PRIVATE);
        Set<String> searchSet = sharedPreferences.getStringSet("history", new HashSet<>());
        // Hiển thị danh sách lịch sử tìm kiếm trong giao diện người dùng
        // Ví dụ: Hiển thị danh sách lịch sử tìm kiếm trong một RecyclerView hoặc ListView
        adapter.setData(new ArrayList<>(searchSet));
        adapter.notifyDataSetChanged();
    }

}


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().
//        return true;
//    }
//    private void Search(){
//
//    }