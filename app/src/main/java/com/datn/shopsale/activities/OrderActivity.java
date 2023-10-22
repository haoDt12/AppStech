package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.datn.shopsale.adapter.OrderAdapter;
import com.datn.shopsale.R;
import com.datn.shopsale.models.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        RecyclerView recyclerView = findViewById(R.id.rcv_order);
        List<Product> items = generateItems(); // Hãy thay thế bằng danh sách của bạn
        OrderAdapter adapter = new OrderAdapter(items);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }
    private List<Product> generateItems() {
        List<Product> items = new ArrayList<>();

        items.add(new Product("1","Iphone 14 ProMax","1000"));
        items.add(new Product("2","IPhone 15","10000"));
        items.add(new Product("3","SamSung","10000"));
        // Thêm các phần tử khác theo cách tương tự

        return items;
    }
}