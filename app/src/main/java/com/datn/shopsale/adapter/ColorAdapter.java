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
import com.datn.shopsale.models.Option;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private List<Option> colorList;
    private OnColorItemClickListener listener; // Define a listener interface
    private String selectedColor;

    public ColorAdapter(List<Option> colorList, OnColorItemClickListener listener) {
        this.colorList = colorList;
        this.listener = listener;
        selectedColor = colorList.get(0).getTitle();
        // Initialize the color mapping (customize this as needed)
    }

    public interface OnColorItemClickListener {
        void onColorItemClick(Option option);
    }

    @NonNull
    @Override
    public ColorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorAdapter.ViewHolder holder, int position) {
        final String color = colorList.get(position).getTitle();
        holder.btnColor.setBackgroundColor(Color.parseColor(colorList.get(position).getContent()));
        holder.tvColor.setText(color);
        if (color.equals(selectedColor)) {
            holder.btnColor.setStrokeColorResource(R.color.red);
        } else {
            holder.btnColor.setStrokeColorResource(R.color.colorNormal);
        }

        holder.btnColor.setOnClickListener(v -> {
            String clickedColor = colorList.get(holder.getAdapterPosition()).getTitle();
            Option option = colorList.get(holder.getAdapterPosition());
            if (selectedColor != null && selectedColor.equals(clickedColor)) {
                selectedColor = null;
            } else {
                selectedColor = clickedColor;
            }
            notifyDataSetChanged();
            listener.onColorItemClick(option);
        });
    }

    @Override
    public int getItemCount() {
        return colorList == null ? 0 : colorList.size();
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
