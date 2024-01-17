package com.datn.shopsale.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.datn.shopsale.modelsv2.Conversation;
import com.datn.shopsale.modelsv2.DataListOrder;
import com.datn.shopsale.modelsv2.FeedBack;
import com.datn.shopsale.modelsv2.Img;
import com.datn.shopsale.modelsv2.User;
import com.datn.shopsale.request.CreateConversationRequest;
import com.datn.shopsale.responsev2.BaseResponse;
import com.datn.shopsale.responsev2.CreateConversationResponse;
import com.datn.shopsale.responsev2.FeedBackResponse;
import com.datn.shopsale.responsev2.GetDetailProductResponse;
import com.datn.shopsale.responsev2.GetInfoUserResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.dashboard.chat.ChatActivity;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvNameProduct, tvRam, tvColor, tvRom;
    private TextView tvPriceProduct;
    private ContentAdapter contentAdapter;

    private ViewPager2 viewPager2;
    private ReviewAdapter adapterRV;

    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private String id;
    private RecyclerView rcv_cmt;
    private RelativeLayout layoutActionBuy;
    private Button btnOutStock;

    private List<com.datn.shopsale.modelsv2.FeedBack> listFb;
    private TextView tvTBC;
    private TextView tvReview;
    private RatingBar ratingBar;
    private float TBC;
    private float rating;
    private Button btnBuyNow;
    private ImageButton btnChat;
    private String token;
    private String img_cover;
    private String quantity;
    private String price;
    private int quantitySelect = 1;
    private com.datn.shopsale.modelsv2.Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        init();
        getCmt();
        onclickByNow();
        btnChat.setOnClickListener(v -> doCreateConversation());
    }

    private void getCmt() {
        LoadingDialog.showProgressDialog(this, "Loading....");
        listFb = new ArrayList<>();
        Call<FeedBackResponse> call = apiService.getFeedBack(preferenceManager.getString("token"), id);
        call.enqueue(new Callback<FeedBackResponse>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<FeedBackResponse> call, @NonNull Response<FeedBackResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    for (FeedBack objFeedBack : response.body().getListFeedBack()) {
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
                        tvTBC.setText(String.format("%s/5", formattedText));
                        tvReview.setText(String.format("%d Review", listFb.size()));
                        ratingBar.setRating(rating);
                        adapterRV = new ReviewAdapter(listFb, getApplicationContext());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        rcv_cmt.setLayoutManager(linearLayoutManager);
                        rcv_cmt.setAdapter(adapterRV);

                    });
                } else {
                    runOnUiThread(() -> {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(DetailProductActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, response.body().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<FeedBackResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, t.getMessage());
                    LoadingDialog.dismissProgressDialog();
                });
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

                        Product videoContent = new Product();
                        videoContent.setVideo(video);
                        contentItems.add(videoContent);

                        if (!listImg.isEmpty()) {
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
                            product = response.body().getData().get(0).getProduct();
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

                            String formattedNumber = CurrencyUtils.formatCurrency(product.getPrice());
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
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(DetailProductActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, response.body().getMessage());
                            }
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
        tvTBC = findViewById(R.id.tv_TBC);
        layoutActionBuy = findViewById(R.id.lnl_action_buy);
        btnOutStock = findViewById(R.id.btn_out_stock);
        tvReview = findViewById(R.id.tv_review);
        ratingBar = findViewById(R.id.ratingBar);
        rcv_cmt = findViewById(R.id.recy_cmt);
        LinearLayout lnlSearch = findViewById(R.id.lnl_search);
        tvNameProduct = findViewById(R.id.tv_nameProduct);
        tvPriceProduct = findViewById(R.id.tv_priceProduct);
        Toolbar toolbarDetailPro = findViewById(R.id.toolbar_detail_pro);
        LinearLayout lnlAllFeedBack = findViewById(R.id.lnl_all_feed_back);
        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);
        viewPager2 = findViewById(R.id.vpg_product);
        tvColor = findViewById(R.id.tv_color);
        tvRam = findViewById(R.id.tv_dungLuong);
        tvRom = findViewById(R.id.tv_rom);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        btnChat = findViewById(R.id.btn_chat);
        setSupportActionBar(toolbarDetailPro);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarDetailPro.setNavigationOnClickListener(v -> onBackPressed());
        lnlSearch.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SearchActivity.class)));
        LayerDrawable starsDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
        starsDrawable.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        starsDrawable.getDrawable(0).setColorFilter(getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);

        token = preferenceManager.getString("token");
        id = getIntent().getStringExtra("id");
        img_cover = getIntent().getStringExtra("img_cover");
        price = getIntent().getStringExtra("price");
        quantity = getIntent().getStringExtra("quantity");
        displayProduct();

        lnlAllFeedBack.setOnClickListener(this);
        btnAddToCart.setOnClickListener(this);
    }

    private void getDataUser(Conversation conversation, String id) {
        LoadingDialog.showProgressDialog(this, "Loading...");
        String token = preferenceManager.getString("token");
        Call<GetInfoUserResponse> call = apiService.getAnyUserById(token, id);
        call.enqueue(new Callback<GetInfoUserResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetInfoUserResponse> call, @NonNull Response<GetInfoUserResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        User user = response.body().getUser();
                        doGoScreenChat(conversation, user);
                    } else {
                        runOnUiThread(() -> {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(DetailProductActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, response.body().getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetInfoUserResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, t.getMessage());
                });
            }
        });
    }

    private void doGoScreenChat(@NonNull Conversation conversation, User user) {
        Intent i = new Intent(DetailProductActivity.this, ChatActivity.class);
        i.putExtra("idConversation", conversation.getConversation_id());
        i.putExtra("dataUser", user);
        startActivity(i);
    }

    private void doCreateConversation() {
        LoadingDialog.showProgressDialog(this, "Loading...");
        String token = preferenceManager.getString("token");
        String userID = preferenceManager.getString("userId");
        CreateConversationRequest request = new CreateConversationRequest();
        request.setCreator_id(Constants.idUserAdmin);
        request.setReceive_id(userID);

        Call<CreateConversationResponse> call = apiService.createConversation(token, request);
        call.enqueue(new Callback<CreateConversationResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateConversationResponse> call, @NonNull Response<CreateConversationResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        Conversation conversation = response.body().getConversation();
                        getDataUser(conversation, conversation.getCreator_id());
                    } else {
                        runOnUiThread(() -> {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(DetailProductActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, response.body().getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreateConversationResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contentAdapter.releasePlayer();
    }

    @SuppressLint("DefaultLocale")
    private void AddToCart() {
        quantitySelect = 1;
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_add_to_cart, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);
        ImageView imgProduct;
        TextView tvPrice;
        TextView tv_kho;
        TextView tv_name;
        ImageButton imgDecrease;
        ImageButton imgIncrease;
        ImageButton btnHuy;
        Button btnThem;
        EditText ed_quantity;
        ed_quantity = view.findViewById(R.id.ed_quantity_cart);
        imgProduct = view.findViewById(R.id.img_product);
        tvPrice = view.findViewById(R.id.tv_price);
        tv_kho = view.findViewById(R.id.tv_kho);
        tv_name = view.findViewById(R.id.tv_name);
        imgDecrease = view.findViewById(R.id.img_decrease);
        imgIncrease = view.findViewById(R.id.img_increase);
        btnHuy = view.findViewById(R.id.btn_cancel);
        btnThem = view.findViewById(R.id.btn_buy);
        Glide.with(this).load(img_cover).into(imgProduct);
        tv_kho.setText(String.format("Kho: %s", quantity));
        tv_name.setText(product.getName());
        tvPrice.setText(CurrencyUtils.formatCurrency(price));

        ed_quantity.setText(String.valueOf(quantitySelect));
        if (quantitySelect == 1) {
            imgDecrease.setEnabled(false);
        }
        if (quantitySelect == Integer.parseInt(quantity)) {
            imgIncrease.setEnabled(false);
        }
        btnHuy.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
        imgIncrease.setOnClickListener(view1 -> {
            quantitySelect = quantitySelect + 1;
            ed_quantity.setText(String.valueOf(quantitySelect));
            if (quantitySelect > 1) {
                imgDecrease.setEnabled(true);
            }
        });
        imgDecrease.setOnClickListener(view1 -> {
            quantitySelect = quantitySelect - 1;
            ed_quantity.setText(String.valueOf(quantitySelect));
            if (quantitySelect == 1) {
                imgDecrease.setEnabled(false);
            }
        });
        btnThem.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();
            addToCart(ed_quantity.getText().toString());
        });
    }

    private void addToCart(String quantity) {
        LoadingDialog.showProgressDialog(this, "Loading...");
        Call<BaseResponse> call = apiService.addProductCart(preferenceManager.getString("token"),
                preferenceManager.getString("userId"), id, quantity);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, "Add to cart success");

                } else {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(DetailProductActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, response.body().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialogWithOk(DetailProductActivity.this, t.getMessage());
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
    }

    private void onclickByNow() {
        btnBuyNow.setOnClickListener(v -> {
            quantitySelect = 1;
            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_add_to_cart, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);
            ImageView imgProduct;
            TextView tvPrice;
            TextView tv_kho;
            TextView tv_name;
            TextView title;
            ImageButton imgDecrease;
            ImageButton imgIncrease;
            ImageButton btnHuy;
            Button btnThem;
            EditText ed_quantity;
            ed_quantity = view.findViewById(R.id.ed_quantity_cart);
            imgProduct = view.findViewById(R.id.img_product);
            tvPrice = view.findViewById(R.id.tv_price);
            tv_kho = view.findViewById(R.id.tv_kho);
            tv_name = view.findViewById(R.id.tv_name);
            title = view.findViewById(R.id.title);
            imgDecrease = view.findViewById(R.id.img_decrease);
            imgIncrease = view.findViewById(R.id.img_increase);
            btnHuy = view.findViewById(R.id.btn_cancel);
            btnThem = view.findViewById(R.id.btn_buy);
            Glide.with(this).load(img_cover).into(imgProduct);
            title.setText(getResources().getText(R.string.mua_ngay));
            tv_kho.setText(String.format("Kho: %s", quantity));
            tvPrice.setText(String.format("Giá: %s", CurrencyUtils.formatCurrency(price)));
            tv_name.setText(product.getName());
            btnThem.setText(getResources().getText(R.string.mua_ngay));

            ed_quantity.setText(String.valueOf(quantitySelect));
            if (quantitySelect == 1) {
                imgDecrease.setEnabled(false);
            }
            if (quantitySelect == Integer.parseInt(quantity)) {
                imgIncrease.setEnabled(false);
            }
            btnHuy.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
            imgIncrease.setOnClickListener(view1 -> {
                quantitySelect = quantitySelect + 1;
                ed_quantity.setText(String.valueOf(quantitySelect));
                if (quantitySelect > 1) {
                    imgDecrease.setEnabled(true);
                }
            });
            imgDecrease.setOnClickListener(view1 -> {
                quantitySelect = quantitySelect - 1;
                ed_quantity.setText(String.valueOf(quantitySelect));
                if (quantitySelect == 1) {
                    imgDecrease.setEnabled(false);
                }
            });
            btnThem.setOnClickListener(view1 -> {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(this, OrderActivity.class);
                Bundle bundle = new Bundle();
                List<com.datn.shopsale.modelsv2.Product> listOrders = new ArrayList<>();
                product.setQuantity(String.valueOf(quantitySelect));
                listOrders.add(product);
                DataListOrder dataListOrder = new DataListOrder();
                dataListOrder.setList(listOrders);
                bundle.putSerializable("listOrder", dataListOrder);
                intent.putExtras(bundle);
                startActivity(intent);
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}