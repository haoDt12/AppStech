package com.datn.shopsale.ui.dashboard.order;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.adapter.ListOrderAdapter;
import com.datn.shopsale.databinding.FragmentWaitConfirmBinding;
import com.datn.shopsale.models.Orders;
import com.datn.shopsale.response.GetListOrderResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.LoadingDialog;
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
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(getActivity());
        apiService = RetrofitConnection.getApiService();
        LoadingDialog.showProgressDialog(getActivity(),"Loading...");
        getListOrderWaitConfirm();
    }

    private void getListOrderWaitConfirm() {
        String token = preferenceManager.getString("token");
        String userId = preferenceManager.getString("userId");
        ArrayList<Orders> dataOrder = new ArrayList<>();
        ArrayList<Orders> dataOrderInTransit = new ArrayList<>();

        Call<GetListOrderResponse.Root> call = apiService.getOrderByUserId(token, userId);
        call.enqueue(new Callback<GetListOrderResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetListOrderResponse.Root> call, @NonNull Response<GetListOrderResponse.Root> response) {
                assert response.body() != null;
                if (response.body().code == 1) {
                    ArrayList<GetListOrderResponse.ListOrder> listOrder = response.body().listOrder;

                    for (GetListOrderResponse.ListOrder order : response.body().listOrder) {
                        LoadingDialog.dismissProgressDialog();
                        Log.d("hhhhhhhh", "onResponse: "+listOrder);
                        dataOrder.add(new Orders(order._id, order.userId, order.product, order.status, order.addressId, order.total));
                    }
                    for (Orders item: dataOrder) {
                        if (item.getStatus().equals("WaitConfirm")){
                            dataOrderInTransit.add(item);
                        }
                    }

                    if (getActivity() != null){
                        getActivity().runOnUiThread(() -> {
                            binding.rcvWaitConfirm.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new ListOrderAdapter(dataOrderInTransit, getActivity());
                            binding.rcvWaitConfirm.setAdapter(adapter);
                            LoadingDialog.dismissProgressDialog();
                        });}
                } else {
                    requireActivity().runOnUiThread(LoadingDialog::dismissProgressDialog);
                    Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetListOrderResponse.Root> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(LoadingDialog::dismissProgressDialog);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getListOrderWaitConfirm();
    }
}