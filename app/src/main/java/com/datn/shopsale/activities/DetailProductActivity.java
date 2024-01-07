package com.datn.shopsale.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ContentAdapter;
import com.datn.shopsale.adapter.ReviewAdapter;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.models.ResponeFeedBack;
import com.datn.shopsale.modelsv2.FeedBack;
import com.datn.shopsale.modelsv2.Img;
import com.datn.shopsale.responsev2.BaseResponse;
import com.datn.shopsale.responsev2.FeedBackResponse;
import com.datn.shopsale.responsev2.GetDetailProductResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.dashboard.chat.ChatActivity;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = DetailProductActivity.class.getSimpleName();
    private static final int OPEN_ORDER = 1812;
    private LinearLayout lnlAllFeedBack;
    private Button btnAddToCart;
    private ImageView imgProduct;
    private Toolbar toolbarDetailPro;
    private TextView tvNameProduct, tvRam, tvColor, tvRom;
    private TextView tvPriceProduct;
    private LinearLayout lnlSearch;
    private ContentAdapter contentAdapter;

    private ViewPager2 viewPager2;
    private ReviewAdapter adapterRV;

    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private String id;
    private RecyclerView recy_cmt;
    private RelativeLayout layoutActionBuy;
    private Button btnOutStock;

    private List<com.datn.shopsale.modelsv2.FeedBack> listFb;
    private TextView tvTBC;
    private TextView tvReview;
    private ImageButton btnChat;
    private RatingBar ratingBar;
    private float TBC;
    private float rating;
    private Button btnBuyNow;
    private String token;
    private String img_cover;
    private String quantity;
    private String price;
    int quantityselect = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        init();
        getCmt();
        onclickByNow();
    }

    private void getCmt() {
        listFb = new ArrayList<>();
        Call<FeedBackResponse> call = apiService.getFeedBack(preferenceManager.getString("token"), id);
        call.enqueue(new Callback<FeedBackResponse>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<FeedBackResponse> call, @NonNull Response<FeedBackResponse> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    for (FeedBack objFeedBack : response.body().getListFeedBack()) {
//                        com.datn.shopsale.modelsv2.FeedBack feedBack = new FeedBack(
//                                objFeedBack.getUserId(),
//                                objFeedBack.getProductId(),
//                                objFeedBack.getRating(),
//                                objFeedBack.getComment(),
//                                objFeedBack.getNameUser(),
//                                objFeedBack.getAvtUser(),
//                                objFeedBack.getDate()
//
//                        );
                        FeedBack feedBack = new FeedBack(
                                objFeedBack.getCustomer_id(),
                                objFeedBack.getProduct_id(), objFeedBack.getRating(),
                                objFeedBack.getComment(),
                                objFeedBack.getCreate_time()
                        );

                        listFb.add(feedBack);
                    }
                    runOnUiThread(() -> {
                        float tong = 0;
                        for (FeedBack objFeedBack : listFb) {
                            tong += Float.parseFloat(objFeedBack.getRating());
                        }
                        if (listFb.size() == 0) {
                            TBC = 0;
                        } else {
                            TBC = tong / listFb.size();
                        }
                        rating = tong / listFb.size();
                        String formattedText = String.format("%.1f", TBC);
                        tvTBC.setText(formattedText+"/5");
                        tvReview.setText(String.format("%d Review", listFb.size()));
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
            public void onFailure(@NonNull Call<FeedBackResponse> call, @NonNull Throwable t) {
            }
        });

    }

    private void displayProduct() {
        LoadingDialog.showProgressDialog(this, "Loading...");
        Call<GetDetailProductResponse> call = apiService.getDetailProduct(token, id);
        call.enqueue(new Callback<GetDetailProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetDetailProductResponse> call, @NonNull Response<GetDetailProductResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        ArrayList<String> listImg = new ArrayList<>();
                        for (Img img : response.body().getData().get(0).getImg()) {
                            listImg.add(img.getImg());
                        }
                        String video = response.body().getData().get(0).getVideo().get(0).getVideo();
                        ArrayList<Product> contentItems = new ArrayList<>();

                        // Thêm video vào danh sách
                        Product videoContent = new Product();
                        videoContent.setVideo(video);
                        contentItems.add(videoContent);

                        if (!listImg.isEmpty()) {
                            // Thêm hình ảnh vào danh sách
                            for (String imageUrl : listImg) {
                                Product imageContent = new Product();
                                ArrayList<String> imageUrls = new ArrayList<>();
                                imageUrls.add(imageUrl);
                                imageContent.setList_img(imageUrls);
                                contentItems.add(imageContent);
                            }
                        }
                        contentAdapter = new ContentAdapter(contentItems, DetailProductActivity.this);
                        runOnUiThread(() -> {
                            com.datn.shopsale.modelsv2.Product product = response.body().getData().get(0).getProduct();
                            LoadingDialog.dismissProgressDialog();
                            viewPager2.setAdapter(contentAdapter);
                            tvNameProduct.setText(product.getName());
                            int quan = Integer.parseInt(product.getQuantity());
                            if (quan <= 0) {
                                layoutActionBuy.setVisibility(View.GONE);
                                btnOutStock.setVisibility(View.VISIBLE);
                            } else {
                                layoutActionBuy.setVisibility(View.VISIBLE);
                                btnOutStock.setVisibility(View.GONE);
                            }

                            String formattedNumber = CurrencyUtils.formatCurrency(product.getPrice()); // Format the integer directly
                            tvPriceProduct.setText(formattedNumber);
                            if (product.getColor() != null) {
                                tvColor.setText(String.format("Màu: %s", product.getColor()));
                                tvColor.setVisibility(View.VISIBLE);
                            } else {
                                tvColor.setVisibility(View.GONE);
                            }
                            if (product.getRam() != null) {
                                tvRam.setText(String.format("Ram: %s", product.getRam()));
                                tvRam.setVisibility(View.VISIBLE);

                            } else {
                                tvRam.setVisibility(View.GONE);
                            }
                            if (product.getRom() != null) {
                                tvRom.setText(String.format("Màu: %s", product.getRom()));
                                tvRom.setVisibility(View.VISIBLE);
                            } else {
                                tvRom.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        runOnUiThread(() -> {
                            LoadingDialog.dismissProgressDialog();
                            AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, response.body().getMessage());
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetDetailProductResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, t.getMessage());
                });
            }
        });
    }

    private void init() {
        tvTBC = (TextView) findViewById(R.id.tv_TBC);
        layoutActionBuy = findViewById(R.id.lnl_action_buy);
        btnOutStock = findViewById(R.id.btn_out_stock);
        tvReview = (TextView) findViewById(R.id.tv_review);
        btnChat = findViewById(R.id.btn_chat);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        recy_cmt = findViewById(R.id.recy_cmt);
        lnlSearch = (LinearLayout) findViewById(R.id.lnl_search);
        imgProduct = (ImageView) findViewById(R.id.img_product);
        tvNameProduct = (TextView) findViewById(R.id.tv_nameProduct);
        tvPriceProduct = (TextView) findViewById(R.id.tv_priceProduct);
        toolbarDetailPro = (Toolbar) findViewById(R.id.toolbar_detail_pro);
        lnlAllFeedBack = (LinearLayout) findViewById(R.id.lnl_all_feed_back);
        btnAddToCart = (Button) findViewById(R.id.btn_add_to_cart);
        viewPager2 = findViewById(R.id.vpg_product);
        tvColor = (TextView) findViewById(R.id.tv_color);
        tvRam = (TextView) findViewById(R.id.tv_dungLuong);
        tvRom = (TextView) findViewById(R.id.tv_rom);
        btnBuyNow = (Button) findViewById(R.id.btn_buy_now);
        setSupportActionBar(toolbarDetailPro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarDetailPro.setNavigationOnClickListener(v -> onBackPressed());
        lnlSearch.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
        });
        LayerDrawable starsDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
        starsDrawable.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        starsDrawable.getDrawable(0).setColorFilter(getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);

        token = preferenceManager.getString("token");
        id = getIntent().getStringExtra("id");
        img_cover = getIntent().getStringExtra("img_cover");
        price = getIntent().getStringExtra("price");
        quantity = getIntent().getStringExtra("quantity");
//        getDataProduct(token, id);
        displayProduct();

        lnlAllFeedBack.setOnClickListener(this);
        btnAddToCart.setOnClickListener(this);
        btnChat.setOnClickListener(this);
    }

    private void doCreateConversation() {
        ArrayList<String> listUserInConversation = new ArrayList<>();
        listUserInConversation.add(Constants.idUserAdmin);
        String idUser = preferenceManager.getString("userId");
        String token = preferenceManager.getString("token");
        Call<ResApi> call = apiService.createConversation(token, "ChatBox", idUser, listUserInConversation);
        call.enqueue(new Callback<ResApi>() {
            @Override
            public void onResponse(@NonNull Call<ResApi> call, @NonNull Response<ResApi> response) {
                if (response.body() != null) {
                    if (response.body().code == 1) {
                        runOnUiThread(() -> {
                            Intent i = new Intent(DetailProductActivity.this, ChatActivity.class);
                            i.putExtra("idConversation", response.body().id);
                            i.putExtra("idUser", listUserInConversation.get(0));
                            startActivity(i);
                        });
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, response.body().message);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResApi> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, t.getMessage());
                });
            }
        });
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
        } else if (view.getId() == R.id.btn_chat) {
            doCreateConversation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contentAdapter.releasePlayer();
//        Toast.makeText(this, "on Destroy", Toast.LENGTH_SHORT).show();
    }

    private void AddToCart() {
        quantityselect = 1;
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_add_to_cart, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);
        ImageView imgProduct;
        TextView tvPrice;
        TextView tv_kho;
        ImageButton imgDecrease;
        ImageButton imgIncrease;
        Button btnHuy;
        Button btnThem;
        EditText ed_quantity;
        ed_quantity = view.findViewById(R.id.ed_quantity_cart);
        imgProduct = view.findViewById(R.id.img_product);
        tvPrice = view.findViewById(R.id.tv_price);
        tv_kho = view.findViewById(R.id.tv_kho);
        imgDecrease = view.findViewById(R.id.img_decrease);
        imgIncrease = view.findViewById(R.id.img_increase);
        btnHuy = view.findViewById(R.id.btn_huy);
        btnThem = view.findViewById(R.id.btn_them);
        Glide.with(this).load(img_cover).into(imgProduct);
        tv_kho.setText("Kho: " + quantity);
        tvPrice.setText(CurrencyUtils.formatCurrency(price));

        ed_quantity.setText(quantityselect + "");
        if (quantityselect == 1) {
            imgDecrease.setEnabled(false);
        }
        if (quantityselect == Integer.parseInt(quantity)) {
            imgIncrease.setEnabled(false);
        }
        btnHuy.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();
        });
        imgIncrease.setOnClickListener(view1 -> {
            quantityselect = quantityselect + 1;
            ed_quantity.setText(quantityselect + "");
            if (quantityselect > 1) {
                imgDecrease.setEnabled(true);
            }
        });
        imgDecrease.setOnClickListener(view1 -> {
            quantityselect = quantityselect - 1;
            ed_quantity.setText(quantityselect + "");
            if (quantityselect == 1) {
                imgDecrease.setEnabled(false);
            }
        });
        btnThem.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();
            addtoCart(ed_quantity.getText().toString());
        });
    }

    private void addtoCart(String quantity) {
        try {
            Call<BaseResponse> call = apiService.addProductCart(preferenceManager.getString("token"),
                    preferenceManager.getString("userId"), id, quantity);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.body().getCode() == 1) {
                        AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, "Add cart");

                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    runOnUiThread(() -> AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, t.getMessage()));
                }
            });
        } catch (Exception e) {
            Log.e("Error", "onFailure: " + e);
            Toast.makeText(DetailProductActivity.this, "error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void onclickByNow() {
        btnBuyNow.setOnClickListener(v -> {
            AlertDialogUtil.showAlertDialogWithOk(this, "By now");
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(this, "onActivityResult", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }
}