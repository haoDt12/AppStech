package com.datn.shopsale.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.ChatScreenAdminActivity;
import com.datn.shopsale.activities.ListProductActivity;
import com.datn.shopsale.activities.SearchActivity;
import com.datn.shopsale.adapter.CategoriesAdapter;
import com.datn.shopsale.adapter.ProductAdapter;
import com.datn.shopsale.adapter.SliderAdapter;
import com.datn.shopsale.databinding.FragmentHomeBinding;
import com.datn.shopsale.modelsv2.Banner;
import com.datn.shopsale.modelsv2.Category;
import com.datn.shopsale.modelsv2.Product;
import com.datn.shopsale.responsev2.GetBannerResponse;
import com.datn.shopsale.responsev2.GetAllProductResponse;
import com.datn.shopsale.responsev2.GetCategoryResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.dashboard.chat.ChatActivityFirebase;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    private List<Product> dataList = new ArrayList<>();
    private List<Banner> dataListBanner = new ArrayList<>();
    private ProductAdapter productAdapter;
    private CategoriesAdapter categoriesAdapter;


    public static boolean isDisableItem = true;

    private Timer timer;

    private ApiService apiService;
    private PreferenceManager preferenceManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void GetListBanner() {

        LoadingDialog.showProgressDialog(getActivity(), "Loading...");
        List<String> list = new ArrayList<>();
        dataListBanner.clear();
        Call<GetBannerResponse> call = apiService.getListBanner(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetBannerResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetBannerResponse> call, @NonNull Response<GetBannerResponse> response) {
                assert response.body() != null;
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    dataListBanner = response.body().getBanner();
                    if (response.body().getCode() == 1) {
                        for (int i = 0; i < dataListBanner.size(); i++) {
                            list.add(dataListBanner.get(i).getImg());
                        }
                        SliderAdapter sliderAdapter = new SliderAdapter(getActivity(), list);
                        binding.vpgSlideImage.setAdapter(sliderAdapter);
                        binding.circleIndicator.setViewPager(binding.vpgSlideImage);
                        sliderAdapter.registerDataSetObserver(binding.circleIndicator.getDataSetObserver());

                    } else {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(requireActivity(), response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GetBannerResponse> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());

                });
            }

        });

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(binding.toolbarHome);
        binding.lnlSearch.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), SearchActivity.class)));
        preferenceManager = new PreferenceManager(getActivity());
        apiService = RetrofitConnection.getApiService();
        GetListBanner();
        timer = new Timer();
        if (binding != null && binding.vpgSlideImage.getAdapter() != null) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    requireActivity().runOnUiThread(() -> {
                        try {
                            int currentItem = binding.vpgSlideImage.getCurrentItem();
                            int totalItems = Objects.requireNonNull(binding.vpgSlideImage.getAdapter()).getCount();
                            int nextItem = (currentItem + 1) % totalItems;
                            binding.vpgSlideImage.setCurrentItem(nextItem);
                        } catch (Exception exception) {
                            Log.d("TAGzz: ", Objects.requireNonNull(exception.getMessage()));
                        }
                    });
                }
            }, 2000, 2000);
        }

        displayCategory();
        displayProduct();
    }

    private void displayProduct() {
        LoadingDialog.showProgressDialog(requireActivity(), "Loading...");
        dataList.clear();
        Call<GetAllProductResponse> call = apiService.getAllProduct(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetAllProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetAllProductResponse> call, @NonNull Response<GetAllProductResponse> response) {
                assert null != response.body();
                Log.d("check", "onResponse: 2");
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();

                    if (response.body().getCode() == 1) {
                        dataList = response.body().getProduct();

                        productAdapter = new ProductAdapter(dataList, getActivity(), R.layout.item_product);
                        binding.rcvListItemPro.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        binding.rcvListItemPro.setAdapter(productAdapter);

                    } else {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(requireActivity(), response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GetAllProductResponse> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());
                });
            }
        });
    }

    private void displayCategory() {
        LoadingDialog.showProgressDialog(requireActivity(), "Loading...");
        Call<GetCategoryResponse> call = apiService.getCategory(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetCategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetCategoryResponse> call, @NonNull Response<GetCategoryResponse> response) {
                assert response.body() != null;
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    if (response.body().getCode() == 1) {
                        List<Category> dataCategory = response.body().getCategory();

                        if (dataCategory.size() > 12) {
                            if (!dataCategory.get(11).getName().equals(getResources().getString(R.string.xem_them_1))) {
                                String temp = "https://cdn-icons-png.flaticon.com/512/10348/10348994.png";
                                Category viewMore = new Category("-1", getResources().getString(R.string.xem_them_1), "---", temp);
                                Category viewLess = new Category("-1", getResources().getString(R.string.an_bot), "---", temp);
                                if (isDisableItem) {
                                    dataCategory.add(11, viewMore);
                                } else {
                                    dataCategory.add(dataCategory.size(), viewLess);
                                }

                            }
                        }
                        categoriesAdapter = new CategoriesAdapter(getActivity(), dataCategory, category -> {
                            if (category.get_id().equals("-1")) {
                                isDisableItem = !isDisableItem;
                                displayCategory();
                            } else {
                                Intent intent = new Intent(getActivity(), ListProductActivity.class);
                                intent.putExtra("categoryId", category.get_id());
                                startActivity(intent);
                            }
                        });
                        binding.rcvListCategories.setLayoutManager(new GridLayoutManager(getActivity(), 4));
                        binding.rcvListCategories.setAdapter(categoriesAdapter);
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GetCategoryResponse> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());
                });
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.chat) {
            if (preferenceManager.getString("userId").equals(Constants.idUserAdmin)) {
                startActivity(new Intent(getActivity(), ChatScreenAdminActivity.class));
            } else {
                startActivity(new Intent(getActivity(), ChatActivityFirebase.class));

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayProduct();
    }
}