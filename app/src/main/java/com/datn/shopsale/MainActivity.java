package com.datn.shopsale;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import com.datn.shopsale.utils.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.datn.shopsale.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_cart, R.id.navigation_notifications, R.id.navigation_dashboard).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // custom bottom nav
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
                            Log.d(TAG, "onResponse: " + response.body().message);
                        }
                    }
                    Log.d(TAG, "onResponse: error get notification");
                }

                @Override
                public void onFailure(@NonNull Call<GetNotificationResponse.Root> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });

        });


    }
}
