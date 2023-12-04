package com.datn.shopsale;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.datn.shopsale.databinding.ActivityMainBinding;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.ui.cart.CartPresenter;
import com.datn.shopsale.ui.cart.ICartView;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ICartView {

    private ActivityMainBinding binding;
    private View cart_badge;
    private CartPresenter cartPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_cart, R.id.navigation_notifications, R.id.navigation_dashboard).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // custom bottom nav
        BottomNavigationMenuView mBottomNavigationMenuView =
                (BottomNavigationMenuView) navView.getChildAt(0);
        View view = mBottomNavigationMenuView.getChildAt(2);
        BottomNavigationItemView itemView = (BottomNavigationItemView) view;
        cart_badge = LayoutInflater.from(this)
                .inflate(R.layout.cart_badge,
                        mBottomNavigationMenuView, false);
        itemView.addView(cart_badge);

        cartPresenter = new CartPresenter(this);
//        cartPresenter.getDataCart(this);

//        Intent intent = new Intent();
//        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
//        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//        startActivity(intent);
    }

    @Override
    public void getDataCartSuccess(List<Cart> list) {
        TextView cartBadgeTextView = cart_badge.findViewById(R.id.cart_badge);
        cartBadgeTextView.setText(String.valueOf(list.size()));
    }

    @Override
    public void getDataCartFail(String message) {

    }
}