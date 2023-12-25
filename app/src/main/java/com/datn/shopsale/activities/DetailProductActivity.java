package com.datn.shopsale.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.datn.shopsale.adapter.ColorAdapter;
import com.datn.shopsale.adapter.ContentAdapter;
import com.datn.shopsale.adapter.RamAdapter;
import com.datn.shopsale.adapter.ReviewAdapter;
import com.datn.shopsale.adapter.RomAdapter;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.FeedBack;
import com.datn.shopsale.models.ListOder;
import com.datn.shopsale.models.Option;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.models.ResponeFeedBack;
import com.datn.shopsale.models.User;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.response.GetProductByIDResponse;
import com.datn.shopsale.response.GetProductResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.dashboard.chat.ChatActivity;
import com.datn.shopsale.ui.dashboard.chat.ConversationActivity;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.Constants;
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
    private static final String TAG = DetailProductActivity.class.getSimpleName();
    private static final int OPEN_ORDER = 1812;
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
    private String quantity;
    private String imgCover;
    private String title;
    private int price;
    private RecyclerView recy_cmt;
    private RelativeLayout layoutActionBuy;
    private Button btnOutStock;

    private List<FeedBack> listFb;
    private TextView tvTBC;
    private TextView tvReview;
    private ImageButton btnChat;
    private RatingBar ratingBar;
    private float TBC;
    private float rating;
    private List<Option> romList;
    private List<Option> ramList;
    private List<Option> colorList;
    private List<GetListProductResponse.Option> optionList;
    private Option colorOption;
    private Option ramOption;
    private Option romOption;
    private int feesAriseColor = 0;
    private int feesAriseRam = 0;
    private int feesAriseRom = 0;
    private boolean isOutOfStock = true;
    private Button btnBuyNow;
    private GetListProductResponse.Product getProduct;
    private GetProductByIDResponse.Product getProduct1;

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
        Call<ResponeFeedBack> call = apiService.getFeedBackByProductId(preferenceManager.getString("token"), id);
        call.enqueue(new Callback<ResponeFeedBack>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<ResponeFeedBack> call, @NonNull Response<ResponeFeedBack> response) {
                assert response.body() != null;
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
                        tvTBC.setText(String.format("%s/5", TBC));
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
            public void onFailure(@NonNull Call<ResponeFeedBack> call, @NonNull Throwable t) {
            }
        });

    }

    private void getDataProduct(String token, String proId) {
        Call<GetProductByIDResponse.Root> call = apiService.getProductByIdV2(token, proId);
        call.enqueue(new Callback<GetProductByIDResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetProductByIDResponse.Root> call, @NonNull Response<GetProductByIDResponse.Root> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    GetProductByIDResponse.Product product1 = response.body().getProduct();
                    title = getIntent().getStringExtra("title");
                    price = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("price")));
                    id = getIntent().getStringExtra("id");
                    quantity = getIntent().getStringExtra("quantity");
                    imgCover = getIntent().getStringExtra("imgCover");
                    getProduct = (GetListProductResponse.Product) getIntent().getSerializableExtra("product");

                    title = product1.getTitle();
                    price = Integer.parseInt(product1.getPrice());
                    quantity = product1.getQuantity();
                    imgCover = product1.getImg_cover();
                    getProduct1 = product1;
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<GetProductByIDResponse.Root> call, @NonNull Throwable t) {

            }
        });
    }

    private void displayProduct() {
        if (getProduct != null) {
            optionList = getProduct.getOption();
//            Log.d("zzzzzKKKKKKKKKKKKKKKKK", "init: " + optionList.toString());
            if (getProduct.getOption() != null) {
                for (GetListProductResponse.Option item : getProduct.getOption()) {
                    Log.d("TAG", "init: " + item.toString());
                    if (item.getType().equals("Color")) {
                        colorList.add(new Option(item.getType(), item.getTitle(), item.getContent(), item.getQuantity(), item.getFeesArise()));
                    }
                    if (item.getType().equals("Rom")) {
                        romList.add(new Option(item.getType(), item.getTitle(), item.getContent(), item.getQuantity(), item.getFeesArise()));
                    }
                    if (item.getType().equals("Ram")) {
                        ramList.add(new Option(item.getType(), item.getTitle(), item.getContent(), item.getQuantity(), item.getFeesArise()));
                    }
                }
            }
        }
        ArrayList<String> listImg = getIntent().getStringArrayListExtra("list_img");
        final String video = GetImgIPAddress.convertLocalhostToIpAddress(Objects.requireNonNull(getIntent().getStringExtra("video")));

        ArrayList<Product> contentItems = new ArrayList<>();

        // Thêm video vào danh sách
        Product videoContent = new Product();
        videoContent.setVideo(video);
        contentItems.add(videoContent);

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
        int quan = Integer.parseInt(quantity);
        if (quan <= 0) {
            layoutActionBuy.setVisibility(View.GONE);
            btnOutStock.setVisibility(View.VISIBLE);
        } else {
            layoutActionBuy.setVisibility(View.VISIBLE);
            btnOutStock.setVisibility(View.GONE);
        }

        String formattedNumber = CurrencyUtils.formatCurrency(String.valueOf(price)); // Format the integer directly
        tvPriceProduct.setText(formattedNumber);
        if (!colorList.isEmpty()) {
            selectedColors = colorList.get(0).getTitle();
            colorOption = colorList.get(0);
            ColorAdapter.OnColorItemClickListener colorItemClickListener = color -> {
                colorOption = color;
                selectedColors = color.getTitle();
                if (color.getFeesArise() != null) {
                    feesAriseColor = Integer.parseInt(color.getFeesArise());
                    tvPriceProduct.setText(CurrencyUtils.formatCurrency(String.valueOf(price + feesAriseColor + feesAriseRom + feesAriseRam)));
                } else {
                    feesAriseColor = 0;
                    tvPriceProduct.setText(CurrencyUtils.formatCurrency(String.valueOf(price + feesAriseColor + feesAriseRom + feesAriseRam)));
                }
            };
            ColorAdapter adapter = new ColorAdapter(colorList, colorItemClickListener);
            recyColorsProduct.setAdapter(adapter);
        } else {
            recyColorsProduct.setVisibility(View.GONE);
            tvColor.setVisibility(View.GONE);
        }
        if (!ramList.isEmpty()) {
            selectedRams = ramList.get(0).getTitle();
            ramOption = ramList.get(0);
            RamAdapter.OnRamItemClickListener ramItemClickListener = ram -> {
                ramOption = ram;
                selectedRams = ram.getTitle();
                if (ram.getFeesArise() != null) {
                    feesAriseRam = Integer.parseInt(ram.getFeesArise());
                    tvPriceProduct.setText(CurrencyUtils.formatCurrency(String.valueOf(price + feesAriseColor + feesAriseRom + feesAriseRam)));
                } else {
                    feesAriseRam = 0;
                    tvPriceProduct.setText(CurrencyUtils.formatCurrency(String.valueOf(price + feesAriseColor + feesAriseRom + feesAriseRam)));
                }
            };
            RamAdapter adapter1 = new RamAdapter(ramList, ramItemClickListener);
            recyDungLuong.setAdapter(adapter1);
        } else {
            recyDungLuong.setVisibility(View.GONE);
            tvRam.setVisibility(View.GONE);
        }
        if (!romList.isEmpty()) {
            romOption = romList.get(0);
            selectedRoms = romList.get(0).getTitle();
            RomAdapter.OnRomItemClickListener romItemClickListener = rom -> {
                romOption = rom;
                selectedRoms = rom.getTitle();
                if (rom.getFeesArise() != null) {
                    feesAriseRom = Integer.parseInt(rom.getFeesArise());
                    tvPriceProduct.setText(CurrencyUtils.formatCurrency(String.valueOf(price + feesAriseColor + feesAriseRom + feesAriseRam)));
                } else {
                    feesAriseRom = 0;
                    tvPriceProduct.setText(CurrencyUtils.formatCurrency(String.valueOf(price + feesAriseColor + feesAriseRom + feesAriseRam)));
                }
            };
            RomAdapter adapter2 = new RomAdapter(romList, romItemClickListener);
            recyRom.setAdapter(adapter2);
        } else {
            tvRom.setVisibility(View.GONE);
            recyRom.setVisibility(View.GONE);
        }
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

        String token = preferenceManager.getString("token");
        title = getIntent().getStringExtra("title");
        price = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("price")));
        id = getIntent().getStringExtra("id");
        quantity = getIntent().getStringExtra("quantity");
        imgCover = getIntent().getStringExtra("imgCover");
        getProduct = (GetListProductResponse.Product) getIntent().getSerializableExtra("product");
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
            optionArrayList.add(new Cart.Option(item.getType(), item.getTitle(), item.getContent(), item.getQuantity(), item.getFeesArise()));
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
                        Log.e("error", Objects.requireNonNull(t.getMessage()));
                        Toast.makeText(DetailProductActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                LoadingDialog.dismissProgressDialog();
                Log.e("error", Objects.requireNonNull(e.getMessage()));
                Toast.makeText(DetailProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }


    }

    private void onclickByNow() {
        btnBuyNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderActivity.class);
            ListOder listOder = new ListOder();
            ArrayList<Cart.Option> optionArrayList = new ArrayList<>();
            if (colorOption != null) {
                if (colorOption.getFeesArise() != null) {
                    optionArrayList.add(new Cart.Option(colorOption.getType(), colorOption.getTitle(), colorOption.getContent(), colorOption.getQuantity(), colorOption.getFeesArise()));
                } else {
                    optionArrayList.add(new Cart.Option(colorOption.getType(), colorOption.getTitle(), colorOption.getContent(), colorOption.getQuantity(), "0"));
                }
            }
            if (ramOption != null) {
                if (ramOption.getFeesArise() != null) {
                    optionArrayList.add(new Cart.Option(ramOption.getType(), ramOption.getTitle(), ramOption.getContent(), ramOption.getQuantity(), ramOption.getFeesArise()));
                } else {
                    optionArrayList.add(new Cart.Option(ramOption.getType(), ramOption.getTitle(), ramOption.getContent(), ramOption.getQuantity(), "0"));
                }
            }
            if (romOption != null) {
                if (romOption.getFeesArise() != null) {
                    optionArrayList.add(new Cart.Option(romOption.getType(), romOption.getTitle(), romOption.getContent(), romOption.getQuantity(), romOption.getFeesArise()));
                } else {
                    optionArrayList.add(new Cart.Option(romOption.getType(), romOption.getTitle(), romOption.getContent(), romOption.getQuantity(), "0"));
                }
            }
            List<Cart> list = new ArrayList<>();

            for (Cart.Option op : optionArrayList) {
                if (Integer.parseInt(op.getQuantity()) == 0) {
                    AlertDialogUtil.showAlertDialogWithOk(this, "Sản phẩm tạm hết hàng");
                    isOutOfStock = false;
                }
            }
            if (isOutOfStock) {
                list.add(new Cart(getProduct.get_id(), preferenceManager.getString("userId"), getProduct.getTitle(), optionArrayList, Integer.parseInt(getProduct.getPrice()), 1, getProduct.getImg_cover(), 1));
                listOder.setList(list);
                intent.putExtra("listOder", listOder);
                startActivity(intent);

            }

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