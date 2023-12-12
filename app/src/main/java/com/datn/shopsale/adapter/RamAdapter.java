package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.datn.shopsale.models.Option;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RamAdapter extends RecyclerView.Adapter<RamAdapter.ViewHolder> {
    private List<Option> ramList;
    private String selectedRam;
    private OnRamItemClickListener listener; // Define a listener interface
    public RamAdapter(List<Option> ramList,OnRamItemClickListener listener) {
        this.ramList = ramList;
        this.listener= listener;
        selectedRam=ramList.get(0).getTitle();
    }
    public interface OnRamItemClickListener {
        void onRamItemClick(Option option);
    }

    @NonNull
    @Override
    public RamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ram, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RamAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final String ram = ramList.get(position).getTitle();
        holder.btnRam.setText(ram);
        if (ram.equals(selectedRam)) {
            holder.btnRam.setStrokeColorResource(R.color.red); // Đặt màu viền khi màu được chọn
        } else {
            holder.btnRam.setStrokeColorResource(R.color.colorNormal); // Đặt viền trong suốt khi màu không được chọn
        }

        holder.btnRam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clickedRam = ramList.get(holder.getAdapterPosition()).getTitle();
                Option option = ramList.get(holder.getAdapterPosition());

                if (selectedRam != null && selectedRam.equals(clickedRam)) {
                    selectedRam = null;
                } else {
                    selectedRam = clickedRam;
                }
                notifyDataSetChanged();
                listener.onRamItemClick(option);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ramList==null?0: ramList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialButton btnRam;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnRam = itemView.findViewById(R.id.btn_ram);
        }
    }
}
