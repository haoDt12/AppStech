package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.modelsv2.FeedBack;
import com.datn.shopsale.ui.cart.CartFragment;
import com.datn.shopsale.ui.cart.IChangeQuantity;
import com.datn.shopsale.utils.Animation;
import com.datn.shopsale.utils.GetImgIPAddress;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private  List<com.datn.shopsale.modelsv2.FeedBack> list;
    private  Context mContext;

    public ReviewAdapter(List<com.datn.shopsale.modelsv2.FeedBack> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedBack objFeedBack = list.get(position);
        holder.cmt.setText(objFeedBack.getComment());
        holder.nameUser.setText(objFeedBack.getCustomer_id().getFull_name());
        holder.ratingBar.setRating(Float.parseFloat(objFeedBack.getRating()));
        Glide.with(mContext).load(GetImgIPAddress.convertLocalhostToIpAddress(objFeedBack.getCustomer_id().getAvatar())).into(holder.imgDoctor);
        LayerDrawable starsDrawable = (LayerDrawable) holder.ratingBar.getProgressDrawable();
        starsDrawable.getDrawable(2).setColorFilter(mContext.getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        starsDrawable.getDrawable(0).setColorFilter(mContext.getResources().getColor(R.color.blur_gray), PorterDuff.Mode.SRC_ATOP);


    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgDoctor;
        private TextView nameUser;
        private RatingBar ratingBar;
        private TextView cmt,date;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            imgDoctor = (CircleImageView) itemView.findViewById(R.id.img_doctor);
            nameUser = (TextView) itemView.findViewById(R.id.nameUser);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            cmt = (TextView) itemView.findViewById(R.id.cmt);
            date = (TextView) itemView.findViewById(R.id.date);


        }
    }


}