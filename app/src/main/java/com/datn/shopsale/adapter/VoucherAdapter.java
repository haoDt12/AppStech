package com.datn.shopsale.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.datn.shopsale.R;
import com.datn.shopsale.response.GetListVoucher;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private List<GetListVoucher.ListVoucher> list;
    private Context context;
    private int actionCode;
    public VoucherAdapter(List<GetListVoucher.ListVoucher> list, Context context,int actionCode) {
        this.list = list;
        this.context = context;
        this.actionCode = actionCode;
    }

    @NonNull
    @Override
    public VoucherAdapter.VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher,parent,false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherAdapter.VoucherViewHolder holder, int position) {
        GetListVoucher.ListVoucher voucher = list.get(position);
        if(voucher == null){
            return;
        }
        if(actionCode == 1){
            holder.btnUse.setVisibility(View.GONE);
        }
        holder.tvTitle.setText(voucher.getTitle());
        holder.tvContent.setText(voucher.getContent());
        holder.tvDate.setText(String.format("Từ %s đến %s", voucher.getFromDate(), voucher.getToDate()));
        holder.btnUse.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderAdapter.class);
            intent.putExtra("voucher",voucher);
            intent.putExtra("price", voucher.getPrice()+ "");
            ((Activity) context).setResult(Activity.RESULT_OK,intent);
            ((Activity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0: list.size();
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout layoutSwipe;
        private LinearLayout dragLayout;
        private LinearLayout mainLayout;
        private LinearLayout lnlLeft;
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvDate;
        private Button btnUse;
        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutSwipe = (SwipeLayout) itemView.findViewById(R.id.layout_swipe);
            dragLayout = (LinearLayout) itemView.findViewById(R.id.dragLayout);
            mainLayout = (LinearLayout) itemView.findViewById(R.id.mainLayout);
            lnlLeft = (LinearLayout) itemView.findViewById(R.id.lnl_left);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            btnUse = (Button) itemView.findViewById(R.id.btn_use);
        }
    }
}
