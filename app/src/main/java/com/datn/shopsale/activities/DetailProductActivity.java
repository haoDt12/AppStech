package com.datn.shopsale.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.FeedBack;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.models.ResponeFeedBack;
import com.datn.shopsale.models.ResponseCart;
import com.datn.shopsale.models.User;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout lnlAllFeedBack;
    private Button btnAddToCart;
    private ImageView imgProduct;
    private Toolbar toolbarDetailPro;
    private TextView tvNameProduct;
    private TextView tvPriceProduct;
    private RecyclerView recyColorsProduct, recyDungLuong;
    private ContentAdapter contentAdapter;
    private ViewPager2 viewPager2;
    private ReviewAdapter adapterRV;

    private User user = new User();
    private Product product = new Product();
    private ArrayList<String> ramList;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    String selectedColors = "";
    String selectedRams = "";
    String id;
    String imgCover;
    String title;
    int price;
    RecyclerView recy_cmt;
    List<FeedBack> listFb;
    private TextView tvTBC;
    private TextView tvReview;
    private RatingBar ratingBar;
    private float TBC;
    private float rating;

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
                        TBC = tong / listFb.size();
                        rating = tong / listFb.size();
                        tvTBC.setText(TBC + "/5");
                        tvReview.setText(listFb.size() + " Review");
                        ratingBar.setRating(rating);
                        adapterRV = new ReviewAdapter(listFb, getApplicationContext());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
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
        imgProduct = (ImageView) findViewById(R.id.img_product);
        tvNameProduct = (TextView) findViewById(R.id.tv_nameProduct);
        tvPriceProduct = (TextView) findViewById(R.id.tv_priceProduct);
        toolbarDetailPro = (Toolbar) findViewById(R.id.toolbar_detail_pro);
        lnlAllFeedBack = (LinearLayout) findViewById(R.id.lnl_all_feed_back);
        btnAddToCart = (Button) findViewById(R.id.btn_add_to_cart);
        recyColorsProduct = findViewById(R.id.recy_colorsProduct);
        recyDungLuong = (RecyclerView) findViewById(R.id.recy_dungLuong);
        viewPager2 = findViewById(R.id.vpg_product);

        setSupportActionBar(toolbarDetailPro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarDetailPro.setNavigationOnClickListener(v -> {
            onBackPressed();
        });


        title = getIntent().getStringExtra("title");
        price = Integer.parseInt(getIntent().getStringExtra("price"));
        id = getIntent().getStringExtra("id");
        imgCover = getIntent().getStringExtra("imgCover");

        ArrayList<String> colorList = getIntent().getStringArrayListExtra("color");
        ramList = getIntent().getStringArrayListExtra("ram_rom");

        ArrayList<String> listImg = getIntent().getStringArrayListExtra("list_img");
        final String video = getIntent().getStringExtra("video");


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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyColorsProduct.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyDungLuong.setLayoutManager(layoutManager1);
        ColorAdapter.OnColorItemClickListener colorItemClickListener = new ColorAdapter.OnColorItemClickListener() {
            @Override
            public void onColorItemClick(String color) {
                selectedColors = color;
            }
        };
        RamAdapter.OnRamItemClickListener ramItemClickListener = new RamAdapter.OnRamItemClickListener() {
            @Override
            public void onRamItemClick(String ram) {
                selectedRams = ram;
            }
        };
        ColorAdapter adapter = new ColorAdapter(colorList, colorItemClickListener);
        recyColorsProduct.setAdapter(adapter);
        RamAdapter adapter1 = new RamAdapter(ramList, ramItemClickListener);
        recyDungLuong.setAdapter(adapter1);

        lnlAllFeedBack.setOnClickListener(this);
        btnAddToCart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.lnl_all_feed_back) {
            startActivity(new Intent(getApplicationContext(), ReviewActivity.class));
        } else if (view.getId() == R.id.lnl_all_feed_back) {
            Intent i = new Intent(this, ReviewActivity.class);
            i.putExtra("id",id);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else if (view.getId() == R.id.btn_add_to_cart) {
                if (validate()) {
                    AddToCart();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contentAdapter.releasePlayer();
    }

    private void AddToCart() {
        String token = preferenceManager.getString("token");
        String idUser = preferenceManager.getString("userId");
        Cart objCart = new Cart();
        objCart.setProductId(id);
        objCart.setUserId(idUser);
        objCart.setTitle(title);
        objCart.setColor(selectedColors);
        objCart.setRam_rom(selectedRams);
        objCart.setPrice(price);
        objCart.setQuantity(1);
        objCart.setImgCover(imgCover);
        objCart.setStatus(1);
        try {
            Call<ResApi> call = apiService.addToCart(token, objCart);
            call.enqueue(new Callback<ResApi>() {
                @Override
                public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                    if (response.body().code == 1) {
                        Toast.makeText(DetailProductActivity.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DetailProductActivity.this, response.body().message, Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<ResApi> call, Throwable t) {
                    Log.e("error", t.getMessage());
                    Toast.makeText(DetailProductActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            Log.e("error", e.getMessage());
            Toast.makeText(DetailProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private boolean validate() {
        if (selectedColors.equals("")) {
            Toast.makeText(this, "Vui lòng chọn màu", Toast.LENGTH_SHORT).show();
            return false;
        } else if (ramList.size() == 0) {
            return true;
        } else if (selectedRams.equals("")) {
            Toast.makeText(this, "Vui lòng chọn Dung lượng", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}