package com.datn.shopsale.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.datn.shopsale.models.ListOder;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.squareup.picasso.Picasso;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ListOder mList;

    public OrderAdapter(ListOder mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(mList.getList().get(position).getTitle());
        holder.tvPrice.setText(String.valueOf(mList.getList().get(position).getPrice()));
        Picasso.get().load(GetImgIPAddress.convertLocalhostToIpAddress(mList.getList().get(position).getImgCover())).into(holder.imgProduct);
        holder.tvColor.setText(mList.getList().get(position).getColor());
        holder.tvQuantity.setText(String.valueOf(mList.getList().get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        return mList.getList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvPrice;

        private ImageView imgProduct;
        private TextView tvColor;
        private TextView tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            tvColor = (TextView) itemView.findViewById(R.id.tv_color);
            tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);
        }
    }
}
