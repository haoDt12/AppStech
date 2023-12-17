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
import com.datn.shopsale.models.Category;
import com.datn.shopsale.response.GetBannerResponse;
import com.datn.shopsale.response.GetListCategoryResponse;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.dashboard.chat.ChatActivityFirebase;
import com.datn.shopsale.utils.AlertDialogUtil;
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

    private ArrayList<GetListProductResponse.Product> dataList = new ArrayList<>();
    private final ArrayList<GetBannerResponse.Banner> listImg = new ArrayList<>();
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

    private List<String> GetListBanner() {
        LoadingDialog.showProgressDialog(getActivity(), "Loading...");
        List<String> list = new ArrayList<>();
        listImg.clear();
        Call<GetBannerResponse.Root> call = apiService.getListBanner(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetBannerResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetBannerResponse.Root> call, @NonNull Response<GetBannerResponse.Root> response) {
                Log.d("TAG", "onResponse: " + response.code() + "zzzzzzzzzzz" + response);
                Log.d("TAG", "onResponse: " + response.body());
                Log.d("check", "onResponse: 1");
                assert response.body() != null;
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    if (response.body().code == 1) {
                        for (GetBannerResponse.Banner item : response.body().banner) {
                            listImg.add(new GetBannerResponse.Banner(item._id, item.img));
                            Log.d("TAG", "run: " + listImg.get(0).getImg());
                        }
                        for (int i = 0; i < listImg.size(); i++) {
                            list.add(listImg.get(i).getImg());
                        }
                        SliderAdapter sliderAdapter = new SliderAdapter(getActivity(), list);
                        binding.vpgSlideImage.setAdapter(sliderAdapter);
                        binding.circleIndicator.setViewPager(binding.vpgSlideImage);
                        sliderAdapter.registerDataSetObserver(binding.circleIndicator.getDataSetObserver());
                        Log.d("item", "onResponse: " + list.size());

                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().message);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GetBannerResponse.Root> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());

                });
            }

        });

        return list;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(binding.toolbarHome);
        binding.lnlSearch.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), SearchActivity.class)));
        preferenceManager = new PreferenceManager(getActivity());
        Log.d("token", "onCreateView: " + preferenceManager.getString("token"));
        apiService = RetrofitConnection.getApiService();
        Log.d("TagList", "onCreateView: " + GetListBanner().size());
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
        } else {
            Log.d("zzzzz", "onViewCreated: null binding" );
        }
        displayCategory();
        displayProduct();
        Log.d("zzzzzz", "onCreateView: " + preferenceManager.getString("token"));
        Log.d("fcm", "onViewCreated: " + preferenceManager.getString("fcm"));
    }

    private void displayProduct() {
        LoadingDialog.showProgressDialog(requireActivity(), "Loading...");
        dataList.clear();
        Call<GetListProductResponse.Root> call = apiService.getListProduct(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetListProductResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetListProductResponse.Root> call, @NonNull Response<GetListProductResponse.Root> response) {
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
                        AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GetListProductResponse.Root> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());
                });
            }
        });
    }

    private void displayCategory() {
        LoadingDialog.showProgressDialog(requireActivity(), "Loading...");
        ArrayList<Category> dataCategory = new ArrayList<>();
        Call<GetListCategoryResponse.Root> call = apiService.getListCategory(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetListCategoryResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetListCategoryResponse.Root> call, @NonNull Response<GetListCategoryResponse.Root> response) {
                Log.d("zzzz", "onResponse: " + response);
                Log.d("check", "onResponse: 3");
                assert response.body() != null;
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    if (response.body().getCode() == 1) {
                        for (GetListCategoryResponse.Category item : response.body().getCategory()) {
                            dataCategory.add(new Category(item.get_id(), item.getTitle(), item.getImg(), item.getDate()));
                        }

                        if (dataCategory.size() > 12) {
                            if (!dataCategory.get(11).getTitle().equals("Xem thêm")) {
                                String temp = "https://cdn-icons-png.flaticon.com/512/10348/10348994.png";
                                Category viewMore = new Category("-1", "Xem thêm", temp, "---");
                                Category viewLess = new Category("-1", "Ẩn bớt", temp, "---");
                                Log.d("zzzz", "run: " + dataCategory);
                                if (isDisableItem) {
                                    dataCategory.add(11, viewMore);
                                } else {
                                    dataCategory.add(dataCategory.size(), viewLess);
                                }

                            }
                        }
                        categoriesAdapter = new CategoriesAdapter(getActivity(), dataCategory, category -> {
                            if (category.getId().equals("-1")) {
                                isDisableItem = !isDisableItem;
                                displayCategory();
                            } else {
                                Intent intent = new Intent(getActivity(), ListProductActivity.class);
                                intent.putExtra("categoryId", category.getId());
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
            public void onFailure(@NonNull Call<GetListCategoryResponse.Root> call, @NonNull Throwable t) {
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
}