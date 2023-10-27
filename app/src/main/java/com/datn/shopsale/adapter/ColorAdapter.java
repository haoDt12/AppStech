package com.datn.shopsale.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private ArrayList<String> colorList;
    private OnColorItemClickListener listener; // Define a listener interface
    private String selectedColor;
    private HashMap<String, Integer> colorToColorRes;

    public ColorAdapter(ArrayList<String> colorList,OnColorItemClickListener listener) {
        this.colorList = colorList;
        this.listener=listener;
        selectedColor=null;
        // Initialize the color mapping (customize this as needed)
        colorToColorRes = new HashMap<>();
        colorToColorRes.put("Red", R.color.red);
        colorToColorRes.put("White", R.color.white);
        colorToColorRes.put("Black", R.color.black);
        colorToColorRes.put("Titan", R.color.titan);   // Replace R.color.whiteColor with your color resources
        colorToColorRes.put("Blue", R.color.blue);
        colorToColorRes.put("Pink",R.color.pink);
        colorToColorRes.put("Violet",R.color.violet);
        colorToColorRes.put("Yellow",R.color.yellow);
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
        final String color = colorList.get(position);
        if (colorToColorRes.containsKey(color)) {
            holder.btnColor.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), colorToColorRes.get(color)));
        }

        if (color.equals(selectedColor)) {
            holder.btnColor.setStrokeColorResource(R.color.red); // Đặt màu viền khi màu được chọn
        } else {
            holder.btnColor.setStrokeColorResource(R.color.colorNormal); // Đặt viền trong suốt khi màu không được chọn
        }

        holder.btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clickedColor = colorList.get(holder.getAdapterPosition());

                if (selectedColor != null && selectedColor.equals(clickedColor)) {
                    selectedColor = null;
                } else {
                    selectedColor = clickedColor;
                }
                notifyDataSetChanged();
                listener.onColorItemClick(clickedColor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialButton btnColor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnColor = (MaterialButton) itemView.findViewById(R.id.btn_color);
        }
    }
}
