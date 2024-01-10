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
import com.datn.shopsale.databinding.FragmentInTransitOrderBinding;
import com.datn.shopsale.modelsv2.ListDetailOrder;
import com.datn.shopsale.request.GetOrderByStatusRequest;
import com.datn.shopsale.responsev2.GetOrderResponseV2;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InTransitOrderFragment extends Fragment {
    private FragmentInTransitOrderBinding binding;
    private PreferenceManager preferenceManager;

    private ApiService apiService;
    private ListOrderAdapter adapter;

    public InTransitOrderFragment() {
        // Required empty public constructor
    }

    public static InTransitOrderFragment newInstance() {
        return new InTransitOrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInTransitOrderBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(requireActivity());
        apiService = RetrofitConnection.getApiService();
        getListOrderInTransit();
    }

    private void getListOrderInTransit() {
        LoadingDialog.showProgressDialog(requireActivity(),"Loading...");
        GetOrderByStatusRequest request = new GetOrderByStatusRequest();
        request.setStatus("InTransit");
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
                        binding.rcvInTransit.setLayoutManager(new LinearLayoutManager(requireActivity()));
                        adapter = new ListOrderAdapter(listOrder, requireActivity());
                        binding.rcvInTransit.setAdapter(adapter);
                    } else {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(requireActivity(), response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                        }
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
        getListOrderInTransit();
    }
}