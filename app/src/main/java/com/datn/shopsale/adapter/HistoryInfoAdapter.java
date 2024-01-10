package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.IClickHis;
import com.datn.shopsale.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryInfoAdapter extends RecyclerView.Adapter<HistoryInfoAdapter.HistoryViewHolder>{
    private List<String> list;
    private IClickHis iClickHis;
    @SuppressLint("NotifyDataSetChanged")
    public HistoryInfoAdapter(List<String> list, IClickHis iClickHis) {
        this.list = list;
        this.iClickHis  = iClickHis;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        String item = list.get(position);
        if (item == null){
            return;
        }

        holder.tvInfoHistory.setText(item);
        holder.tvInfoHistory.setOnClickListener(v-> iClickHis.onclick(item));
    }

    @Override
    public int getItemCount() {
        return list == null?0:list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<String> strings) {
        this.list = strings;
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvInfoHistory;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfoHistory = itemView.findViewById(R.id.tv_infoHistory);
        }
    }
}
