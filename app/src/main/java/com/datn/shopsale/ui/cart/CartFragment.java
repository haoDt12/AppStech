package com.datn.shopsale.ui.cart;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.OrderActivity;
import com.datn.shopsale.adapter.CartAdapter;
import com.datn.shopsale.modelsv2.DataListOrder;
import com.datn.shopsale.modelsv2.Product;
import com.datn.shopsale.modelsv2.ProductCart;
import com.datn.shopsale.responsev2.BaseResponse;
import com.datn.shopsale.responsev2.ProductCartResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {
    private List<ProductCart> cartList;
    private CartAdapter cartAdapter;
    private RecyclerView rcvCart;
    private ImageView imgEmptyCart;
    private RelativeLayout linnerCheckout;
    private Button btnCheckout;
    PreferenceManager preferenceManager;
    private ApiService apiService;
    private TextView Tvsum;
    private int tong = 0;
    private CheckBox chk_selectAll;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private DataListOrder dataListOrder;
    private List<Product> productList;

    public CartFragment() {
    }

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        initView(root);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        chk_selectAll.setChecked(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = RetrofitConnection.getApiService();
        initView(view);
        preferenceManager = new PreferenceManager(requireActivity());
        dataListOrder = new DataListOrder();
        dataListOrder.setList(new ArrayList<>());
        getDataCart();
        onFragmentResult();
        btnCheckout.setOnClickListener(v -> {
            if (dataListOrder != null && dataListOrder.getList() != null && dataListOrder.getList().size() != 0) {
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("listOrder", dataListOrder);
                intent.putExtras(bundle);
                activityResultLauncher.launch(intent);
            } else {
                Toast.makeText(getActivity(), "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView(@NonNull View view) {
        imgEmptyCart = (ImageView) view.findViewById(R.id.img_empty_cart);
        linnerCheckout = (RelativeLayout) view.findViewById(R.id.linner_checkout);
        rcvCart = view.findViewById(R.id.rcv_cart);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        Tvsum = view.findViewById(R.id.sum);
        chk_selectAll = view.findViewById(R.id.chk_selectedAll);
        productList = new ArrayList<>();
        dataListOrder = new DataListOrder();
        productList = new ArrayList<>();
        checkAll();
    }

    private void getDataCart() {
        LoadingDialog.showProgressDialog(getActivity(), "Đang Tải...");
        cartList = new ArrayList<>();
        Call<ProductCartResponse> call = apiService.getProductCart(preferenceManager.getString("token"),
                preferenceManager.getString("userId"));
        call.enqueue(new Callback<ProductCartResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductCartResponse> call, @NonNull Response<ProductCartResponse> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    if (response.body().getProductCart().size() == 0) {
                        imgEmptyCart.setVisibility(View.VISIBLE);
                        rcvCart.setVisibility(View.GONE);
                        linnerCheckout.setVisibility(View.GONE);
                    } else {
                        imgEmptyCart.setVisibility(View.GONE);
                        rcvCart.setVisibility(View.VISIBLE);
                        linnerCheckout.setVisibility(View.VISIBLE);
                        for (ProductCart item : response.body().getProductCart()) {
                            ProductCart objCart = new ProductCart();
                            objCart.set_id(item.get_id());
                            objCart.setProductCart(item.getProductCart());
                            objCart.setCustomer_id(item.getCustomer_id());
                            objCart.setCreate_time(item.getCreate_time());
                            objCart.setQuantity(item.getQuantity());
                            cartList.add(objCart);
                        }
                    }
                    requireActivity().runOnUiThread(() -> {
                        Tvsum.setText(CurrencyUtils.formatCurrency(String.valueOf(tong)));
                        cartAdapter = new CartAdapter(cartList, getActivity(), new IChangeQuantity() {
                            @Override
                            public void IclickReduce(ProductCart objCart, int index) {
                                ReduceQuantity(objCart, index);
                            }

                            @Override
                            public void IclickIncrease(ProductCart objCart, int index) {
                                IncreaseQuantity(objCart, index);
                            }

                            @Override
                            public void IclickCheckBox(ProductCart objCart, int index, boolean checkAll) {
                                tong += objCart.getQuantity() * Integer.parseInt(objCart.getProductCart().getPrice());
                                Tvsum.setText(CurrencyUtils.formatCurrency(String.valueOf(tong)));
                                chk_selectAll.setChecked(checkAll);
                                Product product = objCart.getProductCart();
                                product.setQuantity(String.valueOf(objCart.getQuantity()));
                                product.setProductCartId(objCart.get_id());
                                dataListOrder.getList().add(product);
                            }

                            @Override
                            public void IclickCheckBox2(ProductCart objCart, int index) {
                                tong -= objCart.getQuantity() * Integer.parseInt(objCart.getProductCart().getPrice());
                                Tvsum.setText(CurrencyUtils.formatCurrency(String.valueOf(tong)));
                                if (chk_selectAll.isChecked()) {
                                    chk_selectAll.setChecked(false);
                                }
                                dataListOrder.getList().remove(objCart.getProductCart());
                            }
                        });
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                        rcvCart.setLayoutManager(linearLayoutManager);
                        rcvCart.setAdapter(cartAdapter);
                        LoadingDialog.dismissProgressDialog();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(requireActivity(), response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductCartResponse> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());
                });
            }
        });

    }

    private void ReduceQuantity(ProductCart objCart, int index) {
        LoadingDialog.showProgressDialog(requireActivity(), "Loading...");
        String token = preferenceManager.getString("token");
        String caculation = Constants.btnReduce;
        String productId = objCart.getProductCart().get_id();
        String idUser = preferenceManager.getString("userId");
        Call<BaseResponse> call = apiService.updateCart(token, idUser, productId, caculation);
        call.enqueue(new Callback<BaseResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                requireActivity().runOnUiThread(LoadingDialog::dismissProgressDialog);
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    if (objCart.getStatus() == 2) {
                        tong = tong - Integer.parseInt(objCart.getProductCart().getPrice());
                        Tvsum.setText(CurrencyUtils.formatCurrency(String.valueOf(tong)));
                    }
                    cartList.get(index).setQuantity(cartList.get(index).getQuantity() - 1);
                    if (cartList.get(index).getQuantity() == 0) {
                        cartList.remove(index);
                    }
                    cartAdapter.notifyDataSetChanged();
                } else {
                    requireActivity().runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(requireActivity(), response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());
                });
            }
        });
    }

    private void IncreaseQuantity(ProductCart objCart, int index) {
        String token = preferenceManager.getString("token");
        String caculation = Constants.btnIncrease;
        String productId = objCart.getProductCart().get_id();
        String idUser = preferenceManager.getString("userId");
        Call<BaseResponse> call = apiService.updateCart(token, idUser, productId, caculation);
        call.enqueue(new Callback<BaseResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    if (objCart.getStatus() == 2) {
                        tong = tong + Integer.parseInt(objCart.getProductCart().getPrice());
                        Tvsum.setText(CurrencyUtils.formatCurrency(String.valueOf(tong)));
                    }
                    cartList.get(index).setQuantity(cartList.get(index).getQuantity() + 1);
                    cartAdapter.notifyDataSetChanged();
                } else {
                    requireActivity().runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(requireActivity(), response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());
                });
            }
        });
    }

    private void onFragmentResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                productList.clear();
                tong = 0;
                chk_selectAll.setChecked(false);
                Tvsum.setText(CurrencyUtils.formatCurrency(String.valueOf(tong)));
                getDataCart();
            }
        });
    }

    private void checkAll() {
        chk_selectAll.setOnClickListener(v -> {
            productList.clear();
            tong = 0;
            boolean isCheck = chk_selectAll.isChecked();
            if (isCheck) {
                for (ProductCart item : cartList) {
                    item.setStatus(2);
                    tong += item.getQuantity() * Integer.parseInt(item.getProductCart().getPrice());
                }
                cartAdapter.setList(cartList);
                for (ProductCart productCart : cartList) {
                    Product product = productCart.getProductCart();
                    product.setQuantity(String.valueOf(productCart.getQuantity()));
                    product.setProductCartId(productCart.get_id());
                    productList.add(product);
                }
            } else {
                for (ProductCart item : cartList) {
                    item.setStatus(1);
                }
                cartAdapter.setList(cartList);
            }
            Tvsum.setText(CurrencyUtils.formatCurrency(String.valueOf(tong)));
            dataListOrder.setList(productList);
        });

    }
}