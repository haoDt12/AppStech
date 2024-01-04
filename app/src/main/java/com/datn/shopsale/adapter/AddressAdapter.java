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

import com.daimajia.swipe.SwipeLayout;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.OrderActivity;
import com.datn.shopsale.modelsv2.Address;

import java.util.ArrayList;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private ArrayList<Address> dataList;
    private Callback callback;
    private Context context;
    private int selectedItemPosition = -1;
    private boolean isAddressSelect;
    @SuppressLint("NotifyDataSetChanged")
    public AddressAdapter(ArrayList<Address> dataList, Context context, Callback callback) {
        this.dataList = dataList;
        this.context = context;
        this.callback = callback;
        notifyDataSetChanged();
    }
    public void setIsAddress(boolean isAddressSelect) {
        this.isAddressSelect = isAddressSelect;
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
        holder.bind(address,position);
    }

    @Override
    public int getItemCount() {
        if (dataList == null){
            return 0;
        }
        return dataList.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvPhoneNumber;
        private TextView tvAddressCity;
        private TextView tvAddressStreet;

        private View dragLayout;
        private View mainLayout;
        private SwipeLayout swipeLayout;


        private TextView tvEdit,tvDelete;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tv_phone_number);
            tvAddressCity = (TextView) itemView.findViewById(R.id.tv_address_city);
            tvAddressStreet = (TextView) itemView.findViewById(R.id.tv_address_street);
            tvEdit = (TextView) itemView.findViewById(R.id.tv_edit);
            tvDelete = (TextView) itemView.findViewById(R.id.tv_delete);

            dragLayout = itemView.findViewById(R.id.dragLayout);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.layout_swipe);
        }


        @SuppressLint({"NotifyDataSetChanged", "ClickableViewAccessibility"})
        public void bind(Address address, int position) {
            tvName.setText(address.getName());
            tvPhoneNumber.setText(address.getPhone());
            tvAddressCity.setText(address.getCity());
            tvAddressStreet.setText(address.getStreet());
            mainLayout.setBackgroundResource(selectedItemPosition == position ? R.color.red : R.color.mauve);
            mainLayout.setOnClickListener(v -> {
                if(isAddressSelect) {
                    selectedItemPosition = position;
                    notifyDataSetChanged(); // Notify the adapter that the data set changed
                    Intent intent = new Intent(context, OrderActivity.class);
                    intent.putExtra("nameAddress", address.getName());
                    intent.putExtra("phoneAddress", address.getPhone());
                    intent.putExtra("cityAddress", address.getCity());
                    intent.putExtra("streetAddress", address.getStreet());
                    intent.putExtra("addressId", address.get_id());
                    ((Activity) context).setResult(Activity.RESULT_OK, intent);
                    ((Activity) context).finish();
                }
            });

            tvEdit.setOnClickListener(v-> callback.editAddress(address));
            tvDelete.setOnClickListener(v-> callback.deleteAddress(address));
            dragLayout.setOnTouchListener((v, event) -> {
                return true; // Consume touch events
            });
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, dragLayout);
        }
    }


    public interface Callback{
        void editAddress(Address address);
        void deleteAddress(Address address);
    }
}