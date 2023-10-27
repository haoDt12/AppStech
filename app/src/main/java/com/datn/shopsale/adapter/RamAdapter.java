package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class RamAdapter extends RecyclerView.Adapter<RamAdapter.ViewHolder> {
    private ArrayList<String> ramList;
    private String selectedPosition = null;
    private OnRamItemClickListener listener; // Define a listener interface

    public RamAdapter(ArrayList<String> ramList,OnRamItemClickListener listener) {
        this.ramList = ramList;
        this.listener= listener;
    }
    public interface OnRamItemClickListener {
        void onRamItemClick(String ram);
    }

    @NonNull
    @Override
    public RamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ramrom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RamAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final String ram = ramList.get(position);
        holder.tvRam.setText(ram);
        holder.rdoRam.setChecked(ram.equals(selectedPosition));

        holder.rdoRam.setOnClickListener(v -> {
            String clickedRam = ramList.get(holder.getAdapterPosition());
            selectedPosition = ram;
            notifyDataSetChanged();
            listener.onRamItemClick(clickedRam);
        });
    }

    @Override
    public int getItemCount() {
        return ramList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RadioButton rdoRam;
        private TextView tvRam;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rdoRam = (RadioButton) itemView.findViewById(R.id.rdo_ram);
            tvRam = (TextView) itemView.findViewById(R.id.tv_ram);
        }
    }
}
