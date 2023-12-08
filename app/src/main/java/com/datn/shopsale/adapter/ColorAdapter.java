package com.datn.shopsale.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.datn.shopsale.models.KeyValue;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private List<KeyValue> colorList;
    private OnColorItemClickListener listener; // Define a listener interface
    private String selectedColor;

    public ColorAdapter(List<KeyValue> colorList,OnColorItemClickListener listener) {
        this.colorList = colorList;
        this.listener=listener;
        selectedColor=colorList.get(0).getKey();
        // Initialize the color mapping (customize this as needed)
    }
    public interface OnColorItemClickListener {
        void onColorItemClick(String color);
    }

    @NonNull
    @Override
    public ColorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorAdapter.ViewHolder holder, int position) {
        final String color = colorList.get(position).getKey();
        Log.d("Zzzzz", "onBindViewHolder: " + colorList.get(position).getValue());
        holder.btnColor.setBackgroundColor(Color.parseColor(colorList.get(position).getValue()));
        holder.tvColor.setText(color);
        if (color.equals(selectedColor)) {
            holder.btnColor.setStrokeColorResource(R.color.red); // Đặt màu viền khi màu được chọn
        } else {
            holder.btnColor.setStrokeColorResource(R.color.colorNormal); // Đặt viền trong suốt khi màu không được chọn
        }

        holder.btnColor.setOnClickListener(v -> {
            String clickedColor = colorList.get(holder.getAdapterPosition()).getKey();

            if (selectedColor != null && selectedColor.equals(clickedColor)) {
                selectedColor = null;
            } else {
                selectedColor = clickedColor;
            }
            notifyDataSetChanged();
            listener.onColorItemClick(clickedColor);
        });
    }

    @Override
    public int getItemCount() {
        return colorList==null?0: colorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialButton btnColor;
        private TextView tvColor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnColor = (MaterialButton) itemView.findViewById(R.id.btn_color);
            tvColor = (TextView) itemView.findViewById(R.id.tv_color);
        }
    }
}
