package com.datn.shopsale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.models.MessageModel;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class MessageAdapter extends FirestoreRecyclerAdapter<MessageModel, MessageAdapter.MessageViewHolder> {

    private PreferenceManager preferenceManager;
    private Context mContext;

    public MessageAdapter(@NonNull FirestoreRecyclerOptions<MessageModel> options, Context context) {
        super(options);
        this.mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull MessageModel model) {
        preferenceManager = new PreferenceManager(mContext);

        if(model.getSenderId().equals(preferenceManager.getString("userId"))){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChat.setText(model.getMessage());
            if(model.getImage().equals("")){
                holder.imgRight.setVisibility(View.GONE);
            }else {
                holder.rightChatLayout.setBackgroundResource(R.drawable.bg_custom_chat_white);
                Glide.with(mContext).load(GetImgIPAddress.convertLocalhostToIpAddress(model.getImage())).into(holder.imgRight);
            }
            if(model.getMessage().equals("")){
                holder.rightChat.setVisibility(View.GONE);
            }
            holder.timeRight.setText(Constants.timestamptoString(model.getTimestamp()));


        }else {
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChat.setText(model.getMessage());
            if(model.getImage().equals("")){
                holder.imgLeft.setVisibility(View.GONE);
            }else {
                holder.leftChatLayout.setBackgroundResource(R.drawable.bg_custom_chat_white);
            }
            if(model.getMessage().equals("")){
                holder.leftChat.setVisibility(View.GONE);
            }
            holder.timeLeft.setText(Constants.timestamptoString(model.getTimestamp()));
            Glide.with(mContext).load(GetImgIPAddress.convertLocalhostToIpAddress(model.getImage())).into(holder.imgLeft);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat, parent, false);
        return new MessageViewHolder(view);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout leftChatLayout;
        private TextView leftChat;
        private LinearLayout rightChatLayout;
        private TextView rightChat,timeLeft,timeRight;
        private ImageView imgLeft,imgRight;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = (LinearLayout) itemView.findViewById(R.id.left_chatLayout);
            leftChat = (TextView) itemView.findViewById(R.id.left_chat);
            rightChatLayout = (LinearLayout) itemView.findViewById(R.id.right_chatLayout);
            rightChat = (TextView) itemView.findViewById(R.id.right_chat);
            timeLeft = (TextView) itemView.findViewById(R.id.time_left);
            timeRight = (TextView) itemView.findViewById(R.id.time_right);
            imgLeft = itemView.findViewById(R.id.img_left);
            imgRight = itemView.findViewById(R.id.img_right);


        }
    }
}
