package com.datn.shopsale.ui.dashboard.chat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.Interface.LatestMessageCallback;
import com.datn.shopsale.R;
import com.datn.shopsale.response.GetConversationResponse;
import com.datn.shopsale.response.GetMessageResponse;
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
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationActivity extends AppCompatActivity {
    private static final String TAG = ConversationActivity.class.getSimpleName();

    private Toolbar toolbarConversation;
    private RecyclerView rcvConversation;
    private PreferenceManager preferenceManager;

    private ApiService apiService;
    private ArrayList<GetConversationResponse.Conversation> dataConversation;
    private ArrayList<GetMessageResponse.Message> dataMessage;
    private ConversationAdapter conversationAdapter;
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

        getDataConversation(true);

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
        } catch (JSONException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    });

    private final Emitter.Listener onUserChat = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        try {
            String idConversation = data.getString("conversation");
            getDataConversation(false);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    });

    private void displayConversation(@NonNull ArrayList<GetConversationResponse.Conversation> dataConversation, boolean isLoad) {

        ArrayList<String> listIdConversation = new ArrayList<>();
        for (GetConversationResponse.Conversation conversation : dataConversation) {
            listIdConversation.add(conversation.get_id());
        }
        getDataLatestMessage(listIdConversation, isLoad, dataMessage -> {
            conversationAdapter = new ConversationAdapter(ConversationActivity.this, dataConversation, dataMessage);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ConversationActivity.this);
            rcvConversation.setLayoutManager(linearLayoutManager);
            rcvConversation.setAdapter(conversationAdapter);
        });

    }

    private void getDataLatestMessage(ArrayList<String> idConversation, boolean isLoad, LatestMessageCallback callback) {
        if (isLoad) {
            LoadingDialog.showProgressDialog(ConversationActivity.this, "Loading...");
        }

        dataMessage = new ArrayList<>();
        String token = preferenceManager.getString("token");
        String idUser = preferenceManager.getString("userId");
        if (token.length() == 0 || idUser.length() == 0) {
            AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, "token or idUser null");
        } else {
            Call<GetMessageResponse.Root> call = apiService.getMessageLatest(token, idConversation);
            call.enqueue(new Callback<GetMessageResponse.Root>() {
                @Override
                public void onResponse(@NonNull Call<GetMessageResponse.Root> call, @NonNull Response<GetMessageResponse.Root> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
                                dataMessage = response.body().getDataMessage();
                                if (dataConversation.size() > 0) {
                                    callback.onLatestMessageLoaded(dataMessage);
                                } else {
                                    callback.onLatestMessageLoaded(null);
                                }

                            });
                        } else {
                            runOnUiThread(() -> {
                                if(response.body().getMessage().equals("wrong token")){
                                    CheckLoginUtil.gotoLogin(ConversationActivity.this,response.body().getMessage());
                                }else {
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
                public void onFailure(@NonNull Call<GetMessageResponse.Root> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, t.getMessage());
                    });
                }
            });
        }
    }

    private void getDataConversation(boolean isLoad) {
        if (isLoad) {
            LoadingDialog.showProgressDialog(ConversationActivity.this, "Loading...");
        }
        dataConversation = new ArrayList<>();
        String token = preferenceManager.getString("token");
        String idUser = preferenceManager.getString("userId");
        if (token.length() == 0 || idUser.length() == 0) {
            AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, "token or idUser null");
        } else {
            Call<GetConversationResponse.Root> call = apiService.getConversationByIDUser(token, idUser);
            call.enqueue(new Callback<GetConversationResponse.Root>() {
                @Override
                public void onResponse(@NonNull Call<GetConversationResponse.Root> call, @NonNull Response<GetConversationResponse.Root> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
                                dataConversation = response.body().getConversation();
                                if (dataConversation.size() == 0) {
                                    AlertDialogUtil.showAlertDialogWithOk(ConversationActivity.this, getResources().getString(R.string.chua_co_cuoc_hoi_thoai));
                                } else {
                                    displayConversation(dataConversation, isLoad);
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
                public void onFailure(@NonNull Call<GetConversationResponse.Root> call, @NonNull Throwable t) {
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
}