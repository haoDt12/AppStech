package com.datn.shopsale.ui.cart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.OrderActivity;
import com.datn.shopsale.adapter.CartAdapter;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.models.ResponseCart;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.login.SignUpActivity;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {
//    private CartPresenter cartPresenter;
    private List<Cart> cartList;
    private CartAdapter cartAdapter;
    private RecyclerView rcvCart;
    private boolean isToast = true;
    private Button btnCheckout;
    private RelativeLayout layoutCart;
    PreferenceManager preferenceManager ;
    private ApiService apiService;

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

//        cartPresenter.getDataCart(getContext());

        btnCheckout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), OrderActivity.class));
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = RetrofitConnection.getApiService();
        initView(view);
        preferenceManager = new PreferenceManager(getActivity());
        getDataCart();

    }

    private void initView(@NonNull View view) {
        layoutCart = view.findViewById(R.id.layout_cart);
        rcvCart = view.findViewById(R.id.rcv_cart);
        btnCheckout = view.findViewById(R.id.btn_checkout);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void getDataCart() {
        cartList = new ArrayList<>();
        Call<ResponseCart> call = apiService.getDataCart(preferenceManager.getString("token"),
                preferenceManager.getString("userId"));
        call.enqueue(new Callback<ResponseCart>() {
            @Override
            public void onResponse(Call<ResponseCart> call, Response<ResponseCart> response) {
                if(response.body().getCode()==1){
                    for (Cart item: response.body().getListCart()) {
                        Cart objCart = new Cart();
                        objCart.setProductId(item.getProductId());
                        objCart.setTitle(item.getTitle());
                        objCart.setColor(item.getColor());
                        objCart.setQuantity(item.getQuantity());
                        objCart.setImgCover(item.getImgCover());
                        objCart.setPrice(item.getPrice());
                        objCart.setStatus(1);
                        cartList.add(objCart);
                    }
                    getActivity().runOnUiThread(() -> {
                        cartAdapter = new CartAdapter(cartList, getActivity(), new IChangeQuantity() {
                            @Override
                            public void IclickReduce(Cart objCart, int index) {
                                reduceQuantity(objCart,index);
                            }

                            @Override
                            public void IclickIncrease(Cart objCart, int index) {
                                increaseQuantity(objCart,index);
                            }
                        });
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
                        rcvCart.setLayoutManager(linearLayoutManager);
                        rcvCart.setAdapter(cartAdapter);

                    });
                }else {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseCart> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void increaseQuantity(Cart objCart, int index) {
        String token = preferenceManager.getString("token");
        String productId = objCart.getProductId();
        String caculation = Constants.btnIncrease;
        String idUser = preferenceManager.getString("userId");
       try {
           Call<ResApi> call = apiService.editCart(token,idUser,productId,caculation);
           call.enqueue(new Callback<ResApi>() {
               @Override
               public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                   if(response.body().code==1){
                       cartList.get(index).setQuantity(cartList.get(index).getQuantity()+1);

                       cartAdapter.notifyDataSetChanged();
                   }else {
                       Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                   }
               }

               @Override
               public void onFailure(Call<ResApi> call, Throwable t) {
                   Log.e("Error", "onFailure: " + t);
                   Toast.makeText(getActivity(), "error: "+t, Toast.LENGTH_SHORT).show();
               }
           });
       }catch (Exception e){
           Log.e("Error", "onFailure: " + e);
           Toast.makeText(getActivity(), "error: "+e, Toast.LENGTH_SHORT).show();
       }
    }

    private void reduceQuantity(Cart objCart, int index) {
        String token = preferenceManager.getString("token");
        String productId = objCart.getProductId();
        String caculation = Constants.btnReduce;
        String idUser = preferenceManager.getString("userId");
        try {
            Call<ResApi> call = apiService.editCart(token,idUser,productId,caculation);
            call.enqueue(new Callback<ResApi>() {
                @Override
                public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                    if(response.body().code==1){
                        cartList.get(index).setQuantity(cartList.get(index).getQuantity()-1);
                        if(cartList.get(index).getQuantity()==0){
                            cartList.remove(index);
                        }
                        cartAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResApi> call, Throwable t) {
                    Log.e("Error", "onFailure: " + t);
                    Toast.makeText(getActivity(), "error: "+t, Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.e("Error", "onFailure: " + e);
            Toast.makeText(getActivity(), "error: "+e, Toast.LENGTH_SHORT).show();
        }
    }
//    @Override
//    public void getDataCartSuccess(List<Cart> list) {
//        this.cartList = list;
//        if (getContext() != null && getContext() instanceof Activity && !((Activity) getContext()).isFinishing()) {
//            if (list.size() == 0) {
//                if (cartAdapter != null) {
//                    cartAdapter.updateList(list);
//                }
//                if (isToast) {
//                    Toast.makeText(getContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            TextView cartBadgeTextView = requireActivity().findViewById(R.id.cart_badge);
//            cartBadgeTextView.setText(String.valueOf(list.size()));
//        }
//        if (cartAdapter == null) {
//            cartAdapter = new CartAdapter(list, this);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//            linearLayoutManager.setSmoothScrollbarEnabled(true);
//            rcvCart.setLayoutManager(linearLayoutManager);
//
//            rcvCart.setAdapter(cartAdapter);
//            ItemTouchHelper.SimpleCallback simpleCallback = new RecycleViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
//            new ItemTouchHelper(simpleCallback).attachToRecyclerView(rcvCart);
//        } else {
//            cartAdapter.updateList(list);
//        }
//        final int[] p = {0};

//        rcvCart.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                // This method is called when the scroll state changes
//                Log.d(TAG, "onScrollStateChanged: " + newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                // This method is called every time the user scrolls
//                Log.d(TAG, "onScrolled: " + p[0]);
//                p[0]++;
//                if (!recyclerView.canScrollVertically(1)) {
//                    // User has scrolled to the bottom of the list
//                    // Add your code to handle the event here
//                }
//            }
//        });


    // update view

//        double totalPrice = 0;
//        int totalQuantity = 0;
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).getStatus() == 2) {
//                totalPrice += list.get(i).getPrice();
//                totalQuantity += list.get(i).getQuantity();
//            }
//        }
//        final String resultButton = totalQuantity + " Món" + "  Trang thanh toán" + "  " + totalPrice + "đ";
//        progressDialog.dismiss();
//        btnCheckout.setText(resultButton);

//        progressDialog.dismiss();
//    }

//    @Override
//    public void getDataCartFail(String message) {
//        showToast(message);
//    }

    public void updateView(List<Cart> cartList) {
        cartAdapter.notifyDataSetChanged();
    }

//    @Override
//    public void onSwiped(RecyclerView.ViewHolder viewHolder) {

//        if (viewHolder instanceof CartAdapter.ViewHolder) {
//            ArrayList<String> idProduct = cartList.get(viewHolder.getAdapterPosition()).getProductId();
//            String nameProduct = cartList.get(viewHolder.getAdapterPosition()).getTitle();
//
//            Cart cartDelete = cartList.get(viewHolder.getAdapterPosition());
//            int indexDelete = viewHolder.getAdapterPosition();
//            int pos = cartList.get(viewHolder.getAdapterPosition()).getStatus();
//
//            cartAdapter.deleteItem(indexDelete, pos);
//
//            @SuppressLint("ShowToast") Snackbar snackbar = Snackbar.make(layoutCart, "Đã xoá: " + nameProduct, Snackbar.LENGTH_SHORT);
//            snackbar.setAction("Hoàn tác", view -> {
//                cartAdapter.undoItem(cartDelete, indexDelete, pos);
//                if (indexDelete == 0 || indexDelete == cartList.size() - 1) {
//                    rcvCart.scrollToPosition(indexDelete);
//                }
//            });
//
//            snackbar.setActionTextColor(Color.YELLOW);
//            BottomNavigationView navBar = requireActivity().findViewById(R.id.nav_view);
//            navBar.setVisibility(View.GONE);
//            Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
//            snackBarLayout.setPadding(10, 15, 10, 15);
//
//            snackbar.addCallback(new Snackbar.Callback() {
//                @Override
//                public void onDismissed(Snackbar transientBottomBar, int event) {
//                    navBar.setVisibility(View.VISIBLE);
//                }
//
//            });
//            snackbar.show();
//        }
//    }

//    @SuppressLint("UseCompatLoadingForDrawables")
//    private void showFeedbackDialog() {
//        Dialog dialog = new Dialog(requireActivity());
//        dialog.setContentView(R.layout.diallog_feedback_product);
//        Window window = dialog.getWindow();
//        assert window != null;
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.dialog_bg));
//        window.getAttributes().windowAnimations = R.style.DialogAnimation;
//        WindowManager.LayoutParams windowAttributes = window.getAttributes();
//        window.setAttributes(windowAttributes);
//        windowAttributes.gravity = Gravity.BOTTOM;

//        ImageButton btnCancel = dialog.findViewById(R.id.btn_cancel);
//        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
//        btnCancel.setOnClickListener(view2 -> {
//            dialog.cancel();
//        });
//        btnConfirm.setOnClickListener(view2 -> {
//            showToast("updating....");
//        });
//        dialog.show();
//    }
}