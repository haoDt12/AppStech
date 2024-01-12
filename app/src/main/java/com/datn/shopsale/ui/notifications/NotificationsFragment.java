package com.datn.shopsale.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.datn.shopsale.modelsv2.DataNotification;
import com.datn.shopsale.modelsv2.Notification;
import com.datn.shopsale.responsev2.GetNotificationResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {
    private NotificationAdapter notificationAdapter;
    private final List<Notification> notificationList = new ArrayList<>();

    private FragmentNotificationsBinding binding;
    private PreferenceManager preferenceManager;
    private ApiService apiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.rcvNotification.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        customAppbar();
        preferenceManager = new PreferenceManager(requireActivity());
        apiService = RetrofitConnection.getApiService();
        GetNotification();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void customAppbar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.custom_appbar_notification);
            actionBar.getCustomView().findViewById(R.id.img_delete_notification).setOnClickListener(view -> {
                notificationList.clear();
                notificationAdapter.setListNotification(notificationList);
                binding.rcvNotification.setAdapter(notificationAdapter);
                binding.tvNoNotification.setVisibility(View.VISIBLE);
            });
        }
    }

    private void GetNotification() {
        LoadingDialog.showProgressDialog(requireActivity(), "Loading...");
        notificationList.clear();
        Call<GetNotificationResponse> call = apiService.getNotificationByUser(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetNotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetNotificationResponse> call, @NonNull Response<GetNotificationResponse> response) {
                requireActivity().runOnUiThread(LoadingDialog::dismissProgressDialog);
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    NotificationCount.count = response.body().getData().size();
                    for (DataNotification item : response.body().getData()) {
                        Notification notification = item.getNotification_id();
                        notificationList.add(notification);
                    }
                    updateAdapter();
                } else {
                    if (response.body().getMessage().equals("wrong token")) {
                        CheckLoginUtil.gotoLogin(requireActivity(), response.body().getMessage());
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<GetNotificationResponse> call, @NonNull Throwable t) {
                AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());
            }
        });
    }

    private void updateAdapter() {
        NotificationCount.count = notificationList.size();
        requireActivity().runOnUiThread(() -> {
            binding.rcvNotification.setLayoutManager(new LinearLayoutManager(getActivity()));
            notificationAdapter = new NotificationAdapter(notificationList, getActivity());
            binding.rcvNotification.setAdapter(notificationAdapter);
        });
    }

}