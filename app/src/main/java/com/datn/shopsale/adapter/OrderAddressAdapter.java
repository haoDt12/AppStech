package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.datn.shopsale.activities.OrderActivity;
import com.datn.shopsale.models.Address;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderAddressAdapter extends RecyclerView.Adapter<OrderAddressAdapter.OrderAddressViewHolder> {
    private ArrayList<Address> list;
    private Context context;
    private int selectedItemPosition = -1;

    public OrderAddressAdapter(ArrayList<Address> list, Context context) {
        this.list = list;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderAddressAdapter.OrderAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_address,parent,false);
        return new OrderAddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAddressAdapter.OrderAddressViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Address address = list.get(position);
        if (address != null) {
            holder.tvName.setText(address.getName());
            holder.tvPhoneNumber.setText(address.getPhone_number());
            holder.tvAddressCity.setText(address.getCity());
            holder.tvAddressStreet.setText(address.getStreet());
            holder.mainLayout.setBackgroundResource(selectedItemPosition == position ? R.color.red : R.color.mauve);
            holder.mainLayout.setOnClickListener(v -> {
                selectedItemPosition = position;
                notifyDataSetChanged(); // Notify the adapter that the data set changed
                Intent intent = new Intent(context, OrderActivity.class);
                intent.putExtra("nameAddress",address.getName());
                intent.putExtra("phoneAddress",address.getPhone_number());
                intent.putExtra("cityAddress",address.getCity());
                intent.putExtra("streetAddress",address.getStreet());
                intent.putExtra("addressId", address.get_id());
                ((Activity) context).setResult(Activity.RESULT_OK, intent);
                ((Activity) context).finish();
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null?0: list.size();
    }

    public class OrderAddressViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgLocation;
        private View mainLayout;
        private TextView tvName;
        private TextView tvPhoneNumber;
        private TextView tvAddressCity;
        private TextView tvAddressStreet;
        public OrderAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLocation = (CircleImageView) itemView.findViewById(R.id.img_location);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tv_phone_number);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            tvAddressCity = (TextView) itemView.findViewById(R.id.tv_address_city);
            tvAddressStreet = (TextView) itemView.findViewById(R.id.tv_address_street);
        }
    }
}
