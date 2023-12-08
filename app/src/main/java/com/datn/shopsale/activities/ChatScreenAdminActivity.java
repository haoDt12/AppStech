package com.datn.shopsale.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.datn.shopsale.R;
import com.datn.shopsale.adapter.ConversationAdapter;
import com.datn.shopsale.adapter.MessageAdapter;
import com.datn.shopsale.models.ChatRoomModal;
import com.datn.shopsale.models.MessageModel;
import com.datn.shopsale.utils.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatScreenAdminActivity extends AppCompatActivity {
    private Toolbar toolbarConversation;
    private RecyclerView rcvConversation;
    private PreferenceManager preferenceManager;
    private ConversationAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen_admin);
        initUi();
        setSupportActionBar(toolbarConversation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarConversation.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        getDataConversation();
    }


    void initUi(){
        toolbarConversation =  findViewById(R.id.toolbar_Conversation);
        rcvConversation = (RecyclerView) findViewById(R.id.rcv_Conversation);
        preferenceManager  = new PreferenceManager(this);
    }
    void getDataConversation(){
        Query query = FirebaseFirestore.getInstance().collection("chatrooms").orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatRoomModal> options = new FirestoreRecyclerOptions.Builder<ChatRoomModal>()
                .setQuery(query, ChatRoomModal.class).build();
        adapter = new ConversationAdapter(options,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvConversation.setLayoutManager(linearLayoutManager);
        rcvConversation.setAdapter(adapter);
        adapter.startListening();
    }
}