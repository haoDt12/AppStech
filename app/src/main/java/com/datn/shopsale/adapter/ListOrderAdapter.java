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
import com.datn.shopsale.models.Orders;


import java.util.ArrayList;
import java.util.List;

public class ListOrderAdapter extends RecyclerView.Adapter<ListOrderAdapter.ViewHolder>{
    private List<Orders> mList ;
    private Context context;

    public ListOrderAdapter(List<Orders> mList,Context context) {
        this.mList = mList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listorders,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListOrderAdapter.ViewHolder holder, int position) {
        Orders order = mList.get(position);
        if (order !=null){
            holder.tvName.setText(order.getTitle());
            holder.tvPrice.setText(order.getPrice()+"");
        }
    }

    @Override
    public int getItemCount() {
        return mList == null?0: mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct;
        private TextView tvName;
        private TextView tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
        }
    }
}
