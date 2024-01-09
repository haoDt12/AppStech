package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.modelsv2.ProductCart;
import com.datn.shopsale.ui.cart.IChangeQuantity;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.GetImgIPAddress;

import java.util.List;


@SuppressLint("StaticFieldLeak")
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private static List<ProductCart> listItem;
    private static Context mContext;
    private static IChangeQuantity iChangeQuantity;
    private boolean isExpanded = false;
    @SuppressLint("NotifyDataSetChanged")
    public void setList (List<ProductCart> listItem){
        CartAdapter.listItem = listItem;
        notifyDataSetChanged();
    }

    public CartAdapter(List<ProductCart> listItem, Context context, IChangeQuantity IChangeQuantity) {
        CartAdapter.listItem = listItem;
        CartAdapter.mContext = context;
        CartAdapter.iChangeQuantity = IChangeQuantity;
//        CartAdapter.cartFragment = cartFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ProductCart item = listItem.get(position);
        int status = item.getStatus();
        holder.cbCheck.setChecked(status != 1);
        holder.tvName.setText(item.getProductCart().getName());


        holder.tvName.setOnClickListener(view -> {
            if (isExpanded) {
                holder.tvName.setMaxLines(2);
                isExpanded = false;
            } else {
                holder.tvName.setMaxLines(Integer.MAX_VALUE);
                isExpanded = true;
            }
        });
        int price = Integer.parseInt(item.getProductCart().getPrice()) * item.getQuantity();
        holder.tvPrice.setText(CurrencyUtils.formatCurrency(String.valueOf(price)));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        Glide.with(mContext).load(GetImgIPAddress.convertLocalhostToIpAddress(item.getProductCart().getImg_cover())).into(holder.img_product);
        holder.cbCheck.setChecked(item.getStatus() == 2);
        holder.cbCheck.setOnClickListener(v -> {
            boolean isSelected = holder.cbCheck.isChecked();
            boolean isCheckAll = true;
            if (isSelected) {
                item.setStatus(2);
                holder.cbCheck.setChecked(true);
                for (ProductCart product: listItem) {
                    if(product.getStatus() == 1){
                        isCheckAll = false;
                        break;
                    }
                }
                iChangeQuantity.IclickCheckBox(item, position,isCheckAll);
            } else {
                holder.cbCheck.setChecked(false);
                item.setStatus(1);
                iChangeQuantity.IclickCheckBox2(item, position);
            }
        });


        holder.imgIncrease.setOnClickListener(v -> iChangeQuantity.IclickIncrease(item, position));
        holder.imgDecrease.setOnClickListener(v -> iChangeQuantity.IclickReduce(item, position));

    }

    @Override
    public int getItemCount() {
        if (listItem != null) {
            return listItem.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layoutForeground;
        TextView tvName, tvPrice;
        TextView tvQuantity;
        CheckBox cbCheck;
        ImageButton imgDecrease, imgIncrease;
        ImageView img_product;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutForeground = itemView.findViewById(R.id.layoutForeground);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            cbCheck = itemView.findViewById(R.id.cb_selected);
            imgDecrease = itemView.findViewById(R.id.img_decrease);
            imgIncrease = itemView.findViewById(R.id.img_increase);
            img_product = itemView.findViewById(R.id.img_cart);
        }
    }

}