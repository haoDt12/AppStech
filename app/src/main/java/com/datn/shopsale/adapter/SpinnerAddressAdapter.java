package com.datn.shopsale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.datn.shopsale.R;
import com.datn.shopsale.models.Address;

import java.util.List;

public class SpinnerAddressAdapter extends ArrayAdapter<Address> {
    private List<Address> mList;
    private LayoutInflater layoutInflater;

    public SpinnerAddressAdapter(@NonNull Context context, int resource, List<Address> mList) {
        super(context, resource, mList);
        this.mList = mList;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.item_spinner_address, parent, false);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvPhone = (TextView) view.findViewById(R.id.tv_phone);
        TextView tvAddress = (TextView) view.findViewById(R.id.tv_address);
        Address address = mList.get(position);
        if(address != null){
            tvName.setText(address.getName());
            tvPhone.setText(address.getPhone_number());
            tvAddress.setText(String.format("%s%s", address.getStreet(), address.getCity()));
        }
        return view;
    }
}
