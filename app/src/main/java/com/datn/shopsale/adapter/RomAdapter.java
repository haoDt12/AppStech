package com.datn.shopsale.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class RomAdapter extends RecyclerView.Adapter<RomAdapter.RomViewHolder> {
    private ArrayList<String> romList;
    private OnRomItemClickListener listener; // Define a listener interface
    private String selectedRom; // Define a listener interface

    public RomAdapter(ArrayList<String> romList, OnRomItemClickListener listener) {
        this.romList = romList;
        this.listener = listener;
        selectedRom = null;
    }

    public interface OnRomItemClickListener {
        void onRomItemClick(String rom);
    }
    @NonNull
    @Override
    public RomAdapter.RomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rom,parent,false);
        return new RomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RomAdapter.RomViewHolder holder, int position) {
        final String rom = romList.get(position);
        holder.btnRom.setText(rom);
        if (rom.equals(selectedRom)) {
            holder.btnRom.setStrokeColorResource(R.color.red); // Đặt màu viền khi màu được chọn
        } else {
            holder.btnRom.setStrokeColorResource(R.color.colorNormal); // Đặt viền trong suốt khi màu không được chọn
        }

        holder.btnRom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clickedRom = romList.get(holder.getAdapterPosition());

                if (selectedRom != null && selectedRom.equals(clickedRom)) {
                    selectedRom = null;
                } else {
                    selectedRom = clickedRom;
                }
                notifyDataSetChanged();
                listener.onRomItemClick(clickedRom);
            }
        });
    }

    @Override
    public int getItemCount() {
        return romList==null?0: romList.size();
    }

    public class RomViewHolder extends RecyclerView.ViewHolder {
        private MaterialButton btnRom;

        public RomViewHolder(@NonNull View itemView) {
            super(itemView);
            btnRom = itemView.findViewById(R.id.btn_rom);
        }
    }
}
