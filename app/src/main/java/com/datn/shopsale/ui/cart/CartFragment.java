package com.datn.shopsale.ui.cart;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.models.ListOder;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.models.ResponseCart;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {
    private List<Cart> cartList;
    private CartAdapter cartAdapter;
    private RecyclerView rcvCart;
    private Button btnCheckout;
    private RelativeLayout layoutCart;
    PreferenceManager preferenceManager ;
    private ApiService apiService;
    private TextView Tvsum;
    private  int tong= 0;
    private CheckBox chk_selectAll;
    private List<Cart> listCartSelected;
    private ListOder listOder;
    private ActivityResultLauncher<Intent> activityResultLauncher;
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
        listCartSelected.clear();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = RetrofitConnection.getApiService();
        initView(view);
        preferenceManager = new PreferenceManager(requireActivity());
        listCartSelected = new ArrayList<>();
        listOder = new ListOder();
        getDataCart();
        onFragmentResult();
        btnCheckout.setOnClickListener(v -> {
            if(listCartSelected.size() != 0){
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("listOder",listOder);
                activityResultLauncher.launch(intent);
            }else {
                Toast.makeText(getActivity(), "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            }
        });

        chk_selectAll.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
               setSelectedItem();
               listCartSelected.clear();
               listCartSelected.addAll(cartList);
               listOder.setList(listCartSelected);
            }else {
                unSelectedItem();
                listCartSelected.clear();
            }
        });
    }

    private void initView(@NonNull View view) {
        layoutCart = view.findViewById(R.id.layout_cart);
        rcvCart = view.findViewById(R.id.rcv_cart);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        Tvsum = view.findViewById(R.id.sum);
        chk_selectAll = view.findViewById(R.id.chk_selectedAll);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setSelectedItem(){
        if(cartList != null){
            for (int i = 0; i < cartList.size(); i++) {
                if (cartList.size()==listOder.getList().size()){
                    chk_selectAll.setChecked(true);
                }
                if( cartList.get(i).getStatus()==1){
                    cartList.get(i).setStatus(2);
                    tong+=  (cartList.get(i).getPrice()*cartList.get(i).getQuantity());
                }

                Tvsum.setText(String.valueOf(tong));
                cartAdapter.notifyDataSetChanged();

            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void unSelectedItem(){
        for (int i = 0; i < cartList.size(); i++) {

            cartList.get(i).setStatus(1);
            tong = 0;
            Tvsum.setText(String.valueOf(tong));
            cartAdapter.notifyDataSetChanged();
        }
    }
    private void getDataCart() {
        LoadingDialog.showProgressDialog(getActivity(),"Đang Tải...");
        cartList = new ArrayList<>();
        Call<ResponseCart> call = apiService.getDataCart(preferenceManager.getString("token"),
                preferenceManager.getString("userId"));
        call.enqueue(new Callback<ResponseCart>() {
            @Override
            public void onResponse(@NonNull Call<ResponseCart> call, @NonNull Response<ResponseCart> response) {
                assert response.body() != null;
                if(response.body().getCode()==1){
                    for (Cart item: response.body().getListCart()) {
                        Cart objCart = new Cart();
                        objCart.setProductId(item.getProductId());
                        objCart.setTitle(item.getTitle());
                        objCart.setQuantity(item.getQuantity());
                        objCart.setImgCover(item.getImgCover());
                        objCart.setPrice(item.getPrice());
                        objCart.setStatus(1);
                        objCart.setUserId(preferenceManager.getString("userId"));
                        objCart.setOption(item.getOption());
                        cartList.add(objCart);
                    }
                    requireActivity().runOnUiThread(() -> {
                        Tvsum.setText(String.valueOf(tong));
                        cartAdapter = new CartAdapter(cartList, getActivity(), new IChangeQuantity() {
                            @Override
                            public void IclickReduce(Cart objCart, int index) {
                                reduceQuantity(objCart,index);

                            }

                            @Override
                            public void IclickIncrease(Cart objCart, int index) {
                                increaseQuantity(objCart,index);

                            }

                            @Override
                            public void IclickCheckBox(Cart objCart, int index) {
                                tong+= (objCart.getPrice()* objCart.getQuantity());
                                Tvsum.setText(String.valueOf(tong));
                                listCartSelected.add(objCart);
                                listOder.setList(listCartSelected);
                            }

                            @Override
                            public void IclickCheckBox2(Cart objCart, int index) {
                                tong-= (objCart.getPrice()* objCart.getQuantity());
                                Tvsum.setText(String.valueOf(tong));
                                listCartSelected.remove(objCart);
                            }
                        });
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
                        rcvCart.setLayoutManager(linearLayoutManager);
                        rcvCart.setAdapter(cartAdapter);
                        LoadingDialog.dismissProgressDialog();
                    });
                }else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        LoadingDialog.dismissProgressDialog();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseCart> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    LoadingDialog.dismissProgressDialog();
                });
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
               @SuppressLint("NotifyDataSetChanged")
               @Override
               public void onResponse(@NonNull Call<ResApi> call, @NonNull Response<ResApi> response) {
                   assert response.body() != null;
                   if(response.body().code==1){
                       if(objCart.getStatus()==2){
                         tong = tong + objCart.getPrice();
                           Tvsum.setText(String.valueOf(tong));
                       }
                       cartList.get(index).setQuantity(cartList.get(index).getQuantity()+1);

                       cartAdapter.notifyDataSetChanged();
                   }else {
                       Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                   }
               }

               @Override
               public void onFailure(@NonNull Call<ResApi> call, @NonNull Throwable t) {
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
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<ResApi> call, @NonNull Response<ResApi> response) {
                    assert response.body() != null;
                    if(response.body().code==1){
                        if(objCart.getStatus()==2){
                            tong = tong - objCart.getPrice();
                            Tvsum.setText(String.valueOf(tong));
                        }
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
                public void onFailure(@NonNull Call<ResApi> call, @NonNull Throwable t) {
                    Log.e("Error", "onFailure: " + t);
                    Toast.makeText(getActivity(), "error: "+t, Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.e("Error", "onFailure: " + e);
            Toast.makeText(getActivity(), "error: "+e, Toast.LENGTH_SHORT).show();
        }
    }
private void onFragmentResult(){
    activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK){
            getDataCart();
        }
    });
}
}