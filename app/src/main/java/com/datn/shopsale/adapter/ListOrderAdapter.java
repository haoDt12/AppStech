package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.datn.shopsale.modelsv2.ListDetailOrder;
import com.datn.shopsale.modelsv2.Order;
import com.datn.shopsale.utils.CurrencyUtils;

import java.util.List;

public class ListOrderAdapter extends RecyclerView.Adapter<ListOrderAdapter.ViewHolder> {
    private final List<ListDetailOrder> mList;
    private final Context context;

    @SuppressLint("NotifyDataSetChanged")
    public ListOrderAdapter(List<ListDetailOrder> mList, Context context) {
        this.mList = mList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listorders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListOrderAdapter.ViewHolder holder, int position) {
        ListDetailOrder detailOrder = mList.get(position);
        Order order = detailOrder.getOrder();
        if(detailOrder.getListProduct().size() > 2){
            holder.tvMore.setVisibility(View.VISIBLE);
        }
        if (order != null) {
            holder.tvTotalAmount.setText(String.format("Tổng tiền: %s", CurrencyUtils.formatCurrency(order.getTotal_amount())));
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            GetOrderAdapter adapter = new GetOrderAdapter(detailOrder,context);
            holder.recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recyclerView;
        private final TextView tvTotalAmount;
        private final TextView tvMore;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rcv_order);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvMore = itemView.findViewById(R.id.tv_more);
            recyclerView.setOnTouchListener((v, event) -> true);
        }
    }
}
