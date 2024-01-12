package com.datn.shopsale.ui.dashboard.chat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.IActionMessage;
import com.datn.shopsale.R;
import com.datn.shopsale.modelsv2.Message;
import com.datn.shopsale.modelsv2.User;
import com.datn.shopsale.response.GetMessageResponse;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.jetbrains.annotations.Contract;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private final List<Message> mMessages;
    private final User userSelected;
    private static IActionMessage iActionMessage;

    public MessageAdapter(Context context, List<Message> messages, User userSelected, IActionMessage iActionMessage) {
        MessageAdapter.context = context;
        mMessages = messages;
        this.userSelected = userSelected;
        MessageAdapter.iActionMessage = iActionMessage;
    }

    public void addMessage(Message message) {
        mMessages.add(message);
        notifyItemInserted(mMessages.size() - 1);
    }

    public void updateMessage(String messageID) {
        for (int i = 0; i < mMessages.size(); i++) {
            if (mMessages.get(i).get_id().equals(messageID)) {
                notifyItemChanged(i);
            }
        }
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
                ((ReceivedMessageViewHolder) holder).setData(mMessages.get(position), userSelected.getAvatar());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessages.get(position).getSender_id().equals(userSelected.get_id())) {
            return VIEW_TYPE_RECEIVED;
        } else {
            return VIEW_TYPE_SENT;
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

        }

        void setData(@NonNull Message chat) throws Exception {
            String encryptedMessage = chat.getMessage();
            String decryptedMessage = decryptMessage(encryptedMessage);
            if (decryptedMessage.length() > 0) {
                tvMessage.setVisibility(View.VISIBLE);
                imgMsg.setVisibility(View.GONE);
                if (!chat.getDeleted_at().isEmpty()) {
                    tvMessage.setTypeface(tvMessage.getTypeface(), Typeface.ITALIC);
                    tvMessage.setTextSize(14);
                    tvMessage.setTextColor(Color.GRAY);
                    tvMessage.setText(R.string.message_removed);
                } else {
                    tvMessage.setText(decryptedMessage);
                }

            } else {
                tvMessage.setVisibility(View.GONE);
                imgMsg.setVisibility(View.VISIBLE);
                // send image
//                Glide.with(context)
//                        .load(GetImgIPAddress.convertLocalhostToIpAddress(chat.getImages().get(0)))
//                        .into(imgMsg);
            }

            String dataTime = chat.getCreated_at();
            dataTime = dataTime.substring(dataTime.length() - 8, dataTime.length() - 3);
            tvDateTime.setText(dataTime);

            itemView.setOnLongClickListener(v -> {
                if (!chat.getDeleted_at().isEmpty()) {
                    Toast.makeText(context, chat.getDeleted_at(), Toast.LENGTH_SHORT).show();
                }

//                Toast.makeText(context, chat.getDeleted_at().length() + "", Toast.LENGTH_SHORT).show();
                doActionMessage(chat.get_id());
                return false;
            });

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private static void doActionMessage(String msgID) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirm_delete_msg);
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(context.getDrawable(R.drawable.dialog_bg));
        window.getAttributes().windowAnimations = R.style.DialogAnimationOption;
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        window.setAttributes(windowAttributes);
        windowAttributes.gravity = Gravity.BOTTOM;
        ImageButton btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
        btnCancel.setOnClickListener(view2 -> dialog.cancel());
        btnConfirm.setOnClickListener(view2 -> {
            dialog.dismiss();
            iActionMessage.doAction("DELETE", msgID, "", null);
        });

        dialog.show();
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
        }

        void setData(@NonNull Message chat, String avatar) throws Exception {
            String encryptedMessage = chat.getMessage();
            String decryptedMessage = decryptMessage(encryptedMessage);
            tvMessage.setText(decryptedMessage);
            tvMessage.setVisibility(View.VISIBLE);

            if (decryptedMessage.trim().length() > 0) {
                if (!chat.getDeleted_at().isEmpty()) {
                    tvMessage.setTypeface(tvMessage.getTypeface(), Typeface.ITALIC);
                    tvMessage.setTextSize(14);
                    tvMessage.setTextColor(Color.GRAY);
                    tvMessage.setText(R.string.message_removed);
                } else {
                    tvMessage.setText(decryptedMessage);
                }
                imgMessage.setVisibility(View.GONE);
                tvMessage.setVisibility(View.VISIBLE);
            } else {
                tvMessage.setVisibility(View.GONE);
                imgMessage.setVisibility(View.VISIBLE);
                // Send Image
//                Glide.with(context)
//                        .load(GetImgIPAddress.convertLocalhostToIpAddress(chat.getImages().get(0)))
//                        .into(imgMessage);
            }
            String dataTime = chat.getCreated_at();
            dataTime = dataTime.substring(dataTime.length() - 8, dataTime.length() - 3);
            tvDateTime.setText(dataTime);
            if (avatar != null) {
                Glide.with(context)
                        .load(GetImgIPAddress.convertLocalhostToIpAddress(avatar))
                        .into(imgProfile);
            }

            itemView.setOnLongClickListener(v -> {
                Toast.makeText(context, decryptedMessage, Toast.LENGTH_SHORT).show();
                return false;
            });
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
