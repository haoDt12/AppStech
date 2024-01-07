package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.ShowDetailOrderActivity;
import com.datn.shopsale.modelsv2.ListDetailOrder;
import com.datn.shopsale.modelsv2.Product;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.GetImgIPAddress;

public class GetOrderAdapter extends RecyclerView.Adapter<GetOrderAdapter.GetOrderViewHolder> {
    private final Context context;
    private final ListDetailOrder listDetailOrder;

    public GetOrderAdapter(ListDetailOrder listDetailOrder, Context context) {
        this.listDetailOrder = listDetailOrder;
        this.context = context;
    }

    @NonNull
    @Override
    public GetOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_order, parent, false);
        return new GetOrderViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull GetOrderViewHolder holder, int position) {
        Product products = listDetailOrder.getListProduct().get(position);
        if (products != null) {
            holder.tvName.setText(String.format("Tên sản phẩm: %s", products.getName()));
            holder.tvPrice.setText(String.format("Giá: %s", CurrencyUtils.formatCurrency(products.getPrice())));
            Glide.with(context).load(GetImgIPAddress.convertLocalhostToIpAddress(products.getImg_cover())).into(holder.imgProduct);
            holder.tvQuantity.setText(String.format("Số lượng: %s", products.getQuantity()));
            if (position == 0) {
                if (listDetailOrder.getOrder().getStatus().equals("WaitConfirm")) {
                    holder.lnItemOrder.setVisibility(View.VISIBLE);
                    holder.tvStatusOrder.setText(String.format("%s(%d)", context.getResources().getText(R.string.doi_xac_nhan), listDetailOrder.getListProduct().size()));
                }
                if (listDetailOrder.getOrder().getStatus().equals("WaitingGet")) {
                    holder.lnItemOrder.setVisibility(View.VISIBLE);
                    holder.tvStatusOrder.setText(String.format("%s(%d)", context.getResources().getText(R.string.cho_lay_hang), listDetailOrder.getListProduct().size()));
                }
                if (listDetailOrder.getOrder().getStatus().equals("InTransit")) {
                    holder.lnItemOrder.setVisibility(View.VISIBLE);
                    holder.tvStatusOrder.setText(String.format("%s(%d)", context.getResources().getText(R.string.dang_giao_hang), listDetailOrder.getListProduct().size()));
                }
                if (listDetailOrder.getOrder().getStatus().equals("PayComplete")) {
                    holder.lnItemOrder.setVisibility(View.VISIBLE);
                    holder.tvStatusOrder.setText(String.format("%s(%d)", context.getResources().getText(R.string.da_thanh_toan), listDetailOrder.getListProduct().size()));
                }
                if (listDetailOrder.getOrder().getStatus().equals("Cancel")) {
                    holder.lnItemOrder.setVisibility(View.VISIBLE);
                    holder.tvStatusOrder.setText(String.format("%s(%d)", context.getResources().getText(R.string.da_huy), listDetailOrder.getListProduct().size()));
                }
            }
            holder.lnIdItemOrder.setOnClickListener(v -> {
                Intent i = new Intent(context, ShowDetailOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("detail_order", listDetailOrder);
                i.putExtras(bundle);
                context.startActivity(i);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (listDetailOrder != null && listDetailOrder.getListProduct() != null) {
            return Math.min(listDetailOrder.getListProduct().size(), 2);
        } else {
            return 0;
        }
    }

    public static class GetOrderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProduct;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvStatusOrder;
        private final TextView tvQuantity;
        private final LinearLayout lnItemOrder;
        private final LinearLayout lnIdItemOrder;

        public GetOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatusOrder = itemView.findViewById(R.id.tv_status_order);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            lnItemOrder = itemView.findViewById(R.id.ln_item_order);
            lnIdItemOrder = itemView.findViewById(R.id.id_item_order);
        }
    }
}
