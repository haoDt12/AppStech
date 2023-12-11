package com.datn.shopsale.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ColorAdapter;
import com.datn.shopsale.adapter.ContentAdapter;
import com.datn.shopsale.adapter.RamAdapter;
import com.datn.shopsale.adapter.ReviewAdapter;
import com.datn.shopsale.adapter.RomAdapter;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.FeedBack;
import com.datn.shopsale.models.KeyValue;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.models.ResponeFeedBack;
import com.datn.shopsale.models.User;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout lnlAllFeedBack;
    private Button btnAddToCart;
    private ImageView imgProduct;
    private Toolbar toolbarDetailPro;
    private TextView tvNameProduct, tvRam, tvColor, tvRom;
    private TextView tvPriceProduct;
    private LinearLayout lnlSearch;
    private RecyclerView recyColorsProduct, recyDungLuong, recyRom;
    private ContentAdapter contentAdapter;

    private ViewPager2 viewPager2;
    private ReviewAdapter adapterRV;

    private User user = new User();
    private Product product = new Product();
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private String selectedColors = "";
    private String selectedRams = "";
    private String selectedRoms = "";
    private String id;
    private String imgCover;
    private String title;
    private int price;
    private RecyclerView recy_cmt;
    private List<FeedBack> listFb;
    private TextView tvTBC;
    private TextView tvReview;
    private RatingBar ratingBar;
    private float TBC;
    private float rating;
    private List<KeyValue> romList;
    private List<KeyValue> ramList;
    private List<KeyValue> colorList;
    private List<GetListProductResponse.Option> optionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        init();
        getCmt();
    }

    private void getCmt() {
        listFb = new ArrayList<>();
        Call<ResponeFeedBack> call = apiService.getFeedBackByProductId(preferenceManager.getString("token"), id);
        call.enqueue(new Callback<ResponeFeedBack>() {
            @Override
            public void onResponse(Call<ResponeFeedBack> call, Response<ResponeFeedBack> response) {
                if (response.body().getCode() == 1) {
                    for (FeedBack objFeedBack : response.body().getListFeedBack()) {
                        FeedBack feedBack = new FeedBack(
                                objFeedBack.getUserId(),
                                objFeedBack.getProductId(),
                                objFeedBack.getRating(),
                                objFeedBack.getComment(),
                                objFeedBack.getNameUser(),
                                objFeedBack.getAvtUser(),
                                objFeedBack.getDate()

                        );
                        listFb.add(feedBack);
                    }
                    runOnUiThread(() -> {
                        float tong = 0;
                        for (FeedBack objFeedBack : listFb) {
                            tong += objFeedBack.getRating();
                        }
                        if (listFb.size() == 0) {
                            TBC = 0;
                        } else {
                            TBC = tong / listFb.size();
                        }
                        rating = tong / listFb.size();
                        tvTBC.setText(TBC + "/5");
                        tvReview.setText(listFb.size() + " Review");
                        ratingBar.setRating(rating);
                        adapterRV = new ReviewAdapter(listFb, getApplicationContext());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        recy_cmt.setLayoutManager(linearLayoutManager);
                        recy_cmt.setAdapter(adapterRV);

                    });
                } else {
                    Toast.makeText(DetailProductActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponeFeedBack> call, Throwable t) {

            }
        });

    }

    private void init() {
        tvTBC = (TextView) findViewById(R.id.tv_TBC);
        tvReview = (TextView) findViewById(R.id.tv_review);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        recy_cmt = findViewById(R.id.recy_cmt);
        lnlSearch = (LinearLayout) findViewById(R.id.lnl_search);
        imgProduct = (ImageView) findViewById(R.id.img_product);
        tvNameProduct = (TextView) findViewById(R.id.tv_nameProduct);
        tvPriceProduct = (TextView) findViewById(R.id.tv_priceProduct);
        toolbarDetailPro = (Toolbar) findViewById(R.id.toolbar_detail_pro);
        lnlAllFeedBack = (LinearLayout) findViewById(R.id.lnl_all_feed_back);
        btnAddToCart = (Button) findViewById(R.id.btn_add_to_cart);
        recyColorsProduct = findViewById(R.id.recy_colorsProduct);
        recyDungLuong = (RecyclerView) findViewById(R.id.recy_dungLuong);
        recyRom = (RecyclerView) findViewById(R.id.recy_rom);
        viewPager2 = findViewById(R.id.vpg_product);
        tvColor = (TextView) findViewById(R.id.tv_color);
        tvRam = (TextView) findViewById(R.id.tv_dungLuong);
        tvRom = (TextView) findViewById(R.id.tv_rom);
        romList = new ArrayList<>();
        ramList = new ArrayList<>();
        colorList = new ArrayList<>();

        setSupportActionBar(toolbarDetailPro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarDetailPro.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        lnlSearch.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
        });
        LayerDrawable starsDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
        starsDrawable.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        starsDrawable.getDrawable(0).setColorFilter(getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);


        title = getIntent().getStringExtra("title");
        price = Integer.parseInt(getIntent().getStringExtra("price"));
        id = getIntent().getStringExtra("id");
        imgCover = getIntent().getStringExtra("imgCover");
        GetListProductResponse.Product getProduct = (GetListProductResponse.Product) getIntent().getSerializableExtra("product");
        if (getProduct != null) {
            optionList = getProduct.getOption();
            Log.d("zzzzz", "init: " + optionList.toString());
            if (getProduct.getOption() != null) {
                for (GetListProductResponse.Option item : getProduct.getOption()
                ) {
                    if (item.getType().equals("Color")) {
                        colorList.add(new KeyValue(item.getTitle(), item.getContent()));
                    }
                    if (item.getType().equals("Rom")) {
                        romList.add(new KeyValue(item.getTitle(), item.getContent()));
                    }
                    if (item.getType().equals("Ram")) {
                        ramList.add(new KeyValue(item.getTitle(), item.getContent()));
                    }
                }
            }
        }
        ArrayList<String> listImg = getIntent().getStringArrayListExtra("list_img");
        final String video = GetImgIPAddress.convertLocalhostToIpAddress(Objects.requireNonNull(getIntent().getStringExtra("video")));

        ArrayList<Product> contentItems = new ArrayList<>();

        if (video != null) {
            // Thêm video vào danh sách
            Product videoContent = new Product();
            videoContent.setVideo(video);
            contentItems.add(videoContent);
        }

        if (listImg != null && !listImg.isEmpty()) {
            // Thêm hình ảnh vào danh sách
            for (String imageUrl : listImg) {
                Product imageContent = new Product();
                ArrayList<String> imageUrls = new ArrayList<>();
                imageUrls.add(imageUrl);
                imageContent.setList_img(imageUrls);
                contentItems.add(imageContent);
            }
        }

        contentAdapter = new ContentAdapter(contentItems, this);
        viewPager2.setAdapter(contentAdapter);

        tvNameProduct.setText(title);
        String formattedNumber = CurrencyUtils.formatCurrency(String.valueOf(price)); // Format the integer directly
        tvPriceProduct.setText(formattedNumber);
        if (!colorList.isEmpty()) {
            selectedColors = colorList.get(0).getKey();
            ColorAdapter.OnColorItemClickListener colorItemClickListener = color -> selectedColors = color;
            ColorAdapter adapter = new ColorAdapter(colorList, colorItemClickListener);
            recyColorsProduct.setAdapter(adapter);
        } else {
            recyColorsProduct.setVisibility(View.GONE);
            tvColor.setVisibility(View.GONE);
        }
        if (!ramList.isEmpty()) {
            selectedRams = ramList.get(0).getKey();
            RamAdapter.OnRamItemClickListener ramItemClickListener = ram -> selectedRams = ram;
            RamAdapter adapter1 = new RamAdapter(ramList, ramItemClickListener);
            recyDungLuong.setAdapter(adapter1);
        } else {
            recyDungLuong.setVisibility(View.GONE);
            tvRam.setVisibility(View.GONE);
        }
        if (!romList.isEmpty()) {
            selectedRoms = romList.get(0).getKey();
            RomAdapter.OnRomItemClickListener romItemClickListener = rom -> selectedRoms = rom;
            RomAdapter adapter2 = new RomAdapter(romList, romItemClickListener);
            recyRom.setAdapter(adapter2);
        } else {
            tvRom.setVisibility(View.GONE);
            recyRom.setVisibility(View.GONE);
        }
        lnlAllFeedBack.setOnClickListener(this);
        btnAddToCart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.lnl_all_feed_back) {
            Intent i = new Intent(this, ReviewActivity.class);
            i.putExtra("id", id);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else if (view.getId() == R.id.btn_add_to_cart) {
            AddToCart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contentAdapter.releasePlayer();
    }

    private void AddToCart() {
        LoadingDialog.showProgressDialog(this, "Loading...");
        ArrayList<GetListProductResponse.Option> options = new ArrayList<>();
        for (GetListProductResponse.Option item : optionList
        ) {
            if (item.getTitle().equals(selectedColors) || item.getTitle().equals(selectedRams) || item.getTitle().equals(selectedRoms)) {
                options.add(item);
            }
        }
        String token = preferenceManager.getString("token");
        String idUser = preferenceManager.getString("userId");
        Cart objCart = new Cart();
        objCart.setProductId(id);
        objCart.setUserId(idUser);
        objCart.setTitle(title);
        objCart.setPrice(price);
        objCart.setQuantity(1);
        objCart.setImgCover(imgCover);
        objCart.setStatus(1);
        ArrayList<Cart.Option> optionArrayList = new ArrayList<>();
        for (GetListProductResponse.Option item : options
        ) {
            optionArrayList.add(new Cart.Option(item.getType(),item.getTitle(),item.getContent(),item.getFeesArise()));
        }
        objCart.setOption(optionArrayList);
        try {
            Call<ResApi> call = apiService.addToCart(token, objCart);
            call.enqueue(new Callback<ResApi>() {
                @Override
                public void onResponse(@NonNull Call<ResApi> call, @NonNull Response<ResApi> response) {
                    runOnUiThread(() -> {
                        assert response.body() != null;
                        if (response.body().code == 1) {
                            LoadingDialog.dismissProgressDialog();
                            Toast.makeText(DetailProductActivity.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            LoadingDialog.dismissProgressDialog();
                            Toast.makeText(DetailProductActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<ResApi> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        Log.e("error", t.getMessage());
                        Toast.makeText(DetailProductActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                LoadingDialog.dismissProgressDialog();
                Log.e("error", e.getMessage());
                Toast.makeText(DetailProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }


    }
}