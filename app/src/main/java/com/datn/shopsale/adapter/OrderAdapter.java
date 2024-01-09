package com.datn.shopsale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.GetImgIPAddress;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private final List<com.datn.shopsale.modelsv2.Product> mList;
    private final Context context;
    public OrderAdapter(List<com.datn.shopsale.modelsv2.Product> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        int price = 0;
        price = Integer.parseInt(mList.get(position).getPrice());
        holder.tvName.setText(mList.get(position).getName());
        holder.tvPrice.setText(CurrencyUtils.formatCurrency(String.valueOf(price)));
        Glide.with(context).load(GetImgIPAddress.convertLocalhostToIpAddress(mList.get(position).getImg_cover())).into(holder.imgProduct);
        holder.tvQuantity.setText(mList.get(position).getQuantity());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvPrice;

        private final ImageView imgProduct;
        private final TextView tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            TextView tvColor = (TextView) itemView.findViewById(R.id.tv_color);
            tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);
        }
    }
}
