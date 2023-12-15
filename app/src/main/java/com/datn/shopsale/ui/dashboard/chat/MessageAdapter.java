package com.datn.shopsale.ui.dashboard.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.R;
import com.datn.shopsale.response.GetMessageResponse;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.GetImgIPAddress;

import org.jetbrains.annotations.Contract;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private final ArrayList<GetMessageResponse.Message> mMessages;
    private final String avatarURL;
    private final String senderId;


    public MessageAdapter(Context context, ArrayList<GetMessageResponse.Message> messages, String senderId, String avatarURL) {
        MessageAdapter.context = context;
        mMessages = messages;
        this.senderId = senderId;
        this.avatarURL = avatarURL;
    }

    public void addMessage(GetMessageResponse.Message message) {
        mMessages.add(message);
        notifyItemInserted(mMessages.size() - 1);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View viewSent = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false);
            return new SentMessageViewHolder(viewSent);
        } else {
            View viewReceived = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false);
            return new ReceivedMessageViewHolder(viewReceived);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            try {
                ((SentMessageViewHolder) holder).setData(mMessages.get(position));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                ((ReceivedMessageViewHolder) holder).setData(mMessages.get(position), avatarURL);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessages.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvDateTime;
        ImageView imgMsg;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDateTime = itemView.findViewById(R.id.tvTime);
            imgMsg = itemView.findViewById(R.id.img_msg);

            itemView.setOnClickListener(v -> {
//                Toast.makeText(v.getContext(), "sent", Toast.LENGTH_SHORT).show();
            });
        }

        void setData(@NonNull GetMessageResponse.Message chat) throws Exception {
            String encryptedMessage = chat.getMessage();
            String decryptedMessage = decryptMessage(encryptedMessage);
            if (decryptedMessage.length() > 0) {
                tvMessage.setVisibility(View.VISIBLE);
                imgMsg.setVisibility(View.GONE);
                tvMessage.setText(decryptedMessage);
            } else {
                tvMessage.setVisibility(View.GONE);
                imgMsg.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(GetImgIPAddress.convertLocalhostToIpAddress(chat.getImages().get(0)))
                        .into(imgMsg);
            }

            String dataTime = chat.getTimestamp();
            dataTime = dataTime.substring(dataTime.length() - 8, dataTime.length() - 3);
            tvDateTime.setText(dataTime);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvDateTime;
        ImageView imgProfile, imgMessage;


        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            imgMessage = itemView.findViewById(R.id.img_msg);
            tvDateTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(v -> {
//                Toast.makeText(v.getContext(), "received", Toast.LENGTH_SHORT).show();
            });
        }

        void setData(@NonNull GetMessageResponse.Message chat, String avatar) throws Exception {
            String encryptedMessage = chat.getMessage();
            String decryptedMessage = decryptMessage(encryptedMessage);
            tvMessage.setText(decryptedMessage);
            tvMessage.setVisibility(View.VISIBLE);
            if (decryptedMessage.trim().length() > 0) {
                tvMessage.setText(decryptedMessage);
                imgMessage.setVisibility(View.GONE);
                tvMessage.setVisibility(View.VISIBLE);
            } else {
                tvMessage.setVisibility(View.GONE);
                imgMessage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(GetImgIPAddress.convertLocalhostToIpAddress(chat.getImages().get(0)))
                        .into(imgMessage);
            }
            String dataTime = chat.getTimestamp();
            dataTime = dataTime.substring(dataTime.length() - 8, dataTime.length() - 3);
            tvDateTime.setText(dataTime);
            if (avatar != null) {
                Glide.with(context)
                        .load(GetImgIPAddress.convertLocalhostToIpAddress(avatar))
                        .into(imgProfile);
            }
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
