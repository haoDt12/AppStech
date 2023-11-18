package com.datn.shopsale.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.Interface.IActionCate;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.SearchActivity;
import com.datn.shopsale.adapter.CategoriesAdapter;
import com.datn.shopsale.adapter.ProductAdapter;
import com.datn.shopsale.adapter.SliderAdapter;
import com.datn.shopsale.databinding.FragmentHomeBinding;
import com.datn.shopsale.models.Category;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.response.GetBannerResponse;
import com.datn.shopsale.response.GetListCategoryResponse;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment{

    private boolean isLoadProduct = false;
    private boolean isLoadCategory = false;
    private boolean isLoadBanner = false;

    private FragmentHomeBinding binding;

    private ArrayList<Product> dataList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private SliderAdapter sliderAdapter;
    private ArrayList<GetBannerResponse.Banner> listImg = new ArrayList<>();
    private CategoriesAdapter categoriesAdapter;


    public static boolean isDisableItem = true;

    private Timer timer;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    List<String> imageList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.lnlSearch.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), SearchActivity.class));
        });
        preferenceManager = new PreferenceManager(getActivity());
        apiService = RetrofitConnection.getApiService();
//        List<Integer> imageList = new ArrayList<>();
        List<String> imageList = new ArrayList<>();
//
        Log.d("TagList", "onCreateView: "+GetListBanner().size());




        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    int currentItem = binding.vpgSlideImage.getCurrentItem();
                    int totalItems = binding.vpgSlideImage.getAdapter().getCount();
                    int nextItem = (currentItem + 1) % totalItems;
                    binding.vpgSlideImage.setCurrentItem(nextItem);
                });
            }
        }, 2000, 2000);
        LoadingDialog.showProgressDialog(getActivity(),"Đang Tải...");
        displayCategory();
        displayProduct();
        Log.d("zzzzzz", "onCreateView: " + preferenceManager.getString("token"));
        return root;
    }
    private List<String> GetListBanner(){
        List<String> list = new ArrayList<>();
        listImg.clear();
        Call<GetBannerResponse.Root> call = apiService.getListBanner(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetBannerResponse.Root>() {
            @Override
            public void onResponse(Call<GetBannerResponse.Root> call, Response<GetBannerResponse.Root> response) {
                Log.d("TAG", "onResponse: "+ response.code()+"zzzzzzzzzzz" + response);
                Log.d("TAG", "onResponse: "+response.body());
                if (response.code() == 200) {

                    for (GetBannerResponse.Banner item : response.body().banner) {
                        listImg.add(new GetBannerResponse.Banner(item._id, item.img));
                        Log.d("TAG", "run: "+listImg.get(0).getImg());
                    }
                    for(int i =0; i < listImg.size(); i++){
                        list.add(listImg.get(i).getImg());
                    }
                    SliderAdapter sliderAdapter = new SliderAdapter(getActivity(), list);
                    binding.vpgSlideImage.setAdapter(sliderAdapter);
                    binding.vpgSlideImage.setBackgroundResource(R.drawable.bg_search_view);

                    binding.circleIndicator.setViewPager(binding.vpgSlideImage);
                    sliderAdapter.registerDataSetObserver(binding.circleIndicator.getDataSetObserver());
                    Log.d("item", "onResponse: "+list.size());

                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                        if(isLoadBanner){
                            LoadingDialog.dismissProgressDialog();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GetBannerResponse.Root> call, Throwable t) {
                Toast.makeText(new SearchActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

         return list;
    }

    private void displayProduct() {
        dataList.clear();
        Call<GetListProductResponse.Root> call = apiService.getListProduct(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetListProductResponse.Root>() {
            @Override
            public void onResponse(Call<GetListProductResponse.Root> call, Response<GetListProductResponse.Root> response) {
                if(response.body().getCode() == 1){
                    for (GetListProductResponse.Product item: response.body().getProduct()) {
                        dataList.add(new Product(item.get_id(), item.getCategory(), item.getTitle(),item.getDescription(),
                                item.getColor(), item.getPrice(),item.getQuantity(),item.getSold(),item.getList_img(),
                                item.getDate(),item.getRam_rom(),item.getImg_cover(),item.getVideo()));
                    }
                    getActivity().runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            productAdapter = new ProductAdapter(dataList,getActivity());
                            binding.rcvListitem.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                            binding.rcvListitem.setAdapter(productAdapter);
                            LoadingDialog.dismissProgressDialog();
                            isLoadProduct = true;
                            if(isLoadCategory){
                                LoadingDialog.dismissProgressDialog();
                            }

                        }
                    });
                }else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                    });
                }
            }

            @Override
            public void onFailure(Call<GetListProductResponse.Root> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCategory() {
        ArrayList<Category> dataCategory = new ArrayList<>();
        Call<GetListCategoryResponse.Root> call = apiService.getListCategory(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetListCategoryResponse.Root>() {
            @Override
            public void onResponse(Call<GetListCategoryResponse.Root> call, Response<GetListCategoryResponse.Root> response) {
                if (response.body().getCode() == 1) {
                    for (GetListCategoryResponse.Category item : response.body().getCategory()) {
                        dataCategory.add(new Category(item.get_id(), item.getTitle(), item.getImg(), item.getDate()));
                    }
                    getActivity().runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            if (dataCategory.size() > 12) {
                                if (!dataCategory.get(11).getTitle().equals("Xem thêm")) {
                                    String temp1 = "https://cdn-icons-png.flaticon.com/512/10348/10348994.png";
                                    String temp2 = "https://cdn-icons-png.flaticon.com/512/5602/5602211.png";
                                    Category viewMore = new Category("-1", "Xem thêm", temp1, "---");
                                    Category viewLess = new Category("-1", "Ẩn bớt", temp1, "---");
                                    Log.d("zzzz", "run: " + dataCategory);
                                    if (isDisableItem) {
//                    setAnimationRecyclerview(R.anim.layout_animation_down_to_up);
                                        dataCategory.add(11, viewMore);
                                    } else {
//                    setAnimationRecyclerview(R.anim.layout_animation_up_to_down);
                                        dataCategory.add(dataCategory.size(), viewLess);
                                    }

                                }
                            }
                            categoriesAdapter = new CategoriesAdapter(getActivity(), dataCategory, new IActionCate() {
                                @Override
                                public void onClick(Category category) {
                                    if (category.getId().equals("-1")) {
                                        isDisableItem = !isDisableItem;
                                        displayCategory();
                                    } else {
                                        Toast.makeText(getActivity(), "" + category.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            binding.rcvListCategories.setLayoutManager(new GridLayoutManager(getActivity(), 4));
                            binding.rcvListCategories.setAdapter(categoriesAdapter);
                            LoadingDialog.dismissProgressDialog();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                    });
                }
            }

            @Override
            public void onFailure(Call<GetListCategoryResponse.Root> call, Throwable t) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
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
}