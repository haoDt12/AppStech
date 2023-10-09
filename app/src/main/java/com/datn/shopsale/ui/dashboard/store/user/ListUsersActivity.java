package com.datn.shopsale.ui.dashboard.store.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;

import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ProductAdapter;
import com.datn.shopsale.adapter.UserAdapter;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.models.User;

import java.util.ArrayList;

public class ListUsersActivity extends AppCompatActivity {
    private ImageButton imgBack;
    private RecyclerView rcvListUser;
    private ArrayList<User> dataList = new ArrayList<>();
    private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        imgBack = (ImageButton) findViewById(R.id.img_back);
        rcvListUser = (RecyclerView) findViewById(R.id.rcv_list_user);
        imgBack.setOnClickListener(view -> {
            super.onBackPressed();
        });

        dataList.add(new User("1", "image", "hao@gmail.com", "Hào Đinh", "1245","094xxxxxxx","user"));
        dataList.add(new User("2", "image", "hao@gmail.com", "Hào Đinh", "1245","094xxxxxxx","user"));
        dataList.add(new User("3", "image", "hao@gmail.com", "Hào Đinh", "1245","094xxxxxxx","user"));
        dataList.add(new User("4", "image", "hao@gmail.com", "Hào Đinh", "1245","094xxxxxxx","user"));
        dataList.add(new User("5", "image", "hao@gmail.com", "Hào Đinh", "1245","094xxxxxxx","user"));
        dataList.add(new User("6", "image", "hao@gmail.com", "Hào Đinh", "1245","094xxxxxxx","user"));

        userAdapter = new UserAdapter(dataList);
        rcvListUser.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        rcvListUser.setAdapter(userAdapter);
    }
}