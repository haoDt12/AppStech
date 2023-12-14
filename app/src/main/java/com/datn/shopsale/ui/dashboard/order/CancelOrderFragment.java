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
import com.datn.shopsale.databinding.FragmentCancelOrderBinding;
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

public class CancelOrderFragment extends Fragment {
    private ListOrderAdapter adapter;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private FragmentCancelOrderBinding binding;

    public CancelOrderFragment() {
        // Required empty public constructor
    }

    public static CancelOrderFragment newInstance() {
        return new CancelOrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCancelOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(requireActivity());
        apiService = RetrofitConnection.getApiService();
        getListOrdeCancel();
    }

    private void getListOrdeCancel() {
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
                        for (GetListOrderResponse.ListOrder order : response.body().listOrder) {
                            Log.d("zzz", "onResponse: " + order.total);
                            Log.d("hhhhhhhh", "onResponse: " + response.body().listOrder);
                            dataOrder.add(new Orders(order._id, order.userId, order.product, order.status, order.addressId, order.total));
                        }
                        for (Orders item : dataOrder) {
                            if (item.getStatus().equals("Cancel")) {
                                dataOrderInTransit.add(item);
                            }
                        }
                        binding.rcvCancel.setLayoutManager(new LinearLayoutManager(getActivity()));
                        adapter = new ListOrderAdapter(dataOrderInTransit, getActivity());
                        binding.rcvCancel.setAdapter(adapter);
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
        getListOrdeCancel();
    }
}