package com.datn.shopsale.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ColorAdapter;
import com.datn.shopsale.adapter.ContentAdapter;
import com.datn.shopsale.adapter.RamAdapter;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.models.User;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.PreferenceManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton imgBack;
    private Button btnDanhgia;
    private Button btnAddToCart;
    private ImageView imgProduct;
    private TextView tvNameProduct;
    private TextView tvPriceProduct;
    private RecyclerView recyColorsProduct, recyDungLuong;
    private ContentAdapter contentAdapter;
    private ViewPager2 viewPager2;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        init();
    }

    private void init() {
        imgProduct = (ImageView) findViewById(R.id.img_product);
        tvNameProduct = (TextView) findViewById(R.id.tv_nameProduct);
        tvPriceProduct = (TextView) findViewById(R.id.tv_priceProduct);
        imgBack = (ImageButton) findViewById(R.id.img_back);
        btnDanhgia = (Button) findViewById(R.id.btn_danhgia);
        btnAddToCart = (Button) findViewById(R.id.btn_add_to_cart);
        recyColorsProduct = findViewById(R.id.recy_colorsProduct);
        recyDungLuong = (RecyclerView) findViewById(R.id.recy_dungLuong);
        viewPager2 = findViewById(R.id.vpg_product);


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

        imgBack.setOnClickListener(this);
        btnDanhgia.setOnClickListener(this);
        btnAddToCart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back) {
            super.onBackPressed();
        } else if (view.getId() == R.id.btn_danhgia) {
            startActivity(new Intent(getApplicationContext(), ReviewActivity.class));
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