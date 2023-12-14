package com.datn.shopsale.ui.dashboard.order;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.datn.shopsale.utils.AlertDialogUtil;
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
        return new WaitConfirmFragment();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWaitConfirmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(requireActivity());
        apiService = RetrofitConnection.getApiService();
        getListOrderWaitConfirm();
    }

    private void getListOrderWaitConfirm() {
        LoadingDialog.showProgressDialog(requireActivity(),"Loading...");
        String token = preferenceManager.getString("token");
        String userId = preferenceManager.getString("userId");
        ArrayList<Orders> dataOrder = new ArrayList<>();
        ArrayList<Orders> dataOrderInTransit = new ArrayList<>();
        Call<GetListOrderResponse.Root> call = apiService.getOrderByUserId(token, userId);
        call.enqueue(new Callback<GetListOrderResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetListOrderResponse.Root> call, @NonNull Response<GetListOrderResponse.Root> response) {
                assert response.body() != null;
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    if (response.body().code == 1) {
                        ArrayList<GetListOrderResponse.ListOrder> listOrder = response.body().listOrder;

                        for (GetListOrderResponse.ListOrder order : response.body().listOrder) {
                            Log.d("hhhhhhhh", "onResponse: " + listOrder);
                            dataOrder.add(new Orders(order._id, order.userId, order.product, order.status, order.addressId, order.total));
                        }
                        for (Orders item : dataOrder) {
                            if (item.getStatus().equals("WaitConfirm")) {
                                dataOrderInTransit.add(item);
                            }
                        }

                        binding.rcvWaitConfirm.setLayoutManager(new LinearLayoutManager(requireActivity()));
                        adapter = new ListOrderAdapter(dataOrderInTransit, requireActivity());
                        binding.rcvWaitConfirm.setAdapter(adapter);
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().message);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GetListOrderResponse.Root> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getListOrderWaitConfirm();
    }
}