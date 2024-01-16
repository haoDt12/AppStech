package com.datn.shopsale.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.OrderAdapter;
import com.datn.shopsale.apizalopay.CreateOrder;
import com.datn.shopsale.modelsv2.DataListOrder;
import com.datn.shopsale.modelsv2.ListOrder;
import com.datn.shopsale.modelsv2.MapVoucherCus;
import com.datn.shopsale.modelsv2.Product;
import com.datn.shopsale.request.CreateOrderRequest;
import com.datn.shopsale.responsev2.CreateOrderResponse;
import com.datn.shopsale.responsev2.GetDeliveryAddressResponse;
import com.datn.shopsale.responsev2.GetPriceZaloPayResponseV2;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.dashboard.address.AddressActivity;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class OrderActivity extends AppCompatActivity {
    private final int MONEY = 0;
    private final int E_BANKING = 1;
    private final int ZALO_PAY = 2;
    private int actionPAY = 0;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private TextView tvTotal;
    private TextView tvSumMoney;
    private TextView tvGiamGia;
    private Button btnOder;
    private int sumMoney = 0;
    private String address;
    private Button btnMoney;
    private Button btnEBanking;
    private Button btnZaloPay;
    private static final int REQUEST_CODE = 111;

    private TextView tvName, tvPhone, tvCity, tvStreet;
    private static final int REQUEST_SELECT_ADDRESS = 1;
    private static final int REQUEST_SELECT_VOUCHER = 2;
    private TextView tvPriceVoucher;
    private TextView tvVoucher;
    private MapVoucherCus voucher;
    private List<Product> listOrderProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        initView();
        getDataAddress();
    }

    private void initView() {
        Toolbar toolbarOder = findViewById(R.id.toolbar_oder);
        LinearLayout lnlAddressOrder = findViewById(R.id.lnl_order_address);
        btnMoney = findViewById(R.id.btn_money);
        btnEBanking = findViewById(R.id.btn_e_banking);
        tvName = findViewById(R.id.tv_name);
        tvGiamGia = findViewById(R.id.tv_giam_gia);
        tvPhone = findViewById(R.id.tv_phone);
        tvCity = findViewById(R.id.tv_city);
        tvStreet = findViewById(R.id.tv_street);
        TextView tvQuantity = findViewById(R.id.tv_quantity);
        tvTotal = findViewById(R.id.tv_total);
        TextView tvShipPrice = findViewById(R.id.tv_ship_price);
        tvSumMoney = findViewById(R.id.tv_sum_money);
        btnOder = findViewById(R.id.btn_oder);
        btnZaloPay = findViewById(R.id.btn_zalo_pay);
        LinearLayout lnlVoucher = findViewById(R.id.lnl_voucher);
        tvPriceVoucher = findViewById(R.id.tv_price_voucher);
        tvVoucher = findViewById(R.id.tv_voucher);
        tvGiamGia.setText(getString(R.string.b_n_c_mu_n_ch_n_voucher));
        RecyclerView recyclerView = findViewById(R.id.rcv_order);
        setSupportActionBar(toolbarOder);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarOder.setNavigationOnClickListener(v -> onBackPressed());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        DataListOrder dataListOrder = (DataListOrder) bundle.getSerializable("listOrder");
        assert dataListOrder != null;
        listOrderProduct = dataListOrder.getList();
        tvQuantity.setText(String.valueOf(listOrderProduct.size()));
        tvShipPrice.setText(getResources().getText(R.string.vnd_0));
        tvVoucher.setText(getResources().getText(R.string.vnd_0));
        for (Product item : listOrderProduct) {
            sumMoney = sumMoney + Integer.parseInt(item.getPrice()) * Integer.parseInt(item.getQuantity());
        }
        tvTotal.setText(CurrencyUtils.formatCurrency(String.valueOf(sumMoney)));
        tvSumMoney.setText(CurrencyUtils.formatCurrency(String.valueOf(sumMoney)));
        OrderAdapter adapter = new OrderAdapter(listOrderProduct, this);
        recyclerView.setAdapter(adapter);
        onSelectPayAction(btnMoney);
        onMoney();
        onEBanking();
        onZaloPay();
        onPay();

        lnlAddressOrder.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, AddressActivity.class);
            intent1.putExtra("select", "oke");
            startActivityForResult(intent1, REQUEST_SELECT_ADDRESS);
        });
        lnlVoucher.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, VoucherActivity.class);
            startActivityForResult(intent1, REQUEST_SELECT_VOUCHER);
        });
    }

    private void getDataAddress() {
        LoadingDialog.showProgressDialog(OrderActivity.this, "Loading...");
        Call<GetDeliveryAddressResponse> call = apiService.getDeliveryAddress(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetDeliveryAddressResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetDeliveryAddressResponse> call, @NonNull Response<GetDeliveryAddressResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        if (response.body().getAddress().size() != 0){
                            runOnUiThread(new TimerTask() {
                                @Override
                                public void run() {
                                    address = response.body().getAddress().get(0).get_id();
                                    tvName.setVisibility(View.VISIBLE);
                                    tvPhone.setVisibility(View.VISIBLE);
                                    tvCity.setVisibility(View.VISIBLE);
                                    tvStreet.setVisibility(View.VISIBLE);

                                    tvName.setText(response.body().getAddress().get(0).getName());
                                    tvPhone.setText(response.body().getAddress().get(0).getPhone());
                                    tvCity.setText(response.body().getAddress().get(0).getCity());
                                    tvStreet.setText(response.body().getAddress().get(0).getStreet());
                                }
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(OrderActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this, response.body().getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetDeliveryAddressResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this, t.getMessage());
                });
            }
        });
    }

    private void onMoney() {
        btnMoney.setOnClickListener(v -> {
            actionPAY = MONEY;
            onSelectPayAction(btnMoney);
        });
    }

    private void onEBanking() {
        btnEBanking.setOnClickListener(v -> {
            actionPAY = E_BANKING;
            onSelectPayAction(btnEBanking);
        });
    }

    private void onZaloPay() {
        btnZaloPay.setOnClickListener(v -> {
            actionPAY = ZALO_PAY;
            onSelectPayAction(btnZaloPay);
        });
    }

    private void onPay() {
        btnOder.setOnClickListener(v -> {
            switch (actionPAY) {
                case MONEY:
                    oderMoney();
                    break;
                case E_BANKING:
                    orderEBanking();
                    break;
                case ZALO_PAY:
                    orderZaloPay();
                    break;
            }
        });
    }

    private void oderMoney() {
        LoadingDialog.showProgressDialog(this, "Đang Tải");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<ListOrder> listOrders = new ArrayList<>();
        for (Product item : listOrderProduct) {
            ListOrder order = new ListOrder();
            order.setQuantity(item.getQuantity());
            order.setProduct_id(item.get_id());
            order.setProductCartId(item.getProductCartId());
            listOrders.add(order);
        }
        createOrderRequest.setList_order(listOrders);
        if (voucher != null) {
            createOrderRequest.setMap_voucher_cus_id(voucher.get_id());
        }
        createOrderRequest.setDelivery_address_id(address);
        Call<CreateOrderResponse> call = apiService.createOrderV2(preferenceManager.getString("token"), createOrderRequest);
        call.enqueue(new Callback<CreateOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateOrderResponse> call, @NonNull Response<CreateOrderResponse> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                        setResult(Activity.RESULT_OK);
                        finish();

                    });
                } else {
                    runOnUiThread(() -> {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(OrderActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this, response.body().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreateOrderResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
    }

    private void orderEBanking() {
        if (address == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notification");
            builder.setMessage("Vui lòng chọn địa chỉ");

            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            Intent intent = new Intent(this, EBankingPayActivity.class);
            CreateOrderRequest createOrderRequest = new CreateOrderRequest();
            List<ListOrder> listOrders = new ArrayList<>();
            for (Product item : listOrderProduct) {
                ListOrder order = new ListOrder();
                order.setQuantity(item.getQuantity());
                order.setProduct_id(item.get_id());
                order.setProductCartId(item.getProductCartId());
                listOrders.add(order);
            }
            createOrderRequest.setList_order(listOrders);
            if (voucher != null) {
                createOrderRequest.setMap_voucher_cus_id(voucher.get_id());
            }
            createOrderRequest.setDelivery_address_id(address);
            createOrderRequest.setAmount("");
            createOrderRequest.setBankCode("");
            createOrderRequest.setLanguage("vn");
            Bundle bundle = new Bundle();
            bundle.putSerializable("requestOrderVnPay", createOrderRequest);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private void orderZaloPay() {
        LoadingDialog.showProgressDialog(this, "Đang Tải");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<ListOrder> listOrders = new ArrayList<>();
        for (Product item : listOrderProduct) {
            ListOrder order = new ListOrder();
            order.setQuantity(item.getQuantity());
            order.setProduct_id(item.get_id());
            order.setProductCartId(item.getProductCartId());
            listOrders.add(order);
        }
        createOrderRequest.setList_order(listOrders);
        if (voucher != null) {
            createOrderRequest.setMap_voucher_cus_id(voucher.get_id());
        }
        createOrderRequest.setDelivery_address_id(address);
        Call<GetPriceZaloPayResponseV2> call = apiService.getPriceOrderZaloPayV2(preferenceManager.getString("token"), createOrderRequest);
        call.enqueue(new Callback<GetPriceZaloPayResponseV2>() {
            @Override
            public void onResponse(@NonNull Call<GetPriceZaloPayResponseV2> call, @NonNull Response<GetPriceZaloPayResponseV2> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    runOnUiThread(() -> createOrderZaloPay(response.body().getTotal_amount()));
                } else {
                    runOnUiThread(() -> {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(OrderActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this, response.body().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetPriceZaloPayResponseV2> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this, t.getMessage());
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
    }

    private void onSelectPayAction(Button btn) {
        int backgroundColor = ContextCompat.getColor(this, R.color.white);
        int textColor = ContextCompat.getColor(this, R.color.black);

        ViewCompat.setBackgroundTintList(btnZaloPay, android.content.res.ColorStateList.valueOf(backgroundColor));
        ViewCompat.setBackgroundTintList(btnEBanking, android.content.res.ColorStateList.valueOf(backgroundColor));
        ViewCompat.setBackgroundTintList(btnMoney, android.content.res.ColorStateList.valueOf(backgroundColor));

        btnMoney.setTextColor(textColor);
        btnZaloPay.setTextColor(textColor);
        btnEBanking.setTextColor(textColor);

        ViewCompat.setBackgroundTintList(btn, android.content.res.ColorStateList.valueOf(textColor));
        btn.setTextColor(backgroundColor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                String resultValue = data.getStringExtra("action");
                assert resultValue != null;
                if (resultValue.equals("1")) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        }
        if (requestCode == REQUEST_SELECT_ADDRESS && resultCode == RESULT_OK) {
            assert data != null;

            String name = data.getStringExtra("nameAddress");
            String phone = data.getStringExtra("phoneAddress");
            String city = data.getStringExtra("cityAddress");
            String street = data.getStringExtra("streetAddress");
            address = data.getStringExtra("addressId");

            tvName.setVisibility(View.VISIBLE);
            tvPhone.setVisibility(View.VISIBLE);
            tvCity.setVisibility(View.VISIBLE);
            tvStreet.setVisibility(View.VISIBLE);

            tvName.setText(name);
            tvPhone.setText(phone);
            tvCity.setText(city);
            tvStreet.setText(street);

        }
        if (requestCode == REQUEST_SELECT_VOUCHER) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                voucher = (MapVoucherCus) data.getSerializableExtra("voucher");
                assert voucher != null;
                tvGiamGia.setText(voucher.getVocher_id().getContent());
                tvPriceVoucher.setText(voucher.getVocher_id().getPrice());
            }
        }
    }

    private void createOrderZaloPay(String amount) {
        CreateOrder orderApi = new CreateOrder();

        try {
            JSONObject data = orderApi.createOrder(amount);
            String code = data.getString("return_code");

            if (code.equals("1")) {

                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(OrderActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                        runOnUiThread(() -> {
                            LoadingDialog.dismissProgressDialog();
                            callApiOrderZaloPay();
                        });

                    }

                    @Override
                    public void onPaymentCanceled(String zpTransToken, String appTransID) {
                        runOnUiThread(() -> {
                            LoadingDialog.dismissProgressDialog();
                            new AlertDialog.Builder(OrderActivity.this)
                                    .setTitle("User Cancel Payment")
                                    .setMessage("Đã huỷ thanh toán")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                    })
                                    .setNegativeButton("Cancel", null).show();
                        });
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                        runOnUiThread(() -> {
                            LoadingDialog.dismissProgressDialog();
                            new AlertDialog.Builder(OrderActivity.this)
                                    .setTitle("Payment Fail")
                                    .setMessage(String.format("ZaloPayErrorCode: %s \nTransToken: %s", zaloPayError.toString(), zpTransToken))
                                    .setPositiveButton("OK", (dialog, which) -> {
                                    })
                                    .setNegativeButton("Cancel", null).show();
                        });
                    }
                });
            } else {
                LoadingDialog.dismissProgressDialog();
                AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this, "Error Payment ZaloPay");
            }

        } catch (Exception e) {
            LoadingDialog.dismissProgressDialog();
            AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this, "Error Payment ZaloPay");
            e.printStackTrace();
        }
    }

    private void callApiOrderZaloPay() {
        LoadingDialog.showProgressDialog(this, "Đang Tải");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<ListOrder> listOrders = new ArrayList<>();
        for (Product item : listOrderProduct) {
            ListOrder order = new ListOrder();
            order.setQuantity(item.getQuantity());
            order.setProduct_id(item.get_id());
            order.setProductCartId(item.getProductCartId());
            listOrders.add(order);
        }
        createOrderRequest.setList_order(listOrders);
        if (voucher != null) {
            createOrderRequest.setMap_voucher_cus_id(voucher.get_id());
        }
        createOrderRequest.setDelivery_address_id(address);
        Call<CreateOrderResponse> call = apiService.createOrderZaloPay(preferenceManager.getString("token"), createOrderRequest);
        call.enqueue(new Callback<CreateOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateOrderResponse> call, @NonNull Response<CreateOrderResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                        new AlertDialog.Builder(OrderActivity.this)
                                .setTitle("Payment Success")
                                .setMessage("Đã thanh toán")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                })
                                .setNegativeButton("Cancel", null).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(OrderActivity.this, response.body().getMessage());
                        } else {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(OrderActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this, response.body().getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreateOrderResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this, t.getMessage());
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sumMoney = 0;

        for (Product item : listOrderProduct) {
            sumMoney = sumMoney + Integer.parseInt(item.getPrice()) * Integer.parseInt(item.getQuantity());
        }
        String price = tvPriceVoucher.getText().toString();
        tvVoucher.setText(CurrencyUtils.formatCurrency(price));
        tvTotal.setText(CurrencyUtils.formatCurrency(String.valueOf(sumMoney)));
        tvSumMoney.setText(CurrencyUtils.formatCurrency(String.valueOf(sumMoney - Integer.parseInt(price))));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}