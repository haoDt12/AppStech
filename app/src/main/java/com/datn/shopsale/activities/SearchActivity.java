package com.datn.shopsale.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private HistoryInfoAdapter adapter;
    private ActivitySearchBinding binding;
    private ArrayList<GetListProductResponse.Product> productList = new ArrayList<>();
    private ArrayList<Product> newList = new ArrayList<>();
    ProductAdapter productAdapter;
    SearchAdapter searchAdapter;
    private RecyclerView rcvFoyyou;
    private SearchView idSearch;
    private ApiService apiService;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        rcvFoyyou = (RecyclerView) findViewById(R.id.rcv_foyyou);
        idSearch = (SearchView) findViewById(R.id.id_search);

        setSupportActionBar(binding.toolbarSearch);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        search();
        showSearchHistory();

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

    public void search() {
        idSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                saveSearchHistory(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("zzzzzz", "onQueryTextChange: " + newText);
                List<GetListProductResponse.Product> suggestions = new ArrayList<>();
                for (int i = 0; i < productList.size(); i++) {
                    if (productList.get(i).getTitle().toLowerCase().contains(newText.toLowerCase())) {
                        suggestions.add(productList.get(i));
                        Log.d("zzz", "onQueryTextChange: " + productList.get(i));
                    }
                }
                productAdapter = new ProductAdapter((ArrayList<GetListProductResponse.Product>) suggestions, SearchActivity.this, R.layout.item_product);
                binding.rcvFoyyou.setAdapter(productAdapter);

                return false;
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