package com.datn.shopsale.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.models.Product;
import com.datn.shopsale.response.GetListOrderResponse;
import com.datn.shopsale.response.GetListProductResponse;
import com.datn.shopsale.response.GetOrderResponse;
import com.datn.shopsale.response.GetProductResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.PreferenceManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListProductOfOrderAdapter extends RecyclerView.Adapter<ListProductOfOrderAdapter.ViewHolder> {

    private ArrayList<GetOrderResponse.Product> dataProduct;
    private Context context;
    private ApiService apiService;
    private PreferenceManager preferenceManager;

    public ListProductOfOrderAdapter(ArrayList<GetOrderResponse.Product> dataProduct, Context context) {
        this.dataProduct = dataProduct;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_productoforder,parent,false);
        return new ListProductOfOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GetOrderResponse.Product pro = dataProduct.get(position);
        if (pro == null){
            return;
        }
        String proId = pro.productId;

        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(context);

        String token = preferenceManager.getString("token");
        Log.d("xxxxxxxxxx", "onBindViewHolder: "+proId);

        Call<GetProductResponse.Root> call = apiService.getProductById(token, proId);
        call.enqueue(new Callback<GetProductResponse.Root>() {
            @Override
            public void onResponse(Call<GetProductResponse.Root> call, Response<GetProductResponse.Root> response) {
                if (response.body().getCode()==1){
                    Log.d("zzzzzzzzzzz", "onResponse: "+response.body().getProduct());
                    holder.tvTitleProductOfOrder.setText(response.body().getProduct().getTitle());

                    holder.tvPriceProductOfOrder.setText(formatCurrency(response.body().getProduct().getPrice()));
                    Glide.with(context).load(response.body().getProduct().getImg_cover()).into(holder.imgProduct);

                } else {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetProductResponse.Root> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.tvColorProductOfOrder.setText(pro.color);
        holder.tvNumProductOfOrder.setText("Số lượng: "+pro.quantity+"");
        holder.tvRamRomProductOfOrder.setText("Ram-Rom"+pro.ram_rom);
    }

    @Override
    public int getItemCount() {
        return dataProduct == null ? 0 : dataProduct.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitleProductOfOrder;
        private TextView tvPriceProductOfOrder;
        private TextView tvColorProductOfOrder;
        private TextView tvNumProductOfOrder;
        private TextView tvRamRomProductOfOrder;
        private ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            tvTitleProductOfOrder = (TextView) itemView.findViewById(R.id.tv_titleProductOfOrder);
            tvPriceProductOfOrder = (TextView) itemView.findViewById(R.id.tv_priceProductOfOrder);
            tvColorProductOfOrder = (TextView) itemView.findViewById(R.id.tv_colorProductOfOrder);
            tvNumProductOfOrder = (TextView) itemView.findViewById(R.id.tv_numProductOfOrder);
            tvRamRomProductOfOrder = (TextView) itemView.findViewById(R.id.tv_ramRomProductOfOrder);
        }
    }
    public String formatCurrency(String price) {
        // Tạo một đối tượng DecimalFormat với mẫu số mong muốn
        long number = Long.parseLong(price);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setGroupingSeparator('.'); // Set '.' as the grouping separator
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###.###", formatSymbols);
        String formattedNumber = decimalFormat.format(number);
        return formattedNumber;
    }
}
