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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.IActionMessage;
import com.datn.shopsale.R;
import com.datn.shopsale.modelsv2.Message;
import com.datn.shopsale.modelsv2.User;

import java.util.List;

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
            ((SentMessageViewHolder) holder).setData(mMessages.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(mMessages.get(position), userSelected.getAvatar());
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

        void setData(@NonNull Message chat) {
            String contentMsg = chat.getMessage();
            switch (chat.getMessage_type()) {
                case Message.TYPE_SEND_TEXT:
                    tvMessage.setVisibility(View.VISIBLE);
                    imgMsg.setVisibility(View.GONE);
                    tvMessage.setText(contentMsg);
                    break;
                case Message.TYPE_SEND_IMAGE:
                    tvMessage.setVisibility(View.GONE);
                    imgMsg.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(contentMsg)
                            .into(imgMsg);
                    break;
                case Message.TYPE_SEND_VIDEO:
                    tvMessage.setVisibility(View.VISIBLE); // ẩn text
                    imgMsg.setVisibility(View.GONE);
                    tvMessage.setText(R.string.sent_video);
                    break;
            }

            if (!chat.getDeleted_at().isEmpty()) {
                tvMessage.setVisibility(View.VISIBLE);
                imgMsg.setVisibility(View.GONE);
                tvMessage.setTypeface(tvMessage.getTypeface(), Typeface.ITALIC);
                tvMessage.setTextSize(14);
                tvMessage.setTextColor(Color.GRAY);
                tvMessage.setText(contentMsg);
            }


            String dataTime = chat.getCreated_at();
            dataTime = dataTime.substring(dataTime.length() - 8, dataTime.length() - 3);
            tvDateTime.setText(dataTime);

            itemView.setOnLongClickListener(v -> {
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

        void setData(@NonNull Message chat, String avatar) {
            String contentMsg = chat.getMessage();
            switch (chat.getMessage_type()) {
                case Message.TYPE_SEND_TEXT:
                    if (!chat.getDeleted_at().isEmpty()) {
                        tvMessage.setTypeface(tvMessage.getTypeface(), Typeface.ITALIC);
                        tvMessage.setTextSize(14);
                        tvMessage.setTextColor(Color.GRAY);
                    }
                    tvMessage.setText(contentMsg);
                    imgMessage.setVisibility(View.GONE);
                    tvMessage.setVisibility(View.VISIBLE);
                    break;
                case Message.TYPE_SEND_IMAGE:
                    tvMessage.setVisibility(View.GONE);
                    imgMessage.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(contentMsg)
                            .into(imgMessage);
                    break;
                case Message.TYPE_SEND_VIDEO:
                    tvMessage.setVisibility(View.VISIBLE); // ẩn text
                    imgMessage.setVisibility(View.GONE); //ẩn ảnh
                    //  video
                    tvMessage.setText(R.string.sent_video);
                    break;
            }

            String dataTime = chat.getCreated_at();
            dataTime = dataTime.substring(dataTime.length() - 8, dataTime.length() - 3);
            tvDateTime.setText(dataTime);
            if (avatar != null) {
                Glide.with(context)
                        .load(avatar)
                        .into(imgProfile);
            }

            itemView.setOnLongClickListener(v -> {
                Toast.makeText(context, contentMsg, Toast.LENGTH_SHORT).show();
                return false;
            });
        }
    }
}
