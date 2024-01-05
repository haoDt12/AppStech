package com.datn.shopsale.ui.dashboard.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.adapter.ListOrderAdapter;
import com.datn.shopsale.databinding.FragmentWaitngGetOrderBinding;
import com.datn.shopsale.modelsv2.ListDetailOrder;
import com.datn.shopsale.request.GetOrderByStatusRequest;
import com.datn.shopsale.responsev2.GetOrderResponseV2;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitngGetOrderFragment extends Fragment {

    private ListOrderAdapter adapter;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private FragmentWaitngGetOrderBinding binding;

    public WaitngGetOrderFragment() {
        // Required empty public constructor
    }

    public static WaitngGetOrderFragment newInstance() {
        return new WaitngGetOrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWaitngGetOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(requireActivity());
        apiService = RetrofitConnection.getApiService();
        getListOrdeWatingGet();
    }

    private void getListOrdeWatingGet() {
        LoadingDialog.showProgressDialog(requireActivity(),"Loading...");
        GetOrderByStatusRequest request = new GetOrderByStatusRequest();
        request.setStatus("WaitingGet");
        String token = preferenceManager.getString("token");
        Call<GetOrderResponseV2> call = apiService.getOrderByStatus(token, request);
        call.enqueue(new Callback<GetOrderResponseV2>() {
            @Override
            public void onResponse(@NonNull Call<GetOrderResponseV2> call, @NonNull Response<GetOrderResponseV2> response) {
                assert response.body() != null;
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    if (response.body().getCode() == 1) {
                        List<ListDetailOrder> listOrder = response.body().getListDetailOrder();
                        binding.rcvWaitingGet.setLayoutManager(new LinearLayoutManager(requireActivity()));
                        adapter = new ListOrderAdapter(listOrder, requireActivity());
                        binding.rcvWaitingGet.setAdapter(adapter);
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GetOrderResponseV2> call, @NonNull Throwable t) {
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
        getListOrdeWatingGet();
    }
}