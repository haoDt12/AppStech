package com.datn.shopsale.adapter;

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

import com.datn.shopsale.R;
import com.datn.shopsale.activities.DetailProductActivity;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<GetListProductResponse.Product> dataList;
    private Context context;
    private int itemLayout;
    public ProductAdapter(ArrayList<GetListProductResponse.Product> dataList, Context context, int itemLayout){
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GetListProductResponse.Product product = dataList.get(position);
        if (product == null){
            return;
        }
        Picasso.get().load(GetImgIPAddress.convertLocalhostToIpAddress(product.getImg_cover())).into(holder.imgProduct);
        holder.tvName.setText(product.getTitle());
        String price = product.getPrice();
        String formattedAmount = CurrencyUtils.formatCurrency(price);
        holder.tvPrice.setText(formattedAmount);
        if(product.getQuantity().equals("0")){
            holder.tvStatus.setText(context.getText(R.string.hrt_hang));
        }else {
            holder.tvStatus.setText(context.getText(R.string.con_hang));
        }
        holder.rltProduct.setOnClickListener(v->{
            Intent intent = new Intent(context, DetailProductActivity.class);
            intent.putExtra("list_img",product.getList_img());
            intent.putExtra("video",product.getVideo());
            intent.putExtra("title",product.getTitle());
            intent.putExtra("price",product.getPrice());
            intent.putExtra("id",product.get_id());
            intent.putExtra("imgCover",product.getImg_cover());
            intent.putExtra("product",product);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return dataList == null?0:dataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName, tvStatus;
        private TextView tvPrice;
        private ImageView imgProduct;
        private RelativeLayout rltProduct;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_status);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            rltProduct = (RelativeLayout) itemView.findViewById(R.id.rlt_product);
        }
    }
}
