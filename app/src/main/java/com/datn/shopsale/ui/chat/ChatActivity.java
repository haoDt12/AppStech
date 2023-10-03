package com.datn.shopsale.ui.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.datn.shopsale.R;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rcvChat;
    private LinearLayout idChat;
    private ImageButton btnOption;
    private EditText edChat;
    private ImageButton imgbtnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rcvChat = (RecyclerView) findViewById(R.id.rcv_chat);
        idChat = (LinearLayout) findViewById(R.id.id_chat);
        btnOption = (ImageButton) findViewById(R.id.btn_option);
        edChat = (EditText) findViewById(R.id.ed_chat);
        imgbtnSend = (ImageButton) findViewById(R.id.imgbtn_send);
        btnOption.setOnClickListener(view1 -> {
            Dialog dialog = new Dialog(view1.getContext());
            dialog.setContentView(R.layout.dialog_option_chat);
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(view1.getContext().getDrawable(R.drawable.dialog_bg));
            window.getAttributes().windowAnimations = R.style.DialogAnimationOption;
            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            window.setAttributes(windowAttributes);
            windowAttributes.gravity = Gravity.BOTTOM;
            ImageButton btnCancel = dialog.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(view2 -> {
                dialog.cancel();
            });
            dialog.show();
        });
    }
}