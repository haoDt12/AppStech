package com.datn.shopsale.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.datn.shopsale.R;
import com.datn.shopsale.adapter.NotificationAdapter;
import com.datn.shopsale.databinding.FragmentNotificationsBinding;
import com.datn.shopsale.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.rcvNotification.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        customAppbar();
        fillRecycleView();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void customAppbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false); // Hiển thị nút Back
            actionBar.setDisplayShowTitleEnabled(false); // Ẩn tiêu đề mặc định
            actionBar.setDisplayShowCustomEnabled(true); // Cho phép hiển thị AppBar tùy chỉnh
            actionBar.setCustomView(R.layout.custom_appbar_notification); // Thiết lập AppBar tùy chỉnh
            actionBar.getCustomView().findViewById(R.id.img_delete_notification).setOnClickListener(view -> {
                notificationList.clear();
                notificationAdapter.setListNotification(notificationList);
                binding.rcvNotification.setAdapter(notificationAdapter);
                binding.tvNoNotification.setVisibility(View.VISIBLE);
            });
        }
    }

    private void fillRecycleView() {
        notificationList = new ArrayList<>();
        notificationList.add(new Notification("0",
                "0",
                "Sale",
                "https://cdn.icon-icons.com/icons2/943/PNG/512/shoppaymentorderbuy-29_icon-icons.com_73875.png",
                "06/01",
                "Giảm giá sốc"));
        notificationList.add(new Notification("1",
                "0",
                "Sản phẩm mới",
                "https://www.apple.com/vn/iphone-15/images/overview/closer-look/all_colors__d4w03v51nwcy_large.jpg",
                "16/01",
                "Iphone 15 ra mắt"));
        notificationAdapter = new NotificationAdapter(notificationList,getActivity());
        binding.rcvNotification.setAdapter(notificationAdapter);
    }
}