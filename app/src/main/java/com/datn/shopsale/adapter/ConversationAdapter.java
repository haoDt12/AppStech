package com.datn.shopsale.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.models.ChatRoomModal;
import com.datn.shopsale.ui.dashboard.chat.ChatActivityFirebase;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends FirestoreRecyclerAdapter<ChatRoomModal, ConversationAdapter.ConversationViewHolder> {
    private Context mContext;
    private PreferenceManager preferenceManager;

    public ConversationAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModal> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull ConversationViewHolder holder, int position, @NonNull ChatRoomModal model) {
        preferenceManager = new PreferenceManager(mContext);
        Glide.with(mContext).load(GetImgIPAddress.convertLocalhostToIpAddress(model.getAvatarUser())).into(holder.imgAvt);
        holder.tvNameUser.setText(model.getNameUser());
        if (preferenceManager.getString("userId").equals(model.getIdUserOfLastMessage())) {
            if(model.getLastImage().equals("")){
                holder.tvLastMessage.setText("Bạn: " + model.getLastMessage());
            }else {
                holder.tvLastMessage.setText("Bạn: Bạn đã gửi 1 ảnh");
            }

        } else {
            if(model.getLastImage().equals("")){
                holder.tvLastMessage.setText(model.getLastMessage());
            }else {
                holder.tvLastMessage.setText("Đã gửi 1 ảnh");
            }

        }
        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(mContext, ChatActivityFirebase.class);
            i.putStringArrayListExtra("listId", (ArrayList<String>) model.getUserId());
            mContext.startActivity(i);
        });
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgAvt;
        private TextView tvNameUser;
        private TextView tvLastMessage;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);


            imgAvt = (CircleImageView) itemView.findViewById(R.id.img_avt);
            tvNameUser = (TextView) itemView.findViewById(R.id.tv_nameUser);
            tvLastMessage = (TextView) itemView.findViewById(R.id.tv_lastMessage);

        }
    }
}
