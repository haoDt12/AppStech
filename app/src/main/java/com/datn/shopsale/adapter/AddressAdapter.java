package com.datn.shopsale.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.datn.shopsale.models.Address;
import com.datn.shopsale.models.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private ArrayList<Address> dataList;

    public AddressAdapter(ArrayList<Address> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address,parent,false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = dataList.get(position);
        if (address == null){
            return;
        }
        holder.tvName.setText(address.getName());
        holder.tvPhoneNumber.setText(address.getPhone_number());
        holder.tvAddressDetail.setText(address.getDetail());
    }

    @Override
    public int getItemCount() {
        if (dataList == null){
            return 0;
        }
        return dataList.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgLocation;
        private TextView tvName;
        private TextView tvPhoneNumber;
        private TextView tvAddressDetail;
        private TextView tvEdit;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLocation = (CircleImageView) itemView.findViewById(R.id.img_location);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tv_phone_number);
            tvAddressDetail = (TextView) itemView.findViewById(R.id.tv_address_detail);
            tvEdit = (TextView) itemView.findViewById(R.id.tv_edit);
        }
    }
}