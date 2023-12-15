package com.datn.shopsale.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.datn.shopsale.models.Address;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.ListOder;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.request.OderRequest;
import com.datn.shopsale.response.GetListVoucher;
import com.datn.shopsale.response.GetPriceZaloPayResponse;
import com.datn.shopsale.response.ResponseAddress;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.dashboard.address.AddressActivity;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private ListOder listOder;
    private ApiService apiService;
    private PreferenceManager preferenceManager;
    private Toolbar toolbarOder;
    private TextView tvQuantity;
    private TextView tvTotal;
    private TextView tvShipPrice;
    private TextView tvSumMoney;
    private TextView tvGiamGia;
    private Button btnOder;
    private ArrayList<Address> dataList = new ArrayList<>();
    private int sumMoney = 0,sumPriceProduct = 0;
    private String address;
    private Button btnMoney;
    private Button btnEBanking;
    private Button btnZaloPay;
    private static final int REQUEST_CODE = 111;
    private LinearLayout lnlAddressOrder;
    private LinearLayout lnlVoucher;

    private RecyclerView recyclerView;
    private TextView tvName, tvPhone, tvCity, tvStreet;
    private static final int REQUEST_SELECT_ADDRESS = 1;
    private static final int REQUEST_SELECT_VOUCHER = 2;
    private TextView tvPriceVoucher;
    private TextView tvVoucher;
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
        toolbarOder = (Toolbar) findViewById(R.id.toolbar_oder);
        lnlAddressOrder = (LinearLayout) findViewById(R.id.lnl_order_address);
        recyclerView = findViewById(R.id.rcv_order);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvGiamGia = (TextView) findViewById(R.id.tv_giam_gia);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvStreet = (TextView) findViewById(R.id.tv_street);
        tvQuantity = (TextView) findViewById(R.id.tv_quantity);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvShipPrice = (TextView) findViewById(R.id.tv_ship_price);
        tvSumMoney = (TextView) findViewById(R.id.tv_sum_money);
        btnOder = (Button) findViewById(R.id.btn_oder);
        btnMoney = (Button) findViewById(R.id.btn_money);
        btnEBanking = (Button) findViewById(R.id.btn_e_banking);
        btnZaloPay = (Button) findViewById(R.id.btn_zalo_pay);
        lnlVoucher = (LinearLayout) findViewById(R.id.lnl_voucher);
        tvPriceVoucher = (TextView) findViewById(R.id.tv_price_voucher);
        tvVoucher = (TextView) findViewById(R.id.tv_voucher);
        tvGiamGia.setText(getString(R.string.b_n_c_mu_n_ch_n_voucher));
        RecyclerView recyclerView = findViewById(R.id.rcv_order);
        setSupportActionBar(toolbarOder);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarOder.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        Intent intent = getIntent();
        if (intent.hasExtra("listOder")) {
            listOder = (ListOder) intent.getSerializableExtra("listOder");
        }
        assert listOder != null;
        tvQuantity.setText(String.valueOf(listOder.getList().size()));
        tvShipPrice.setText(getResources().getText(R.string.vnd_0));
        tvVoucher.setText(getResources().getText(R.string.vnd_0));
        for (Cart item : listOder.getList()) {
            for (Cart.Option option: item.getOption()) {
                if(option.getFeesArise() != null){
                    sumPriceProduct += Integer.parseInt(option.getFeesArise());
                }
            }
            sumMoney = sumMoney + (item.getPrice() + sumPriceProduct) * item.getQuantity();
            sumPriceProduct = 0;
        }
        tvTotal.setText(String.valueOf(sumMoney));
        tvSumMoney.setText(String.valueOf(sumMoney));
        OrderAdapter adapter = new OrderAdapter(listOder);
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
        String idUser = preferenceManager.getString("userId");

        Call<ResponseAddress.Root> call = apiService.getAddress(preferenceManager.getString("token"), idUser);
        call.enqueue(new Callback<ResponseAddress.Root>() {
            @Override
            public void onResponse(@NonNull Call<ResponseAddress.Root> call, @NonNull Response<ResponseAddress.Root> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    runOnUiThread(() -> {
                        for (ResponseAddress.Address item : response.body().getUser().getAddress()) {
                            dataList.add(new Address(item.get_id(), item.getUserId(), item.getName(), item.getCity(), item.getStreet(), item.getPhone_number()));
                        }
                    });

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseAddress.Root> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        List<OderRequest.Product> listProduct = new ArrayList<>();
        for (Cart item : listOder.getList()) {
            ArrayList<OderRequest.Option> optionList = new ArrayList<>();
            for (Cart.Option option : item.getOption()) {
                if(option.getFeesArise() != null){
                    optionList.add(new OderRequest.Option(option.getType(), option.getTitle(), option.getContent(), option.getFeesArise()));
                }else {
                    optionList.add(new OderRequest.Option(option.getType(), option.getTitle(), option.getContent(), "0"));
                }
            }
            listProduct.add(new OderRequest.Product(item.getProductId(), optionList, item.getQuantity()));
        }
        OderRequest.Root request = new OderRequest.Root();
        request.setProduct(listProduct);
        request.setUserId(preferenceManager.getString("userId"));
        request.setAddress(address);
        LoadingDialog.showProgressDialog(this, "Đang Tải");
        Call<ResApi> call = apiService.createOrder(preferenceManager.getString("token"), request);
        call.enqueue(new Callback<ResApi>() {
            @Override
            public void onResponse(@NonNull Call<ResApi> call, @NonNull Response<ResApi> response) {
                assert response.body() != null;
                if (response.body().code == 1) {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                        setResult(Activity.RESULT_OK);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResApi> call, @NonNull Throwable t) {
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
            builder.setTitle("Noification");
            builder.setMessage("Vui lòng chọn địa chỉ");

            builder.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            Intent intent = new Intent(this, EBankingPayActivity.class);
            intent.putExtra("listOder", listOder);
            preferenceManager.putString("addressOrder", address);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private void orderZaloPay() {
        List<OderRequest.Product> listProduct = new ArrayList<>();
        ArrayList<OderRequest.Option> optionList = new ArrayList<>();
        for (Cart item : listOder.getList()) {
            for (Cart.Option option : item.getOption()) {
                optionList.add(new OderRequest.Option(option.getType(), option.getTitle(), option.getContent(), option.getFeesArise()));
            }
            listProduct.add(new OderRequest.Product(item.getProductId(), optionList, item.getQuantity()));
        }
        OderRequest.Root request = new OderRequest.Root();
        request.setProduct(listProduct);
        request.setUserId(preferenceManager.getString("userId"));
        request.setAddress(address);
        LoadingDialog.showProgressDialog(this, "Đang Tải");
        Call<GetPriceZaloPayResponse> call = apiService.getPriceOrderZaloPay(preferenceManager.getString("token"), request);
        call.enqueue(new Callback<GetPriceZaloPayResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetPriceZaloPayResponse> call, @NonNull Response<GetPriceZaloPayResponse> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                        createOrderZaloPay(String.valueOf(response.body().getPrice()));

                    });
                } else {
                    runOnUiThread(() -> {
                        AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this, response.body().getMessage());
                        LoadingDialog.dismissProgressDialog();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetPriceZaloPayResponse> call, @NonNull Throwable t) {
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
                GetListVoucher.ListVoucher voucher = (GetListVoucher.ListVoucher) data.getSerializableExtra("voucher");
                String price = data.getStringExtra("price");
                assert voucher != null;
                tvGiamGia.setText(voucher.getContent());
                tvPriceVoucher.setText(price);
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
                        callApiOrderZaloPay(transactionId,transToken);
                    }
                    @Override
                    public void onPaymentCanceled(String zpTransToken, String appTransID) {
                        new AlertDialog.Builder(OrderActivity.this)
                                .setTitle("User Cancel Payment")
                                .setMessage(String.format("zpTransToken: %s \n", zpTransToken))
                                .setPositiveButton("OK", (dialog, which) -> {
                                })
                                .setNegativeButton("Cancel", null).show();
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                        new AlertDialog.Builder(OrderActivity.this)
                                .setTitle("Payment Fail")
                                .setMessage(String.format("ZaloPayErrorCode: %s \nTransToken: %s", zaloPayError.toString(), zpTransToken))
                                .setPositiveButton("OK", (dialog, which) -> {
                                })
                                .setNegativeButton("Cancel", null).show();
                    }
                });
            }else {
                AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this,"Error Payment ZaloPay");
            }

        } catch (Exception e) {
            AlertDialogUtil.showAlertDialogWithOk(OrderActivity.this,"Error Payment ZaloPay");
            e.printStackTrace();
        }
    }
    private void callApiOrderZaloPay(String transactionId, String transToken){
        List<OderRequest.Product> listProduct = new ArrayList<>();
        ArrayList<OderRequest.Option> optionList = new ArrayList<>();
        for (Cart item : listOder.getList()) {
            for (Cart.Option option : item.getOption()) {
                optionList.add(new OderRequest.Option(option.getType(), option.getTitle(), option.getContent(), option.getFeesArise()));
            }
            listProduct.add(new OderRequest.Product(item.getProductId(), optionList, item.getQuantity()));
        }
        OderRequest.Root request = new OderRequest.Root();
        request.setProduct(listProduct);
        request.setUserId(preferenceManager.getString("userId"));
        request.setAddress(address);
        LoadingDialog.showProgressDialog(this, "Đang Tải");
        Call<ResApi> call = apiService.createOrderZaloPay(preferenceManager.getString("token"), request);
        call.enqueue(new Callback<ResApi>() {
            @Override
            public void onResponse(@NonNull Call<ResApi> call, @NonNull Response<ResApi> response) {
                assert response.body() != null;
                if (response.body().code == 1) {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                        new AlertDialog.Builder(OrderActivity.this)
                                .setTitle("Payment Success")
                                .setMessage(String.format("TransactionId: %s - TransToken: %s", transactionId, transToken))
                                .setPositiveButton("OK", (dialog, which) -> {
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                })
                                .setNegativeButton("Cancel", null).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(OrderActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResApi> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sumMoney = 0;

        for (Cart item : listOder.getList()) {
            sumMoney = sumMoney + item.getPrice() * item.getQuantity();
        }
        String price = tvPriceVoucher.getText().toString();
        tvVoucher.setText(CurrencyUtils.formatCurrency(price));
        tvTotal.setText(String.valueOf(sumMoney));
        tvSumMoney.setText(String.valueOf(sumMoney - Integer.parseInt(price)));
    }
}