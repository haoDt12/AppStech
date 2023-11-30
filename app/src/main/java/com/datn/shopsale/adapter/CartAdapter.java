package com.datn.shopsale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.models.Cart;
import com.datn.shopsale.ui.cart.CartFragment;
import com.datn.shopsale.ui.cart.IChangeQuantity;
import com.datn.shopsale.utils.Animation;
import com.datn.shopsale.utils.GetImgIPAddress;

import java.util.List;


@SuppressLint("StaticFieldLeak")
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private static List<Cart> listItem;
    private static Context mContext;
    private static CartFragment cartFragment;
    private static IChangeQuantity iChangeQuantity;
    private boolean isExpanded = false;

    public CartAdapter(List<Cart> listItem,Context context,IChangeQuantity IChangeQuantity) {
        CartAdapter.listItem = listItem;
        CartAdapter.mContext = context;
        CartAdapter.iChangeQuantity = IChangeQuantity;
//        CartAdapter.cartFragment = cartFragment;
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
        int _index = position;
        int status = item.getStatus();
        int quantity = item.getQuantity();

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

        holder.tvPrice.setText(item.getQuantity()* item.getPrice() + " đ");
        holder.tvQuantity.setText(item.getQuantity() + "");
        Glide.with(mContext).load(GetImgIPAddress.convertLocalhostToIpAddress(item.getImgCover())).into(holder.img_product);
        holder.cbCheck.setOnClickListener(v -> {
            boolean isSelected = holder.cbCheck.isChecked();
            if (isSelected) {
                item.setStatus(2);
                iChangeQuantity.IclickCheckBox(item,_index);
//                cartFragment.updateStatusCart(2, position);
            } else {
                item.setStatus(1);
                iChangeQuantity.IclickCheckBox2(item,_index);
//                cartFragment.updateStatusCart(1, position);
            }
        });


        holder.imgIncrease.setOnClickListener(v -> {
            iChangeQuantity.IclickIncrease(item,_index);

        });
        holder.imgDecrease.setOnClickListener(v -> {
            iChangeQuantity.IclickReduce(item,_index);
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
        ImageView img_product;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutForeground = itemView.findViewById(R.id.layoutForeground);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            cbCheck = itemView.findViewById(R.id.cb_selected);
            imgDecrease = itemView.findViewById(R.id.img_decrease);
            imgIncrease = itemView.findViewById(R.id.img_increase);
            img_product = itemView.findViewById(R.id.img_cart);



        }
    }

//    public void deleteItem(int index, int pos) {
//        listItem.remove(index);
//        // update in server
//        cartFragment.updateStatusCart(0, pos);
//        notifyItemRemoved(index);
//    }

//    public void undoItem(Cart cart, int index, int pos) {
//        listItem.add(index, cart);
//        // update in server
//        cartFragment.updateStatusCart(1, pos);
//        notifyItemInserted(index);
//    }

//    public void updateList(List<Cart> newList) {
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new OrderDiffCallback(newList, listItem));
//        int oldSize = listItem.size();
//        listItem.clear();
//        listItem.addAll(newList);
//        diffResult.dispatchUpdatesTo(this);
//        int newSize = newList.size();
//    }

//    private static class OrderDiffCallback extends DiffUtil.Callback {
//        private final List<Cart> oldCartList;
//        private final List<Cart> newCartList;
//
//        public OrderDiffCallback(List<Cart> newOrderList, List<Cart> oldOrderList) {
//            this.newCartList = newOrderList;
//            this.oldCartList = oldOrderList;
//        }
//
//        @Override
//        public int getOldListSize() {
//            return oldCartList.size();
//        }
//
//        @Override
//        public int getNewListSize() {
//            return newCartList.size();
//        }
//
//        @Override
//        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//            return Objects.equals(oldCartList.get(oldItemPosition).getId(), newCartList.get(newItemPosition).getId());
//        }
//
//        @Override
//        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//            Cart oldOrder = oldCartList.get(oldItemPosition);
//            Cart newOrder = newCartList.get(newItemPosition);
//            return oldOrder.getTitle().equals(newOrder.getTitle())
//                    && Objects.equals(oldOrder.getPrice(), newOrder.getPrice())
//                    && Objects.equals(oldOrder.getQuantity(), newOrder.getQuantity())
//                    && Objects.equals(oldOrder.getUserId(), newOrder.getUserId())
//                    && Objects.equals(oldOrder.getDate(), newOrder.getDate())
//                    && Objects.equals(oldOrder.getId(), newOrder.getId())
////                    && Objects.equals(oldOrder.getIdMerchant(), newOrder.getIdMerchant())
////                    && Objects.equals(oldOrder.getNotes(), newOrder.getNotes())
////                    && Objects.equals(oldOrder.getPos(), newOrder.getPos())
//                    && Objects.equals(oldOrder.getStatus(), newOrder.getStatus());
//        }
//    }


}