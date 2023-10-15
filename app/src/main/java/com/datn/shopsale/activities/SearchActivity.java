package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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

        List<String> list = new ArrayList<>();
        list.add("HEllo");
        list.add("HHHHHHHHHHHHH");
        list.add("FFFFFFFFFFFF");
        list.add("GGG");
        list.add("EEEEEE");

        adapter = new HistoryInfoAdapter(list);
        binding.rcvHistory.setAdapter(adapter);
        binding.rcvHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
}