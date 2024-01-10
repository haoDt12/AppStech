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
import com.datn.shopsale.response.GetNotificationResponse;
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

        TextView cartBadgeTextView = cart_badge.findViewById(R.id.cart_badge);
        ApiService apiService = RetrofitConnection.getApiService();
        PreferenceManager preferenceManager = new PreferenceManager(this);
        runOnUiThread(() -> {
            Call<GetNotificationResponse.Root> call = apiService.getNotification(preferenceManager.getString("token"));
            call.enqueue(new Callback<GetNotificationResponse.Root>() {
                @Override
                public void onResponse(@NonNull Call<GetNotificationResponse.Root> call, @NonNull Response<GetNotificationResponse.Root> response) {
                    if (response.body() != null) {
                        if (response.body().code == 1) {
                            int count = response.body().notification.size();
                            NotificationCount.count = count;
                            if (count > 0) {
                                cartBadgeTextView.setVisibility(View.VISIBLE);
                            } else {
                                cartBadgeTextView.setVisibility(View.GONE);
                            }
                            cartBadgeTextView.setText(String.valueOf(NotificationCount.count));
                        } else {
                            if (response.body().message.equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(MainActivity.this, response.body().message);
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(MainActivity.this, response.body().message);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GetNotificationResponse.Root> call, @NonNull Throwable t) {
                    AlertDialogUtil.showAlertDialogWithOk(MainActivity.this, t.getMessage());
                }
            });

        });
    }
}
