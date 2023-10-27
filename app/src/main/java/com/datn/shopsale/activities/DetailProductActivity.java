package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ColorAdapter;
import com.datn.shopsale.adapter.ContentAdapter;
import com.datn.shopsale.adapter.ProductAdapter;
import com.datn.shopsale.adapter.RamAdapter;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.CartRequest;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.models.User;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.response.ResApiNew;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.login.LoginActivity;
import com.datn.shopsale.ui.login.VerifyOTPActivity;
import com.datn.shopsale.utils.PreferenceManager;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

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
    private RecyclerView recyColorsProduct,recyDungLuong;
    private ContentAdapter contentAdapter;
    private ViewPager2 viewPager2;
    private ApiService apiService;
    private User user = new User();
    private Product product = new Product();
    private PreferenceManager preferenceManager;
    ArrayList<String> selectedColors = new ArrayList<>();
    ArrayList<String> selectedRams = new ArrayList<>();
    String id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        init();
    }
    private void init(){
        imgProduct = (ImageView) findViewById(R.id.img_product);
        tvNameProduct = (TextView) findViewById(R.id.tv_nameProduct);
        tvPriceProduct = (TextView) findViewById(R.id.tv_priceProduct);
        imgBack = (ImageButton) findViewById(R.id.img_back);
        btnDanhgia = (Button) findViewById(R.id.btn_danhgia);
        btnAddToCart = (Button) findViewById(R.id.btn_addToCart);
        recyColorsProduct = findViewById(R.id.recy_colorsProduct);
        recyDungLuong = (RecyclerView) findViewById(R.id.recy_dungLuong);
        viewPager2 = findViewById(R.id.vpg_product);


        final String namePr = getIntent().getStringExtra("title");
        final String pricePr = getIntent().getStringExtra("price");
        id = getIntent().getStringExtra("id");

        ArrayList<String> colorList = getIntent().getStringArrayListExtra("color");
        ArrayList<String> ramList = getIntent().getStringArrayListExtra("ram_rom");

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

        contentAdapter = new ContentAdapter(contentItems,this);
        viewPager2.setAdapter(contentAdapter);

        tvNameProduct.setText(namePr);
        tvPriceProduct.setText(String.valueOf(pricePr));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyColorsProduct.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyDungLuong.setLayoutManager(layoutManager1);
        ColorAdapter.OnColorItemClickListener colorItemClickListener = new ColorAdapter.OnColorItemClickListener() {
            @Override
            public void onColorItemClick(String color) {
                Product colorContent = new Product();
                selectedColors.add(color);
                colorContent.setColor(selectedColors);

            }
        };
        RamAdapter.OnRamItemClickListener ramItemClickListener = new RamAdapter.OnRamItemClickListener() {
            @Override
            public void onRamItemClick(String ram) {
                Product ramContent = new Product();
                selectedRams.add(ram);
                ramContent.setColor(selectedRams);
            }
        };
        ColorAdapter adapter = new ColorAdapter(colorList, colorItemClickListener);
        recyColorsProduct.setAdapter(adapter);
        RamAdapter adapter1 = new RamAdapter(ramList,ramItemClickListener);
        recyDungLuong.setAdapter(adapter1);

        imgBack.setOnClickListener(this);
        btnDanhgia.setOnClickListener(this);
        btnAddToCart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.img_back){
            super.onBackPressed();
        } else if (view.getId()==R.id.btn_danhgia) {
            startActivity(new Intent(getApplicationContext(),ReviewActivity.class));
        }else if(view.getId()==R.id.btn_addToCart){
            if (selectedColors.isEmpty() || id == null || selectedRams.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn màu và dung lượng", Toast.LENGTH_SHORT).show();
            } else {
                final String userId = user.get_id();
                final String productId = id;
                final String selectedColor = selectedColors.get(0); // Lấy màu đã chọn (chỉ lấy một màu)
                final String selectedRamRom = String.valueOf(product.getRam_rom());
                AddToCart(userId, productId, selectedColor, selectedRamRom);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        contentAdapter.releasePlayer();
    }
    private void AddToCart(String userId, String productId, String color, String ramRom) {
        // Tạo một đối tượng Product đại diện cho sản phẩm được chọn
        Product product = new Product();
        product.set_id(productId);
        selectedColors.add(color);
        product.setColor(selectedColors);
        selectedRams.add(ramRom);
        product.setColor(selectedRams);

        CartRequest.Product product1 = new CartRequest.Product();
        product1.color = String.valueOf(selectedColors);
        product1.quantity = 1;
        product1.ram_rom = String.valueOf(selectedRams);
        product1.productId = productId;

        CartRequest.Root request = new CartRequest.Root();
        request.product = product1;
        request.userId = userId;


//
//        CartRequest.Product cartRequest = new CartRequest.Product();
//        cartRequest.setProduct(product);
//        cartRequest.setUserId(userId);
//        cartRequest.setTotal(0);
//        CartRequest.
//
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        Date date = new Date(timestamp.getTime());
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
//        cartRequest.setDate_time(simpleDateFormat.format(date));

        // Gọi API để thêm sản phẩm vào giỏ hàng
        Call<ResApiNew> call = apiService.addToCart(preferenceManager.getString("token"), request);

        call.enqueue(new Callback<ResApiNew>() {
            @Override
            public void onResponse(Call<ResApiNew> call, Response<ResApiNew> response) {
                if (response.body().code ==1) {
                    Toast.makeText(DetailProductActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailProductActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                    Log.i("TAG9", "onResponse: "+response.message());

                }
            }

            @Override
            public void onFailure(Call<ResApiNew> call, Throwable t) {
                Toast.makeText(DetailProductActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}