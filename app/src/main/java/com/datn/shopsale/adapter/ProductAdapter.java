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
import com.datn.shopsale.utils.GetImgIPAddress;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> dataList;
    private Context context;
    private int itemLayout;

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

        if (product == null) {
            return;
        }
        Glide.with(context).load(GetImgIPAddress.convertLocalhostToIpAddress(product.getImg_cover())).into(holder.imgProduct);
        holder.tvName.setText(product.getName());
        String price = product.getPrice();
        String formattedAmount = CurrencyUtils.formatCurrency(price);
        holder.tvPrice.setText(formattedAmount);
        holder.tvStatus.setText(product.getStatus());
        holder.tvSold.setText("Đã bán: " + product.getSold());
        holder.rltProduct.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailProductActivity.class);
            intent.putExtra("img_cover", product.getImg_cover());
//            intent.putExtra("video", product.getVideo());
//            intent.putExtra("title", product.getTitle());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("id", product.get_id());
//            intent.putExtra("imgCover", product.getImg_cover());
            intent.putExtra("quantity", product.getQuantity());
//            intent.putExtra("option", product.getOption());
//            intent.putExtra("product", product);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvStatus, tvSold;
        private TextView tvPrice;
        private ImageView imgProduct;
        private RelativeLayout rltProduct;


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
