package com.datn.shopsale.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.ShowDetailOrderActivity;
import com.datn.shopsale.models.Orders;
import com.datn.shopsale.response.GetListOrderResponse;
import com.datn.shopsale.response.GetProductResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.CurrencyUtils;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListOrderAdapter extends RecyclerView.Adapter<ListOrderAdapter.ViewHolder>{
    private List<Orders> mList ;
    private Context context;
    private ApiService apiService;
    private PreferenceManager preferenceManager;

    public ListOrderAdapter(List<Orders> mList,Context context) {
        this.mList = mList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listorders,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListOrderAdapter.ViewHolder holder, int position) {
        Orders order = mList.get(position);
        if (order ==null){
            return;
        }
        GetListOrderResponse.Product products = order.getProduct().get(0);

        holder.itemOrder.setOnClickListener(view -> {
            Intent i = new Intent(context, ShowDetailOrderActivity.class);
            i.putExtra("orderId", order.getId());
            context.startActivity(i);
        });
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(context);
        String proId = products.productId;
        String token = preferenceManager.getString("token");
        Call<GetProductResponse.Root> call = apiService.getProductById(token, proId);
        call.enqueue(new Callback<GetProductResponse.Root>() {
            @Override
            public void onResponse(Call<GetProductResponse.Root> call, Response<GetProductResponse.Root> response) {
                if (response.body().getCode()==1){
                    Log.d("zzzzzzzzzzz", "onResponse: "+response.body().getProduct());
                    holder.tvName.setText(response.body().getProduct().getTitle());

                    holder.tvPrice.setText(CurrencyUtils.formatCurrency(response.body().getProduct().getPrice()));
                    Glide.with(context).load(GetImgIPAddress.convertLocalhostToIpAddress(response.body().getProduct().getImg_cover())).into(holder.imgProduct);

                } else {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetProductResponse.Root> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null?0: mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct;
        private TextView tvName;
        private TextView tvPrice;
        private CardView itemOrder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            itemOrder = (CardView) itemView.findViewById(R.id.item_order);
        }
    }
}
