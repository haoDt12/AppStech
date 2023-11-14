package com.datn.shopsale.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryInfoAdapter extends RecyclerView.Adapter<HistoryInfoAdapter.HistoryViewHolder>{
    private List<String> list;
    private SearchView searchView;

    public HistoryInfoAdapter(List<String> list, SearchView searchView) {
        this.list = list;
        this.searchView = searchView;
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
        holder.tvInfoHistory.setOnClickListener(v->{
            searchView.setQuery(item, false);
        });
    }

    @Override
    public int getItemCount() {
        return list == null?0:list.size();
    }

    public void setData(ArrayList<String> strings) {
        this.list = strings;
        notifyDataSetChanged();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInfoHistory;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfoHistory = (TextView) itemView.findViewById(R.id.tv_infoHistory);
        }
    }
}
