package com.datn.shopsale.ui.dashboard.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.datn.shopsale.R;
import com.datn.shopsale.adapter.MessageAdapter;
import com.datn.shopsale.models.ChatRoomModal;
import com.datn.shopsale.models.MessageModel;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ChatActivityFirebase extends AppCompatActivity {
    private RecyclerView rcvChat;
    private LinearLayout idChat;
    private ImageButton btnOption;
    private EditText edChat;
    private ImageButton imgbtnSend;
    private Toolbar toolbar;
    private String chatRoomId;
    private PreferenceManager preferenceManager;
    private ChatRoomModal chatRoomModel;
    private MessageAdapter messageAdapter;
    private List<MessageModel> list;
    private ChatRoomModal objChatRoomModal;
    private List<String> listId;
    private String idOther;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_firebase);
        rcvChat = (RecyclerView) findViewById(R.id.rcv_chat);
        btnOption = (ImageButton) findViewById(R.id.btn_option);
        edChat = (EditText) findViewById(R.id.ed_chat);
        imgbtnSend = (ImageButton) findViewById(R.id.img_btn_send);
        toolbar = findViewById(R.id.toolbar_chat);
        preferenceManager = new PreferenceManager(this);
        list = new ArrayList<>();
        listId = getIntent().getStringArrayListExtra("listId");
        if (listId != null) {
            idOther = Constants.getOtherId(listId, preferenceManager.getString("userId"));
        }
        if (idOther == null) {
            chatRoomId = Constants.getChatRoomId(Constants.idUserAdmin, preferenceManager.getString("userId"));
        } else {
            chatRoomId = Constants.getChatRoomId(Constants.idUserAdmin, idOther);

        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
//        toolbar.setNavigationOnClickListener(v -> {
//            onBackPressed();
//        });
        // tạo room chat
        getOrCreateChatRoomModel();
        // lấy data chat

        getDataMessage();

//
//        if (edChat.getText().toString().trim().isEmpty()){
//            imgbtnSend.setVisibility(View.GONE);
//        }else{
//            imgbtnSend.setVisibility(View.VISIBLE);
//        }
        imgbtnSend.setOnClickListener(view -> {
            sendMessage();
        });
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
            LinearLayout linerFile = dialog.findViewById(R.id.lnl_file);


            linerFile.setOnClickListener(view -> {
                dialog.cancel();
                ImagePicker.with(this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            });
            btnCancel.setOnClickListener(view2 -> {
                dialog.cancel();
            });
            dialog.show();
        });
    }
    private void getDataMessage() {
        Query query = FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId)
                .collection("chats").orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class).build();
        messageAdapter = new MessageAdapter(options, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvChat.setLayoutManager(linearLayoutManager);
        rcvChat.setAdapter(messageAdapter);
        messageAdapter.startListening();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int lastPosition = messageAdapter.getItemCount() - 1;
                    if (lastPosition >= 0) {
                        rcvChat.smoothScrollToPosition(lastPosition);
                    }
                }
            }
        });
    }

    private void sendMessage() {
        Calendar calendar = Calendar.getInstance();

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        calendar.setTimeZone(timeZone);

        long vietnamTimeInMillis = calendar.getTimeInMillis();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(vietnamTimeInMillis);
        Timestamp t = new Timestamp(timestamp);
        String message = edChat.getText().toString().trim();

        if (message.isEmpty()) {
            return;
        } else {
            chatRoomModel.setLastMessageTimestamp(t);
            chatRoomModel.setLastMessage(message);
            chatRoomModel.setLastImage("");
            chatRoomModel.setIdUserOfLastMessage(preferenceManager.getString("userId"));
            FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId).set(chatRoomModel);
            MessageModel messageModel = new MessageModel(message, preferenceManager.getString("userId"), Timestamp.now(), "");
            FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId)
                    .collection("chats").add(messageModel)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            rcvChat.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                        }
                    });
            edChat.setText("");
        }
    }

    private void getOrCreateChatRoomModel() {
        FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            chatRoomModel = task.getResult().toObject(ChatRoomModal.class);
                            if (chatRoomModel == null) {
                                Calendar calendar = Calendar.getInstance();
                                TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
                                calendar.setTimeZone(timeZone);
                                long vietnamTimeInMillis = calendar.getTimeInMillis();
                                java.sql.Timestamp timestamp = new java.sql.Timestamp(vietnamTimeInMillis);
                                Timestamp t = new Timestamp(timestamp);
                                chatRoomModel = new ChatRoomModal(
                                        chatRoomId,
                                        Arrays.asList(Constants.idUserAdmin, preferenceManager.getString("userId")),
                                        t,
                                        "",
                                        "",
                                        preferenceManager.getString("avatarLogin"),
                                        preferenceManager.getString("nameLogin"),
                                        ""

                                );
                                FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId).set(chatRoomModel);
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Calendar calendar = Calendar.getInstance();
            StorageReference mountainsRef = storageRef.child("imageChat" + calendar.getTimeInMillis() + ".png");
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();
                UploadTask uploadTask = mountainsRef.putBytes(bytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivityFirebase.this, "Loi", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Calendar calendar = Calendar.getInstance();
                                TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
                                calendar.setTimeZone(timeZone);
                                long vietnamTimeInMillis = calendar.getTimeInMillis();
                                java.sql.Timestamp timestamp = new java.sql.Timestamp(vietnamTimeInMillis);
                                Timestamp t = new Timestamp(timestamp);
                                chatRoomModel.setLastMessageTimestamp(t);
                                chatRoomModel.setLastMessage("");
                                chatRoomModel.setLastImage(uri.toString());
                                chatRoomModel.setIdUserOfLastMessage(preferenceManager.getString("userId"));
                                FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId).set(chatRoomModel);
                                MessageModel messageModel = new MessageModel("", preferenceManager.getString("userId"), Timestamp.now(), uri.toString());
                                FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId)
                                        .collection("chats").add(messageModel)
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                rcvChat.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                                            }
                                        });
                            }
                        });
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {

        }

    }
}