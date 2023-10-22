package com.datn.shopsale.ui.dashboard.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ListOrderAdapter;
import com.datn.shopsale.adapter.NotificationAdapter;
import com.datn.shopsale.adapter.ProductAdapter;
import com.datn.shopsale.databinding.FragmentHomeBinding;
import com.datn.shopsale.databinding.FragmentNotificationsBinding;
import com.datn.shopsale.databinding.FragmentWaitConfirmBinding;
import com.datn.shopsale.models.Notification;
import com.datn.shopsale.models.Orders;
import com.datn.shopsale.models.Product;

import java.util.ArrayList;
import java.util.List;

public class WaitConfirmFragment extends Fragment {
    private ListOrderAdapter adapter;
    private List<Orders> mList;

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
        binding.rcvList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        fillRecycleView();
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }

    private void fillRecycleView() {
        mList = new ArrayList<>();
        mList.add(new Orders("0",
                "0",
                null,
                "Iphone",
                "Chua xac nhan",
                "https://cdn-icons-png.flaticon.com/512/3239/3239958.png",
                "HaNoi",
                1,
                10000000,
                12000000,
                null));

        mList.add(new Orders("0",
                "0",
                null,
                "Iphone",
                "Chua xac nhan",
                "https://cdn-icons-png.flaticon.com/512/3239/3239958.png",
                "HaNoi",
                1,
                10000000,
                12000000,
                null));

        adapter = new ListOrderAdapter(mList,getActivity());
        binding.rcvList.setAdapter(adapter);
    }
}