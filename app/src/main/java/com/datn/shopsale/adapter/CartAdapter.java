package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.ui.cart.CartFragment;
import com.datn.shopsale.utils.Animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@SuppressLint("StaticFieldLeak")
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private static List<Cart> listItem;
    private static CartFragment cartFragment;
    private boolean isExpanded = false;

    public CartAdapter(List<Cart> listItem, CartFragment cartFragment) {
        CartAdapter.listItem = listItem;
        CartAdapter.cartFragment = cartFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Animation animation = new Animation();
        Cart item = listItem.get(position);

        int status = item.getStatus();
        int quantity = item.getQuantity();
        animation.decrease(quantity, holder.imgDecrease);
        animation.increase(quantity, holder.imgIncrease);
        holder.cbCheck.setChecked(status != 1);
        holder.tvName.setText(item.getTitle());

        holder.tvName.setOnClickListener(view -> {
            if (isExpanded) {
                holder.tvName.setMaxLines(2);
                isExpanded = false;
            } else {
                holder.tvName.setMaxLines(Integer.MAX_VALUE);
                isExpanded = true;
            }
        });


        holder.tvPrice.setText(item.getPrice() + "đ");
        holder.tvQuantity.setText(item.getQuantity() + "");

        holder.cbCheck.setOnClickListener(v -> {
            boolean isSelected = holder.cbCheck.isChecked();
            if (isSelected) {
                item.setStatus(2);
                cartFragment.updateStatusCart(2, position);
            } else {
                item.setStatus(1);
                cartFragment.updateStatusCart(1, position);
            }
        });


        holder.imgIncrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString().trim());
            currentQuantity++;
            if (currentQuantity > 20) {
                animation.increase(currentQuantity, holder.imgIncrease);
                return;
            }
            holder.tvQuantity.setText(String.valueOf(currentQuantity));
            double price = item.getPrice() / item.getQuantity() * currentQuantity;
            holder.tvPrice.setText(price + "đ");

            cartFragment.updateQuantityCart(currentQuantity, position);
            cartFragment.updatePriceCart(price, position);
//            cartFragment.updateView();

            animation.increase(currentQuantity, holder.imgIncrease);
            animation.decrease(currentQuantity, holder.imgDecrease);


        });
        holder.imgDecrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString().trim());
            currentQuantity--;
            if (currentQuantity < 1) {
                return;
            }
            holder.tvQuantity.setText(String.valueOf(currentQuantity));
            double price = item.getPrice() / item.getQuantity() * currentQuantity;
            holder.tvPrice.setText(price + "đ");

            cartFragment.updateQuantityCart(currentQuantity, position);
            cartFragment.updatePriceCart(price, position);
//            cartFragment.updateView();

            animation.increase(currentQuantity, holder.imgIncrease);
            animation.decrease(currentQuantity, holder.imgDecrease);

        });

    }

    @Override
    public int getItemCount() {
        if (listItem != null) {
            return listItem.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layoutForeground;
        TextView tvName, tvPrice;
        TextView tvQuantity;
        CheckBox cbCheck;
        ImageButton imgDecrease, imgIncrease;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutForeground = itemView.findViewById(R.id.layoutForeground);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            cbCheck = itemView.findViewById(R.id.cb_selected);
            imgDecrease = itemView.findViewById(R.id.img_decrease);
            imgIncrease = itemView.findViewById(R.id.img_increase);


            itemView.setOnClickListener(v -> {
                // update notes
                ArrayList<String> idProduct = listItem.get(getAdapterPosition()).getProductId();
                Dialog dialog = new Dialog(itemView.getContext());
                dialog.setContentView(R.layout.dialog_confirm);
                dialog.setCancelable(false);

                TextView tvConfirm = dialog.findViewById(R.id.tvConfirm);
                TextView tvCancel = dialog.findViewById(R.id.tvCancel);

                tvCancel.setOnClickListener(view -> dialog.dismiss());
                tvConfirm.setOnClickListener(view -> {
                    cartFragment.updateNotes("updating");
                    dialog.dismiss();
                });

                dialog.show();
                Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.CENTER);

            });
        }
    }

    public void deleteItem(int index, int pos) {
        listItem.remove(index);
        // update in server
        cartFragment.updateStatusCart(0, pos);
        notifyItemRemoved(index);
    }

    public void undoItem(Cart cart, int index, int pos) {
        listItem.add(index, cart);
        // update in server
        cartFragment.updateStatusCart(1, pos);
        notifyItemInserted(index);
    }

    public void updateList(List<Cart> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new OrderDiffCallback(newList, listItem));
        int oldSize = listItem.size();
        listItem.clear();
        listItem.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        int newSize = newList.size();
    }

    private static class OrderDiffCallback extends DiffUtil.Callback {
        private final List<Cart> oldCartList;
        private final List<Cart> newCartList;

        public OrderDiffCallback(List<Cart> newOrderList, List<Cart> oldOrderList) {
            this.newCartList = newOrderList;
            this.oldCartList = oldOrderList;
        }

        @Override
        public int getOldListSize() {
            return oldCartList.size();
        }

        @Override
        public int getNewListSize() {
            return newCartList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldCartList.get(oldItemPosition).getId(), newCartList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Cart oldOrder = oldCartList.get(oldItemPosition);
            Cart newOrder = newCartList.get(newItemPosition);
            return oldOrder.getTitle().equals(newOrder.getTitle())
                    && Objects.equals(oldOrder.getPrice(), newOrder.getPrice())
                    && Objects.equals(oldOrder.getQuantity(), newOrder.getQuantity())
                    && Objects.equals(oldOrder.getUserId(), newOrder.getUserId())
                    && Objects.equals(oldOrder.getDate(), newOrder.getDate())
                    && Objects.equals(oldOrder.getId(), newOrder.getId())
//                    && Objects.equals(oldOrder.getIdMerchant(), newOrder.getIdMerchant())
//                    && Objects.equals(oldOrder.getNotes(), newOrder.getNotes())
//                    && Objects.equals(oldOrder.getPos(), newOrder.getPos())
                    && Objects.equals(oldOrder.getStatus(), newOrder.getStatus());
        }
    }


}