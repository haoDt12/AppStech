package com.datn.shopsale.ui.dashboard.order;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.adapter.ListOrderAdapter;
import com.datn.shopsale.databinding.FragmentWaitConfirmBinding;
import com.datn.shopsale.models.Orders;
import com.datn.shopsale.response.GetListOrderResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitConfirmFragment extends Fragment {
    private ListOrderAdapter adapter;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private FragmentWaitConfirmBinding binding;
    public WaitConfirmFragment() {
        // Required empty public constructor
    }

    public static WaitConfirmFragment newInstance() {
        WaitConfirmFragment fragment = new WaitConfirmFragment();
        return fragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWaitConfirmBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        preferenceManager = new PreferenceManager(getActivity());
        apiService = RetrofitConnection.getApiService();
        getListOrderWaitConfirm();
        return root;
    }
    private void getListOrderWaitConfirm() {
        String token = preferenceManager.getString("token");
        String userId = preferenceManager.getString("userId");
        ArrayList<Orders> dataOrder = new ArrayList<>();
        ArrayList<Orders> dataOrderInTransit = new ArrayList<>();

        Call<GetListOrderResponse.Root> call = apiService.getOrderByUserId(token, userId);
        call.enqueue(new Callback<GetListOrderResponse.Root>() {
            @Override
            public void onResponse(Call<GetListOrderResponse.Root> call, Response<GetListOrderResponse.Root> response) {
                if (response.body().code == 1) {
                    for (GetListOrderResponse.ListOrder order : response.body().listOrder) {
                        Log.d("hhhhhhhh", "onResponse: "+response.body().listOrder);
                        dataOrder.add(new Orders(order._id, order.userId, order.product, order.status, order.addressId, order.total));
                    }
                    for (Orders item: dataOrder) {
                        if (item.getStatus().equals("WaitConfirm")){
                            dataOrderInTransit.add(item);
                        }
                    }

                    if (getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.rcvWaitConfirm.setLayoutManager(new LinearLayoutManager(getActivity()));
                                adapter = new ListOrderAdapter(dataOrderInTransit, getActivity());
                                binding.rcvWaitConfirm.setAdapter(adapter);
                            }
                        });}
                } else {
                    Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetListOrderResponse.Root> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}