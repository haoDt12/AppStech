package com.datn.shopsale.ui.dashboard.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.Interface.IActionMessage;
import com.datn.shopsale.R;
import com.datn.shopsale.modelsv2.Conversation;
import com.datn.shopsale.modelsv2.User;
import com.datn.shopsale.responsev2.GetListConversationResponse;
import com.datn.shopsale.responsev2.MessageResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationActivity extends AppCompatActivity implements IActionMessage {
    private static final String TAG = ConversationActivity.class.getSimpleName();

    private Toolbar toolbarConversation;
    private RecyclerView rcvConversation;
    private PreferenceManager preferenceManager;

    private ApiService apiService;
    private ConversationAdapter conversationAdapter;
    private List<Conversation> dataConversation;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(Constants.URL_API);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        initView();


        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("on-chat", onNewMessage);
        mSocket.on("user-chat", onUserChat);

        mSocket.connect();

        apiService = RetrofitConnection.getApiService();
        setSupportActionBar(toolbarConversation);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarConversation.setNavigationOnClickListener(v -> onBackPressed());

        getDataConversation();

    }

    void initView() {
        toolbarConversation = findViewById(R.id.toolbar_conversation);
        rcvConversation = findViewById(R.id.rcv_conversation);
        preferenceManager = new PreferenceManager(ConversationActivity.this);
    }

    private final Emitter.Listener onConnect = args -> runOnUiThread(() -> Log.d(TAG, "run: " + R.string.connect));

    private final Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        Toast.makeText(ConversationActivity.this, "onNewMessage: ", Toast.LENGTH_SHORT).show();
        String message;
        try {
            message = data.getString("message");
            Log.d(TAG, "onNewMessage: " + message);
        } catch (JSONException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    });

    private final Emitter.Listener onUserChat = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        // new message
        Log.d(TAG, "onUserChat: " + data);
        getDataConversation();
    });

    private void displayConversation(@NonNull List<Conversation> dataConversation) {
        Log.d(TAG, "displayConversation: " + dataConversation.get(0).toString());
        conversationAdapter = new ConversationAdapter(ConversationActivity.this, dataConversation, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ConversationActivity.this);
        rcvConversation.setLayoutManager(linearLayoutManager);
        rcvConversation.setAdapter(conversationAdapter);
    }


    private void getDataConversation() {
        LoadingDialog.showProgressDialog(ConversationActivity.this, "Loading...");
        dataConversation = new ArrayList<>();
        String token = preferenceManager.getString("token");
        String idUser = preferenceManager.getString("userId");
        if (token.length() == 0 || idUser.length() == 0) {
            AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, "token or idUser null");
        } else {
            Call<GetListConversationResponse> call = apiService.getConversationByIDUser(token, idUser);
            call.enqueue(new Callback<GetListConversationResponse>() {
                @Override
                public void onResponse(@NonNull Call<GetListConversationResponse> call, @NonNull Response<GetListConversationResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
                                dataConversation = response.body().getConversations();
                                if (dataConversation.size() == 0) {
                                    AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, getResources().getString(R.string.chua_co_cuoc_hoi_thoai));
                                } else {
                                    displayConversation(dataConversation);
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                if (response.body().getMessage().equals("wrong token")) {
                                    CheckLoginUtil.gotoLogin(ConversationActivity.this, response.body().getMessage());
                                } else {
                                    AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, response.body().getMessage());
                                }
                            });
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, "error get data conversation");
                    }
                    LoadingDialog.dismissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<GetListConversationResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, t.getMessage());
                    });
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off("user-chat", onUserChat);
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off("on-chat", onNewMessage);

    }

    private void doGoScreenChat(Conversation conversation) {
        Intent i = new Intent(ConversationActivity.this, ChatActivity.class);
        User user = new User();
        user.set_id(conversation.getCreator_id());
        user.setEmail(conversation.getEmail());
        user.setAvatar(conversation.getAvatar());
        user.setFull_name(conversation.getName());
        user.setPhone_number(conversation.getPhone());
        user.setStatus(conversation.getStatus());

        i.putExtra("idConversation", conversation.getConversation_id());
        i.putExtra("idMsg", conversation.getIdMsg());
        i.putExtra("dataUser", user);
        startActivity(i);
    }

    private void updateStatusMessage(String msgID, String status, Conversation conversation) {
        if (msgID.length() == 0) {
            doGoScreenChat(conversation);
        } else {
            LoadingDialog.showProgressDialog(ConversationActivity.this, "Loading...");
            String token = preferenceManager.getString("token");
            Call<MessageResponse> call = apiService.updateStatusMessage(token, msgID, status);
            call.enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
                                conversationAdapter.updateStatusMessage(conversation.getConversation_id());
                                doGoScreenChat(conversation);
                            });
                        } else {
                            runOnUiThread(() -> {
                                if (response.body().getMessage().equals("wrong token")) {
                                    CheckLoginUtil.gotoLogin(ConversationActivity.this, response.body().getMessage());
                                } else {
                                    AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, response.body().getMessage());
                                }
                            });
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, "error get data conversation");
                    }
                    LoadingDialog.dismissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, t.getMessage());
                    });
                }
            });
        }

    }

    @Override
    public void doAction(String typeAction, String msgID, String status, Conversation conversation) {
        if (typeAction.equals("UPDATE_STATUS")) {
            updateStatusMessage(msgID, status, conversation);
        }
    }
}