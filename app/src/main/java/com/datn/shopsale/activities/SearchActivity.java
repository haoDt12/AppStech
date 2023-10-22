package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.datn.shopsale.R;
import com.datn.shopsale.adapter.HistoryInfoAdapter;
import com.datn.shopsale.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private HistoryInfoAdapter adapter;
    private ActivitySearchBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(view -> {
            onBackPressed();
        });

        List<String> list = new ArrayList<>();
        list.add("lót chuột cỡ lớn");
        list.add("cam wifi");
        list.add("Chuột logitech");
        list.add("laptop gaming");
        list.add("bàn phím cơ");
        list.add("lót chuột cỡ lớn");

        adapter = new HistoryInfoAdapter(list);
        binding.rcvHistory.setAdapter(adapter);
        binding.rcvHistory.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false));
    }
}