package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.DetailProductActivity;
import com.datn.shopsale.modelsv2.Product;
import com.datn.shopsale.utils.CurrencyUtils;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Product> dataList;
    private final Context context;
    private final int itemLayout;

    public ProductAdapter(List<Product> dataList, Context context, int itemLayout) {
        this.dataList = dataList;
        this.context = context;
        this.itemLayout = itemLayout;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = dataList.get(position);

        if (product != null) {
            Glide.with(context).load(product.getImg_cover()).into(holder.imgProduct);
            holder.tvName.setText(product.getName());
            String price = product.getPrice();
            String formattedAmount = CurrencyUtils.formatCurrency(price);
            holder.tvPrice.setText(formattedAmount);
            holder.tvStatus.setText(product.getStatus());
            holder.tvSold.setText("Đã bán: " + product.getSold());
            holder.rltProduct.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailProductActivity.class);
                intent.putExtra("img_cover", product.getImg_cover());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("id", product.get_id());
                intent.putExtra("quantity", product.getQuantity());
                context.startActivity(intent);
            });

        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvStatus, tvSold;
        private final TextView tvPrice;
        private final ImageView imgProduct;
        private final RelativeLayout rltProduct;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvSold = (TextView) itemView.findViewById(R.id.tv_sold);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_status);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            rltProduct = (RelativeLayout) itemView.findViewById(R.id.rlt_product);
        }
    }
}
