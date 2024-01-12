package com.datn.shopsale;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.databinding.ActivityMainBinding;
import com.datn.shopsale.responsev2.GetNotificationResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.notifications.NotificationCount;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView cartBadgeTextView;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.datn.shopsale.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);


        @SuppressLint("RestrictedApi") BottomNavigationMenuView mBottomNavigationMenuView =
                (BottomNavigationMenuView) navView.getChildAt(0);
        View view = mBottomNavigationMenuView.getChildAt(2);
        @SuppressLint("RestrictedApi") BottomNavigationItemView itemView = (BottomNavigationItemView) view;
        View cart_badge = LayoutInflater.from(this)
                .inflate(R.layout.cart_badge,
                        mBottomNavigationMenuView, false);
        itemView.addView(cart_badge);

        cartBadgeTextView = cart_badge.findViewById(R.id.cart_badge);
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        getCountNotification();
    }

    private void getCountNotification() {
        Call<GetNotificationResponse> call = apiService.getNotificationByUser(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetNotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetNotificationResponse> call, @NonNull Response<GetNotificationResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        int count = response.body().getData().size();
                        NotificationCount.count = count;
                        if (count > 0) {
                            cartBadgeTextView.setVisibility(View.VISIBLE);
                        } else {
                            cartBadgeTextView.setVisibility(View.GONE);
                        }
                        cartBadgeTextView.setText(String.valueOf(NotificationCount.count));
                    } else {
                        runOnUiThread(() -> {
                        });
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(MainActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(MainActivity.this, response.body().getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetNotificationResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialogWithOk(MainActivity.this, t.getMessage());
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCountNotification();
    }
}
