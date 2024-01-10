package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.IActionCate;
import com.datn.shopsale.R;
import com.datn.shopsale.modelsv2.Category;
import com.datn.shopsale.ui.home.HomeFragment;
import com.datn.shopsale.utils.GetImgIPAddress;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.categoriesViewHolder> {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static List<Category> listCate;

    private static IActionCate iActionCate;

    public CategoriesAdapter(Context context, List<Category> categories, IActionCate iActionCate) {
        CategoriesAdapter.context = context;
        listCate = categories;
        CategoriesAdapter.iActionCate = iActionCate;
    }

    @NonNull
    @Override
    public categoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new categoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull categoriesViewHolder holder, int position) {
        Category category = listCate.get(position);
        holder.tvName.setText(category.getName());
        Glide.with(context).load(GetImgIPAddress.convertLocalhostToIpAddress(category.getImg())).into(holder.img);
        if (category.get_id().equals("-1")) {
            int paddingImage = 40;
            holder.img.setPadding(paddingImage, paddingImage, paddingImage, paddingImage);
        }
        if (position >= 12) {
            if (HomeFragment.isDisableItem) {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    @Override
    public int getItemCount() {
        return listCate.size();
    }

    public static class categoriesViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView img;

        public categoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_cate);
            tvName = itemView.findViewById(R.id.tv_title);

            itemView.setOnClickListener(v -> iActionCate.onClick(listCate.get(getAdapterPosition())));

        }
    }
}
