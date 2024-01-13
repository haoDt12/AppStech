package com.datn.shopsale.ui.dashboard.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.IActionMessage;
import com.datn.shopsale.R;
import com.datn.shopsale.modelsv2.Conversation;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.PreferenceManager;

import org.jetbrains.annotations.Contract;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private final Context mContext;
    private final List<Conversation> conversations;
    private final IActionMessage iActionMessage;

    public ConversationAdapter(Context mContext, List<Conversation> conversations, IActionMessage iActionMessage) {
        this.mContext = mContext;
        this.conversations = conversations;
        this.iActionMessage = iActionMessage;
    }

    public void updateStatusMessage(String idConversation) {
        for (int i = 0; i < conversations.size(); i++) {
            if (conversations.get(i).getConversation_id().equals(idConversation)) {
                notifyItemChanged(i);
            }
        }
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {

        PreferenceManager preferenceManager = new PreferenceManager(mContext);
        String idUserLogged = preferenceManager.getString("userId");

        Conversation conversation = conversations.get(position);
        Glide.with(mContext)
                .load(conversation.getAvatar())
                .into(holder.imgAvt);
        holder.tvNameUser.setText(conversation.getName());
        if (conversation.getStatus().equals("unseen")) {
            holder.tvLastMessage.setTypeface(holder.tvLastMessage.getTypeface(), Typeface.BOLD);
            holder.tvTime.setTypeface(holder.tvTime.getTypeface(), Typeface.BOLD);
        }
        String contentMsg = conversation.getMessage();
        if (!conversation.getMsg_deleted_at().isEmpty()) {
            holder.tvLastMessage.setTypeface(holder.tvLastMessage.getTypeface(), Typeface.ITALIC);
            holder.tvLastMessage.setTextSize(14);
            holder.tvLastMessage.setTextColor(Color.GRAY);
        }
        if (idUserLogged.equals(conversation.getSender_id())) {
            holder.tvLastMessage.setText("Bạn: " + contentMsg);
        } else {
            holder.tvLastMessage.setText(contentMsg);
        }

        if (contentMsg.length() > 0) {
            holder.layoutMsg.setVisibility(View.VISIBLE);
            String dataTime = conversation.getCreated_at();
            dataTime = dataTime.substring(dataTime.length() - 8, dataTime.length() - 3);
            holder.tvTime.setText(dataTime);
        }

        holder.itemView.setOnClickListener(v -> iActionMessage.doAction("UPDATE_STATUS", conversation.getIdMsg(), conversation.getStatus(), conversation));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvt;
        RelativeLayout layoutMsg;
        TextView tvNameUser, tvLastMessage, tvTime;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvt = (CircleImageView) itemView.findViewById(R.id.img_avt);
            tvNameUser = (TextView) itemView.findViewById(R.id.tv_nameUser);
            layoutMsg = (RelativeLayout) itemView.findViewById(R.id.layout_msg);
            tvLastMessage = (TextView) itemView.findViewById(R.id.tv_lastMessage);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
