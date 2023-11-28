package com.datn.shopsale.ui.dashboard.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ListOrderAdapter;
import com.datn.shopsale.databinding.FragmentCancelOrderBinding;
import com.datn.shopsale.databinding.FragmentPayCompleteBinding;
import com.datn.shopsale.models.Orders;
import com.datn.shopsale.response.GetListOrderResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
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
        CancelOrderFragment fragment = new CancelOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCancelOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(getActivity());
        apiService = RetrofitConnection.getApiService();
        LoadingDialog.showProgressDialog(getActivity(),"Loading...");
        getListOrdeCancel();
    }
    private void getListOrdeCancel() {
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
                        if (item.getStatus().equals("Cancel")){
                            dataOrderInTransit.add(item);
                        }
                    }
                    if (getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.rcvCancel.setLayoutManager(new LinearLayoutManager(getActivity()));
                                adapter = new ListOrderAdapter(dataOrderInTransit, getActivity());
                                binding.rcvCancel.setAdapter(adapter);
                                LoadingDialog.dismissProgressDialog();
                            }
                        });
                    }
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