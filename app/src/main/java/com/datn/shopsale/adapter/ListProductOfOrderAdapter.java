package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.AddReviewActivity;
import com.datn.shopsale.modelsv2.Product;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.GetImgIPAddress;

import java.util.List;

public class ListProductOfOrderAdapter extends RecyclerView.Adapter<ListProductOfOrderAdapter.ViewHolder> {

    private final List<Product> dataProduct;
    private final Context context;
    private final String status;
    private boolean check = true;

    @SuppressLint("NotifyDataSetChanged")
    public ListProductOfOrderAdapter(List<Product> dataProduct, Context context, String status) {
        this.dataProduct = dataProduct;
        this.context = context;
        this.status = status;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_productoforder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product pro = dataProduct.get(position);
        if (pro == null) {
            return;
        }
        holder.tvTitleProductOfOrder.setText(String.format("Tên sản phẩm: %s", pro.getName()));
        holder.tvPriceProductOfOrder.setText(String.format("Giá: %s", CurrencyUtils.formatCurrency(pro.getPrice())));
        Glide.with(context).load(GetImgIPAddress.convertLocalhostToIpAddress(pro.getImg_cover())).into(holder.imgProduct);
        if (status.equals("PayComplete")) {
            if (check) {
                holder.btn_danhgia.setVisibility(View.VISIBLE);
            } else {
                holder.btn_danhgia.setVisibility(View.GONE);
            }
        }
        holder.tvColorProductOfOrder.setText(String.format("Màu: %s", pro.getColor()));
        if(pro.getRom() != null){
            holder.tvRamRomProductOfOrder.setText(String.format("Rom: %s", pro.getRom()));
            holder.tvRamRomProductOfOrder.setVisibility(View.VISIBLE);
        }
        if(pro.getRam() != null){
            holder.tvRamRamProductOfOrder.setText(String.format("Ram: %s", pro.getRam()));
            holder.tvRamRamProductOfOrder.setVisibility(View.VISIBLE);
        }
        holder.tvNumProductOfOrder.setText(String.format("Số lượng: %s", pro.getQuantity()));
        holder.btn_danhgia.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddReviewActivity.class);
            intent.putExtra("id", pro.get_id());
            intent.putExtra("image", pro.getImg_cover());
            intent.putExtra("name", pro.getName());
            intent.putExtra("price", pro.getPrice());
            intent.putExtra("color", pro.getColor());
            intent.putExtra("ram", pro.getRam());
            intent.putExtra("rom", pro.getRom());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            check = false;
        });
    }

    @Override
    public int getItemCount() {
        return dataProduct == null ? 0 : dataProduct.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitleProductOfOrder;
        private final TextView tvPriceProductOfOrder;
        private final TextView tvColorProductOfOrder;
        private final TextView tvNumProductOfOrder;
        private final TextView tvRamRomProductOfOrder;
        private final TextView tvRamRamProductOfOrder;
        private final ImageView imgProduct;
        private final Button btn_danhgia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            tvTitleProductOfOrder = (TextView) itemView.findViewById(R.id.tv_titleProductOfOrder);
            tvPriceProductOfOrder = (TextView) itemView.findViewById(R.id.tv_priceProductOfOrder);
            tvColorProductOfOrder = (TextView) itemView.findViewById(R.id.tv_colorProductOfOrder);
            tvNumProductOfOrder = (TextView) itemView.findViewById(R.id.tv_numProductOfOrder);
            tvRamRomProductOfOrder = (TextView) itemView.findViewById(R.id.tv_ramRomProductOfOrder);
            tvRamRamProductOfOrder = (TextView) itemView.findViewById(R.id.tv_ramRamProductOfOrder);
            btn_danhgia = itemView.findViewById(R.id.btn_danhgia);
        }
    }
}
