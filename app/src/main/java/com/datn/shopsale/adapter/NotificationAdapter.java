package com.datn.shopsale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.datn.shopsale.models.Notification;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationAdapterViewHoller>{
   private List<Notification> notificationList;
   private Context context;

    public NotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }
    public void setListNotification(List<Notification> notificationList){
        this.notificationList = notificationList;
    }
    @NonNull
    @Override
    public NotificationAdapterViewHoller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false);
        return new NotificationAdapterViewHoller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapterViewHoller holder, int position) {
        Notification notification = notificationList.get(position);
        if(notification!=null){
            Picasso.get().load(notification.getImage()).into(holder.imgNotification);
            holder.tvMessageNotification.setText(notification.getTypeNotification());
            holder.tvMonthNotification.setText(notification.getTime());
            holder.tvTitleNotification.setText(notification.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        if(notificationList != null){
            return notificationList.size();
        }
        return 0;
    }

    public class NotificationAdapterViewHoller extends RecyclerView.ViewHolder {
        private ImageView imgNotification;
        private TextView tvTitleNotification;
        private TextView tvMessageNotification;
        private TextView tvMonthNotification;
        public NotificationAdapterViewHoller(@NonNull View itemView) {
            super(itemView);
            imgNotification = (ImageView) itemView.findViewById(R.id.img_notification);
            tvTitleNotification = (TextView) itemView.findViewById(R.id.tv_title_notification);
            tvMessageNotification = (TextView) itemView.findViewById(R.id.tv_message_notification);
            tvMonthNotification = (TextView) itemView.findViewById(R.id.tv_month_notification);

        }
    }
}
