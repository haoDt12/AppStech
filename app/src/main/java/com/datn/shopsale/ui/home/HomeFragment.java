package com.datn.shopsale.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.datn.shopsale.R;
import com.datn.shopsale.adpater.ProductAdapter;
import com.datn.shopsale.adpater.SliderAdapter;
import com.datn.shopsale.databinding.FragmentHomeBinding;
import com.datn.shopsale.models.Product;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ArrayList<Product> dataList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private Timer timer;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.img1);
        imageList.add(R.drawable.ic_launcher_background);
        imageList.add(R.drawable.ic_launcher_foreground);

        SliderAdapter sliderAdapter = new SliderAdapter(getActivity(), imageList);
        binding.vpSlideimage.setAdapter(sliderAdapter);

        binding.circleIndicator.setViewPager(binding.vpSlideimage);
        sliderAdapter.registerDataSetObserver(binding.circleIndicator.getDataSetObserver());

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = binding.vpSlideimage.getCurrentItem();
                        int totalItems = binding.vpSlideimage.getAdapter().getCount();
                        int nextItem = (currentItem + 1) % totalItems;
                        binding.vpSlideimage.setCurrentItem(nextItem);
                    }
                });
            }
        }, 2000, 2000);

        dataList.add(new Product("1", "Iphone 12", "23.000.000"));
        dataList.add(new Product("1", "Iphone 6", "3.000.000"));
        dataList.add(new Product("1", "Iphone 8", "10.000.000"));
        dataList.add(new Product("1", "Iphone 10", "14.000.000"));

        productAdapter = new ProductAdapter(dataList);
        binding.rcvListitem.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        binding.rcvListitem.setAdapter(productAdapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        timer.cancel();
    }
}