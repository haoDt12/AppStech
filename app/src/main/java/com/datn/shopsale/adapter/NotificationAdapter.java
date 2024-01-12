package com.datn.shopsale.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.ShowDetailOrderActivity;
import com.datn.shopsale.modelsv2.ListDetailOrder;
import com.datn.shopsale.modelsv2.Notification;
import com.datn.shopsale.modelsv2.Voucher;
import com.datn.shopsale.request.GetOrderByIdRequest;
import com.datn.shopsale.request.GetVoucherByIdRequest;
import com.datn.shopsale.responsev2.GetOrderResponseV2;
import com.datn.shopsale.responsev2.GetVoucherByIdResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.DateUtils;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationAdapterViewHoller> {
    private List<Notification> notificationList;

    private final Context context;

    public NotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    public void setListNotification(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationAdapterViewHoller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationAdapterViewHoller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapterViewHoller holder, int position) {
        Notification notification = notificationList.get(position);
        holder.tvMessageNotification.setSelected(true);
        holder.tvMessageNotification.setSingleLine(true);
        if (notification != null) {
            Glide.with(context).load(GetImgIPAddress.convertLocalhostToIpAddress(notification.getImg())).into(holder.imgNotification);
            holder.tvMessageNotification.setText(notification.getContent());
            holder.tvTitleNotification.setText(notification.getTitle());
            String formattedDate = DateUtils.formatDate(notification.getCreate_time());
            holder.tvMonthNotification.setText(formattedDate);
            holder.item_notification.setOnClickListener(v -> {
                boolean isNotification = false;
                String[] txtSplitOrder = notification.getContent().split(context.getResources().getString(R.string.voi_ma_don_hang));
                String[] txtSplitVoucher = notification.getContent().split(context.getResources().getString(R.string.voi_ma_giam_gia));
                if (txtSplitOrder.length > 1) {
                    isNotification = true;
                    getListOrderWaitConfirm(txtSplitOrder[1].trim());
                }
                if (txtSplitVoucher.length > 1) {
                    isNotification = true;
                    getVoucherById(txtSplitVoucher[1].trim());
                }
                if (!isNotification) {
                   showDetailNotification(notification);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (notificationList != null) {
            return notificationList.size();
        }
        return 0;
    }

    public static class NotificationAdapterViewHoller extends RecyclerView.ViewHolder {
        private final ImageView imgNotification;
        private final TextView tvTitleNotification;
        private final TextView tvMessageNotification;
        private final TextView tvMonthNotification;
        private final LinearLayout item_notification;

        public NotificationAdapterViewHoller(@NonNull View itemView) {
            super(itemView);
            imgNotification = (ImageView) itemView.findViewById(R.id.img_notification);
            tvTitleNotification = (TextView) itemView.findViewById(R.id.tv_title_notification);
            tvMessageNotification = (TextView) itemView.findViewById(R.id.tv_message_notification);
            tvMonthNotification = (TextView) itemView.findViewById(R.id.tv_month_notification);
            item_notification = (LinearLayout) itemView.findViewById(R.id.item_notification);
        }
    }

    private void getListOrderWaitConfirm(String orderId) {
        ApiService apiService = RetrofitConnection.getApiService();
        PreferenceManager preferenceManager = new PreferenceManager(context);
        LoadingDialog.showProgressDialog(((Activity) context), "Loading...");
        GetOrderByIdRequest request = new GetOrderByIdRequest();
        request.setOrderId(orderId);
        String token = preferenceManager.getString("token");
        Call<GetOrderResponseV2> call = apiService.getOrderByOrderId(token, request);
        call.enqueue(new Callback<GetOrderResponseV2>() {
            @Override
            public void onResponse(@NonNull Call<GetOrderResponseV2> call, @NonNull Response<GetOrderResponseV2> response) {
                assert response.body() != null;
                ((Activity) context).runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    if (response.body().getCode() == 1) {
                        ListDetailOrder listOrderDetail = response.body().getListDetailOrder().get(0);
                        Intent i = new Intent(context, ShowDetailOrderActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("detail_order", listOrderDetail);
                        i.putExtras(bundle);
                        context.startActivity(i);
                    } else {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(context, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(context, response.body().getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GetOrderResponseV2> call, @NonNull Throwable t) {
                ((Activity) context).runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(context, t.getMessage());
                });
            }
        });
    }

    private void getVoucherById(String voucherId) {
        LoadingDialog.showProgressDialog(context, "Loading...");
        ApiService apiService = RetrofitConnection.getApiService();
        PreferenceManager preferenceManager = new PreferenceManager(context);
        GetVoucherByIdRequest request = new GetVoucherByIdRequest();
        request.setVoucherId(voucherId);
        Call<GetVoucherByIdResponse> call = apiService.getVoucherByVoucherId(preferenceManager.getString("token"), request);
        call.enqueue(new Callback<GetVoucherByIdResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetVoucherByIdResponse> call, @NonNull Response<GetVoucherByIdResponse> response) {
                ((Activity) context).runOnUiThread(LoadingDialog::dismissProgressDialog);
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    Voucher voucher = response.body().getVoucher();
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View customView = inflater.inflate(R.layout.item_voucher, null);
                    TextView tvTitle = customView.findViewById(R.id.tv_title);
                    TextView tvContent = customView.findViewById(R.id.tv_content);
                    TextView tvDate = customView.findViewById(R.id.tv_date);
                    Button btnUse = customView.findViewById(R.id.btn_use);
                    tvTitle.setText(voucher.getName());
                    tvContent.setText(voucher.getContent());
                    tvDate.setText(String.format("Từ %s đến %s", voucher.getFromDate(), voucher.getToDate()));
                    btnUse.setVisibility(View.GONE);
                    new AlertDialog.Builder(context)
                            .setTitle("Voucher detail")
                            .setView(customView)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    if (response.body().getMessage().equals("wrong token")) {
                        CheckLoginUtil.gotoLogin(context, response.body().getMessage());
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(context, response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetVoucherByIdResponse> call, @NonNull Throwable t) {
                ((Activity) context).runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(context, t.getMessage());
                });
            }
        });
    }

    private void showDetailNotification(Notification notification) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.item_voucher, null);
        CardView cardItemNotification = customView.findViewById(R.id.card_item_notification);
        ImageView imgNotification = customView.findViewById(R.id.img_notification);
        TextView tvTitle = customView.findViewById(R.id.tv_title);
        TextView tvContent = customView.findViewById(R.id.tv_content);
        TextView tvDate = customView.findViewById(R.id.tv_date);
        Button btnUse = customView.findViewById(R.id.btn_use);
        tvTitle.setText(notification.getTitle());
        tvContent.setText(notification.getContent());
        tvDate.setText(String.format("Ngày %s", notification.getCreate_time()));
        Glide.with(context).load(notification.getImg()).into(imgNotification);
        btnUse.setVisibility(View.GONE);
        new AlertDialog.Builder(context)
                .setTitle("Thông báo khuyến mại")
                .setView(customView)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
