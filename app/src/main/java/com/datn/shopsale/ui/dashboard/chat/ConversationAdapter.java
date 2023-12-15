package com.datn.shopsale.ui.dashboard.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.response.GetConversationResponse;
import com.datn.shopsale.response.GetMessageResponse;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.PreferenceManager;

import org.jetbrains.annotations.Contract;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private final Context mContext;
    private final ArrayList<GetConversationResponse.Conversation> conversations;
    private final ArrayList<GetMessageResponse.Message> latestMessage;

    public ConversationAdapter(Context mContext, ArrayList<GetConversationResponse.Conversation> conversations, ArrayList<GetMessageResponse.Message> dataLatestMessage) {
        this.mContext = mContext;
        this.conversations = conversations;
        this.latestMessage = dataLatestMessage;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        PreferenceManager preferenceManager = new PreferenceManager(mContext);
        GetConversationResponse.Conversation conversation = conversations.get(position);
        String idUserLoged = preferenceManager.getString("userId");
        String idOtherUser = "";
        String imgOtherUser = "";
        String name = "";
        for (int i = 0; i < conversation.getUser().size(); i++) {
            if (!conversation.getUser().get(i).get_id().equals(idUserLoged)) {
                idOtherUser = conversation.getUser().get(i).get_id();
                imgOtherUser = conversation.getUser().get(i).getAvatar();
                name = conversation.getUser().get(i).getFull_name();
            }
        }

        Glide.with(mContext)
                .load(GetImgIPAddress.convertLocalhostToIpAddress(imgOtherUser))
                .into(holder.imgAvt);

        holder.tvNameUser.setText(name);
        if (latestMessage.size() == conversations.size()) {
            GetMessageResponse.Message message = latestMessage.get(position);
            if (message == null) return;
            String idConversation = message.getConversation().get_id();
            try {
                if (idConversation.equals(conversation.get_id())) {
                    if (message.getStatus().equals("unseen")) {
                        holder.tvLastMessage.setTypeface(holder.tvLastMessage.getTypeface(), Typeface.BOLD);
                        holder.tvTime.setTypeface(holder.tvTime.getTypeface(), Typeface.BOLD);
                    }
                    String encryptedMessage = message.getMessage();
                    String decryptedMessage = decryptMessage(encryptedMessage);
                    String text = "";
                    if (message.getSenderId().equals(idUserLoged)) {
                        if (decryptedMessage.trim().length() > 0) {
                            text = "Bạn: " + decryptedMessage;
                        } else {
                            text = "Bạn đã gửi 1 ảnh";
                        }
                    } else {
                        if (decryptedMessage.trim().length() > 0) {
                            text = decryptedMessage;
                        } else {
                            text = "đã gửi 1 ảnh";
                        }
                    }
                    holder.tvLastMessage.setText(text);
                    String dataTime = message.getTimestamp();
                    dataTime = dataTime.substring(dataTime.length() - 8, dataTime.length() - 3);
                    holder.tvTime.setText(dataTime);
                }
            } catch (Exception e) {
                Log.d("Conversation adapter", "onBindViewHolder: " + e.getMessage());
            }

        } else {
            holder.tvLastMessage.setText("");
            holder.tvTime.setText("");
        }
        String finalIdOtherUser = idOtherUser;
        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(mContext, ChatActivity.class);
            i.putExtra("idConversation", conversation.get_id());
            i.putExtra("idUser", finalIdOtherUser);
            mContext.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvt;
        TextView tvNameUser;
        TextView tvLastMessage;
        TextView tvTime;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvt = (CircleImageView) itemView.findViewById(R.id.img_avt);
            tvNameUser = (TextView) itemView.findViewById(R.id.tv_nameUser);
            tvLastMessage = (TextView) itemView.findViewById(R.id.tv_lastMessage);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);

        }
    }

    @NonNull
    @Contract("_ -> new")
    public static String decryptMessage(@NonNull String encryptedMessage) throws Exception {
        String[] textParts = encryptedMessage.split(":");
        byte[] iv = hexStringToByteArray(textParts[0]);
        byte[] encryptedText = hexStringToByteArray(textParts[1]);

        MessageDigest md = MessageDigest.getInstance(Constants.HASH_ALGORITHM);
        byte[] keyBytes = md.digest(Constants.ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes16 = new byte[16];
        System.arraycopy(keyBytes, 0, keyBytes16, 0, 16);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes16, "AES");

        Cipher cipher = Cipher.getInstance(Constants.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

        byte[] decryptedBytes = cipher.doFinal(encryptedText);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
