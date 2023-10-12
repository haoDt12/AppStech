package com.datn.shopsale.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.datn.shopsale.Interface.IActionCate;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.CategoriesAdapter;
import com.datn.shopsale.adapter.ProductAdapter;
import com.datn.shopsale.adapter.SliderAdapter;
import com.datn.shopsale.databinding.FragmentHomeBinding;
import com.datn.shopsale.models.Category;
import com.datn.shopsale.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements IActionCate {

    private FragmentHomeBinding binding;

    private ArrayList<Product> dataList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private CategoriesAdapter categoriesAdapter;


    public static boolean isDisableItem = true;

    private Timer timer;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        displayCategory();

        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.fist);
        imageList.add(R.drawable.seco);
        imageList.add(R.drawable.third);
        imageList.add(R.drawable.ford);
        imageList.add(R.drawable.five);


        SliderAdapter sliderAdapter = new SliderAdapter(getActivity(), imageList);
        binding.vpgSlideImage.setAdapter(sliderAdapter);
        binding.vpgSlideImage.setBackgroundResource(R.drawable.bg_search_view);

        binding.circleIndicator.setViewPager(binding.vpgSlideImage);
        sliderAdapter.registerDataSetObserver(binding.circleIndicator.getDataSetObserver());

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = binding.vpgSlideImage.getCurrentItem();
                        int totalItems = binding.vpgSlideImage.getAdapter().getCount();
                        int nextItem = (currentItem + 1) % totalItems;
                        binding.vpgSlideImage.setCurrentItem(nextItem);
                    }
                });
            }
        }, 2000, 2000);

        dataList.add(new Product("1", "Iphone 12", "23.000.000"));
        dataList.add(new Product("2", "Iphone 6", "3.000.000"));
        dataList.add(new Product("3", "Iphone 8", "10.000.000"));
        dataList.add(new Product("4", "Iphone 10", "14.000.000"));

        productAdapter = new ProductAdapter(dataList);
        binding.rcvListitem.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binding.rcvListitem.setAdapter(productAdapter);
        return root;
    }

    private void displayCategory() {
        ArrayList<Category> dataCategory = new ArrayList<>();
        dataCategory.add(new Category("1", "Smart Phone", "https://cdn-icons-png.flaticon.com/512/644/644458.png", "12/10/2023"));
        dataCategory.add(new Category("2", "Laptop", "https://cdn-icons-png.flaticon.com/512/428/428001.png", "11/10/2023"));
        dataCategory.add(new Category("3", "Smart Watch", "https://cdn-icons-png.flaticon.com/512/8488/8488889.png", "8/10/2023"));
        dataCategory.add(new Category("4", "Airpods", "https://cdn-icons-png.flaticon.com/512/5906/5906114.png", "9/10/2023"));
        dataCategory.add(new Category("5", "Camera", "https://cdn-icons-png.flaticon.com/512/2972/2972113.png", "12/10/2023"));
        dataCategory.add(new Category("6", "PC", "https://cdn-icons-png.flaticon.com/512/2291/2291988.png", "12/10/2023"));

        dataCategory.add(new Category("7", "Smart Phone", "https://cdn-icons-png.flaticon.com/512/644/644458.png", "12/10/2023"));
        dataCategory.add(new Category("8", "Laptop", "https://cdn-icons-png.flaticon.com/512/428/428001.png", "11/10/2023"));
        dataCategory.add(new Category("9", "Smart Watch", "https://cdn-icons-png.flaticon.com/512/8488/8488889.png", "8/10/2023"));
        dataCategory.add(new Category("10", "Airpods", "https://cdn-icons-png.flaticon.com/512/5906/5906114.png", "9/10/2023"));
        dataCategory.add(new Category("11", "Camera", "https://cdn-icons-png.flaticon.com/512/2972/2972113.png", "12/10/2023"));
        dataCategory.add(new Category("12", "PC", "https://cdn-icons-png.flaticon.com/512/2291/2291988.png", "12/10/2023"));
        dataCategory.add(new Category("13", "Smart Phone", "https://cdn-icons-png.flaticon.com/512/644/644458.png", "12/10/2023"));

        if (dataCategory.size() > 12) {
            if (!dataCategory.get(11).getTitle().equals("Xem thêm")) {
                String temp1 = "https://cdn-icons-png.flaticon.com/512/10348/10348994.png";
                String temp2 = "https://cdn-icons-png.flaticon.com/512/5602/5602211.png";
                Category viewMore = new Category("-1", "Xem thêm", temp1, "---");
                Category viewLess = new Category("-1", "Ẩn bớt", temp1, "---");
                if (isDisableItem) {
//                    setAnimationRecyclerview(R.anim.layout_animation_down_to_up);
                    dataCategory.add(11, viewMore);
                } else {
//                    setAnimationRecyclerview(R.anim.layout_animation_up_to_down);
                    dataCategory.add(dataCategory.size(), viewLess);
                }

            }
        }

        categoriesAdapter = new CategoriesAdapter(getActivity(), dataCategory, this);
        binding.rcvListCategories.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        binding.rcvListCategories.setAdapter(categoriesAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        timer.cancel();
    }

    private void setAnimationRecyclerview(int animResource) {
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getActivity(), animResource);
        binding.rcvListCategories.setLayoutAnimation(animationController);
    }

    @Override
    public void onClick(Category category) {
        if (category.getId().equals("-1")) {
            isDisableItem = !isDisableItem;
            displayCategory();
        } else {
            Toast.makeText(getActivity(), "" + category.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}