package com.datn.shopsale.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.adapter.NotificationAdapter;
import com.datn.shopsale.databinding.FragmentNotificationsBinding;
import com.datn.shopsale.models.Notification;
import com.datn.shopsale.response.GetNotificationResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {
    private NotificationAdapter notificationAdapter;
    //private NotifiAdapter adapter;
    private List<Notification> notificationList = new ArrayList<>();

    private FragmentNotificationsBinding binding;
    private PreferenceManager preferenceManager;
    private ApiService apiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.rcvNotification.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        customAppbar();
        preferenceManager = new PreferenceManager(getActivity());
        apiService = RetrofitConnection.getApiService();
        GetNotification();
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

    private void GetNotification() {
        notificationList.clear();
        String idUser = preferenceManager.getString("userId");
        Call<GetNotificationResponse.Root> call = apiService.getNotification(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetNotificationResponse.Root>() {
            @Override
            public void onResponse(Call<GetNotificationResponse.Root> call, Response<GetNotificationResponse.Root> response) {
                if (response.body().code == 1) {
                    NotificationCount.count = response.body().notification.size();
//                    Toast.makeText(getActivity(), "" + NotificationCount.count , Toast.LENGTH_SHORT).show();
                    for (GetNotificationResponse.Notification item : response.body().notification) {
                        Notification notification = new Notification();
                        notification.set_id(item.get_id());
                        notification.setTitle(item.getTitle());
                        notification.setContent(item.getContent());
                        notification.setDate(item.getDate());
                        notification.setTypePrivatePublic("Public");
                        notificationList.add(notification);
                    }
                    updateAdapter();
                } else {
                    Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GetNotificationResponse.Root> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //private
        Call<GetNotificationResponse.Root> callPrivate = apiService.getNotificationPrivate(preferenceManager.getString("token"), idUser);
        callPrivate.enqueue(new Callback<GetNotificationResponse.Root>() {
            @Override
            public void onResponse(Call<GetNotificationResponse.Root> call, Response<GetNotificationResponse.Root> response) {
                if (response.body().code == 1) {
                    for (GetNotificationResponse.Notification item : response.body().notification) {
                        notificationList.add(new Notification(item.get_id(), item.getTitle(), item.getDate(), item.getContent(), item.getImg(), item.getUserId(), "Private"));
                    }
                    updateAdapter();
                } else {
                    Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetNotificationResponse.Root> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void updateAdapter() {
        NotificationCount.count = notificationList.size();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.rcvNotification.setLayoutManager(new LinearLayoutManager(getActivity()));
                notificationAdapter = new NotificationAdapter(notificationList, getActivity());
                binding.rcvNotification.setAdapter(notificationAdapter);
            }
        });
    }

}