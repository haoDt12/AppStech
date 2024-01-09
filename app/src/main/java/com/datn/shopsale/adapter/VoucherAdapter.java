package com.datn.shopsale.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.datn.shopsale.modelsv2.MapVoucherCus;

import java.io.Serializable;
import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private List<MapVoucherCus> list;
    private Context context;
    private int actionCode;

    public VoucherAdapter(List<MapVoucherCus> list, Context context, int actionCode) {
        this.list = list;
        this.context = context;
        this.actionCode = actionCode;
    }

    @NonNull
    @Override
    public VoucherAdapter.VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherAdapter.VoucherViewHolder holder, int position) {
        MapVoucherCus voucher = list.get(position);
        if (voucher == null) {
            return;
        }
        if (actionCode == 1) {
            holder.btnUse.setVisibility(View.GONE);
        }
        holder.tvTitle.setText(voucher.getVocher_id().getName());
        holder.tvContent.setText(voucher.getVocher_id().getContent());
        holder.tvDate.setText(String.format("Từ %s đến %s", voucher.getVocher_id().getFromDate(), voucher.getVocher_id().getToDate()));
        holder.btnUse.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderAdapter.class);
            intent.putExtra("voucher", (Serializable) voucher);
            ((Activity) context).setResult(Activity.RESULT_OK, intent);
            ((Activity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvDate;
        private final Button btnUse;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            btnUse = (Button) itemView.findViewById(R.id.btn_use);
        }
    }
}
