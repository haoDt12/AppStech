package com.datn.shopsale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.datn.shopsale.R;
import com.datn.shopsale.models.Address;
import com.datn.shopsale.models.Voucher;

import java.util.ArrayList;
import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private List<Voucher> list;
    private Context context;

    public VoucherAdapter(List<Voucher> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public VoucherAdapter.VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher,parent,false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherAdapter.VoucherViewHolder holder, int position) {
        Voucher voucher = list.get(position);
        if(voucher == null){
            return;
        }
        holder.tvTitle.setText(voucher.getTitle());
        holder.tvContent.setText(voucher.getTitle()+"-"+voucher.getSale());
        holder.tvDate.setText(voucher.getDate());
    }

    @Override
    public int getItemCount() {
        return list==null?0: list.size();
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout layoutSwipe;
        private LinearLayout dragLayout;
        private LinearLayout mainLayout;
        private LinearLayout lnlLeft;
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvDate;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutSwipe = (SwipeLayout) itemView.findViewById(R.id.layout_swipe);
            dragLayout = (LinearLayout) itemView.findViewById(R.id.dragLayout);
            mainLayout = (LinearLayout) itemView.findViewById(R.id.mainLayout);
            lnlLeft = (LinearLayout) itemView.findViewById(R.id.lnl_left);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
