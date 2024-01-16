package com.datn.shopsale.ui.dashboard.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.Interface.IActionMessage;
import com.datn.shopsale.R;
import com.datn.shopsale.modelsv2.Conversation;
import com.datn.shopsale.modelsv2.Message;
import com.datn.shopsale.modelsv2.User;
import com.datn.shopsale.responsev2.BaseResponse;
import com.datn.shopsale.responsev2.GetListMessageResponse;
import com.datn.shopsale.responsev2.MessageResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements IActionMessage {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ImageView imgAvatar;
    private TextView tvName;
    private PreferenceManager preferenceManager;

    private ApiService apiService;
    private Message mMessages;
    private MessageAdapter mAdapter;
    private RecyclerView recyclerViewChat;


    private static final int TYPING_TIMER_LENGTH = 600;
    private EditText inputMessage;
    private boolean isTyping = false;

    private final Handler mTypingHandler = new Handler();
    private String mUsername;
    private User mUserSelected;
    private String conversationID;
    private String idUserLog;
    private Uri imageUri;

    private Boolean isConnected = true;

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(Constants.URL_API);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
            leave();
        });

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("on-chat", onNewMessage);
        mSocket.on("user-chat", onUserChat);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
//        mSocket.on("user-update-chat", updateChat);
        mSocket.connect();

        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        idUserLog = preferenceManager.getString("userId");

        conversationID = getIntent().getStringExtra("idConversation");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            User user = (User) getIntent().getSerializableExtra("dataUser");
            if (user != null) {
                displayUserSelected(user);
                getDataMessage(conversationID, user);
            }
        }

        ImageButton btnOption = findViewById(R.id.img_option);

        btnOption.setOnClickListener(view1 -> {
            Dialog dialog = new Dialog(view1.getContext());
            dialog.setContentView(R.layout.dialog_option_chat);
            Window window = dialog.getWindow();
            assert window != null;
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
            btnCancel.setOnClickListener(view2 -> dialog.cancel());
            dialog.show();
        });

        inputMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollToBottom();
            }
        });

        inputMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == R.id.send || actionId == EditorInfo.IME_NULL) {
                attemptSend();
                return true;
            }
            return false;
        });

        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                scrollToBottom();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == idUserLog) return;
                if (!mSocket.connected()) return;

                if (!isTyping) {
                    isTyping = true;
                    mSocket.emit("typing");
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageButton sendButton = findViewById(R.id.img_send);
        sendButton.setOnClickListener(v -> attemptSend());

    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_chat);
        inputMessage = findViewById(R.id.message_input);
        recyclerViewChat = findViewById(R.id.rcv_chat);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        imgAvatar = toolbar.findViewById(R.id.img_avatar);
        tvName = toolbar.findViewById(R.id.tv_name);

    }

    private void addParticipantsLog(int numUsers) {
        Log.d(TAG, "addParticipantsLog: " + numUsers);
//        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
    }


    private void addMessage(String message) {
        String token = preferenceManager.getString("token");
        RequestBody rbConversationID = RequestBody.create(MediaType.parse("text/plain"), conversationID);
        RequestBody rbSenderID = RequestBody.create(MediaType.parse("text/plain"), idUserLog);
        RequestBody rbMessage = RequestBody.create(MediaType.parse("text/plain"), message);

        // Send Image
        if (imageUri != null) {
            File file = new File(Objects.requireNonNull(imageUri.getPath()));
            RequestBody rbMessageType = RequestBody.create(MediaType.parse("text/plain"), Message.TYPE_SEND_IMAGE);
            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("images", file.getName(), imageRequestBody);
            Call<MessageResponse> call = apiService.addMessage(token, rbConversationID, rbSenderID, rbMessageType, rbMessage, imagePart, null);
            call.enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
//                                Log.d(TAG, "onResponse: " + response.body().getMessages());
                                mMessages = response.body().getMessages();
                                JSONObject messageAdded = new JSONObject();
                                try {
                                    messageAdded.put("conversation_id", mMessages.getConversation_id());
                                    messageAdded.put("sender_id", mMessages.getSender_id());
                                    messageAdded.put("message_type", mMessages.getMessage_type());
                                    messageAdded.put("message", mMessages.getMessage());
                                    messageAdded.put("status", mMessages.getStatus());
                                    messageAdded.put("created_at", mMessages.getCreated_at());
                                    messageAdded.put("deleted_at", mMessages.getDeleted_at());
                                    messageAdded.put("_id", mMessages.get_id());
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                JSONObject jsonMessage = new JSONObject();
                                try {
                                    imageUri = null;
                                    jsonMessage.put("message", messageAdded);
                                    mSocket.emit("on-chat", jsonMessage);
                                    mSocket.emit("user-chat", mMessages.getMessage());
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                scrollToBottom();
                            });
                        } else {
                            runOnUiThread(() -> {
                                if (response.body().getMessage().equals("wrong token")) {
                                    CheckLoginUtil.gotoLogin(ChatActivity.this, response.body().getMessage());
                                } else {
                                    AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, response.body().getMessage());
                                }
                            });
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, "error get data message");
                    }
                    LoadingDialog.dismissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        Log.d(TAG, "onFailure add message image: " + t.getMessage());
                        AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, t.getMessage());
                    });
                }
            });

        }
        // Send text
        else {
            RequestBody rbMessageType = RequestBody.create(MediaType.parse("text/plain"), Message.TYPE_SEND_TEXT);
            Call<MessageResponse> call = apiService.addMessage(token, rbConversationID, rbSenderID, rbMessageType, rbMessage, null, null);
            call.enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
                                mMessages = response.body().getMessages();
                                JSONObject messageAdded = new JSONObject();
                                try {
                                    messageAdded.put("conversation_id", mMessages.getConversation_id());
                                    messageAdded.put("sender_id", mMessages.getSender_id());
                                    messageAdded.put("message_type", mMessages.getMessage_type());
                                    messageAdded.put("message", mMessages.getMessage());
                                    messageAdded.put("status", mMessages.getStatus());
                                    messageAdded.put("created_at", mMessages.getCreated_at());
                                    messageAdded.put("deleted_at", mMessages.getDeleted_at());
                                    messageAdded.put("_id", mMessages.get_id());
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                JSONObject jsonMessage = new JSONObject();
                                try {
                                    jsonMessage.put("message", messageAdded);
                                    mSocket.emit("on-chat", jsonMessage);
                                    mSocket.emit("user-chat", mMessages.getMessage());
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                scrollToBottom();
                            });
                        } else {
                            runOnUiThread(() -> {
                                if (response.body().getMessage().equals("wrong token")) {
                                    CheckLoginUtil.gotoLogin(ChatActivity.this, response.body().getMessage());
                                } else {
                                    AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, response.body().getMessage());
                                }
                            });
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, "error get data message");
                    }
                    LoadingDialog.dismissProgressDialog();
                }

                @Override
                public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        Log.d(TAG, "onFailure add message text: " + t.getMessage());
                        AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, t.getMessage());
                    });
                }
            });
        }
    }

    private void attemptSend() {
        if (!mSocket.connected()) return;
        isTyping = false;
        String message = inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            inputMessage.requestFocus();
            return;
        }
        inputMessage.setText("");
        addMessage(message);
    }


    private void leave() {
        mUsername = null;
        mSocket.disconnect();
        mSocket.connect();
    }

    private void scrollToBottom() {
        recyclerViewChat.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private final Emitter.Listener updateChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                Log.d(TAG, "call: " + data);
            });

        }
    };

    private final Emitter.Listener onUserChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                try {
                    Message newMessage = new Message();
                    newMessage.setConversation_id(data.getString("conversation_id"));
                    newMessage.setSender_id(data.getString("sender_id"));
                    newMessage.setMessage(data.getString("message"));
                    newMessage.setMessage_type(data.getString("message_type"));
                    newMessage.setStatus(data.getString("status"));
                    newMessage.setCreated_at(data.getString("created_at"));
                    newMessage.setDeleted_at(data.getString("deleted_at"));
                    newMessage.set_id(data.getString("_id"));

                    mAdapter.addMessage(newMessage);
                    scrollToBottom();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    };

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                if (!isConnected) {
                    if (null != mUsername)
                        mSocket.emit("add user", mUsername);
                    Toast.makeText(getApplicationContext(),
                            R.string.connect, Toast.LENGTH_LONG).show();
                    isConnected = true;
                }
            });
        }
    };
    private final Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        Log.d(TAG, "run on new message: " + data);
        Toast.makeText(ChatActivity.this, "onNewMessage: ", Toast.LENGTH_SHORT).show();
        String username;
        String message;
        try {
            username = data.getString("username");
            message = data.getString("message");
        } catch (JSONException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    });

    private final Emitter.Listener onUserJoined = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        String username;
        int numUsers;
        try {
            username = data.getString("username");
            numUsers = data.getInt("numUsers");
        } catch (JSONException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            return;
        }
        addParticipantsLog(numUsers);
    });

    private final Emitter.Listener onUserLeft = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        String username;
        int numUsers;
        try {
            username = data.getString("username");
            numUsers = data.getInt("numUsers");
        } catch (JSONException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            return;
        }
        addParticipantsLog(numUsers);
    });

    private final Emitter.Listener onTyping = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        String username;
        try {
            username = data.getString("username");
        } catch (JSONException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    });


    private final Emitter.Listener onStopTyping = args -> runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        String username;
        try {
            username = data.getString("username");
        } catch (JSONException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    });

    private final Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!isTyping) return;
            isTyping = false;
            mSocket.emit("stop typing");
        }
    };

    private void displayUserSelected(@NonNull User dataUser) {
        tvName.setText(dataUser.getFull_name());
        Glide.with(this)
                .load(dataUser.getAvatar())
                .into(imgAvatar);
    }

    private void displayMessage(List<Message> dataMessage, User userSelected) {
        mUserSelected = userSelected;
        mAdapter = new MessageAdapter(ChatActivity.this, dataMessage, userSelected, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(linearLayoutManager);
        recyclerViewChat.setAdapter(mAdapter);
        scrollToBottom();
    }

    private void getDataMessage(String conversationID, User dataUserSelected) {
        LoadingDialog.showProgressDialog(ChatActivity.this, "Loading...");
        String token = preferenceManager.getString("token");
        Call<GetListMessageResponse> call = apiService.getMessageByIDConversation(token, conversationID);
        call.enqueue(new Callback<GetListMessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetListMessageResponse> call, @NonNull Response<GetListMessageResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        runOnUiThread(() -> displayMessage(response.body().getMessages(), dataUserSelected));
                    } else {
                        runOnUiThread(() -> {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(ChatActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, response.body().getMessage());
                            }
                        });
                    }
                } else {
                    AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, "error get data conversation");
                }
                LoadingDialog.dismissProgressDialog();
            }

            @Override
            public void onFailure(@NonNull Call<GetListMessageResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, t.getMessage());
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                addMessage("");
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, "da" + ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();

        mSocket.off("user-chat", onUserChat);
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off("on-chat", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
    }

    private void deleteMessage(String msgID) {
        LoadingDialog.showProgressDialog(ChatActivity.this, "Loading...");
        String token = preferenceManager.getString("token");
        String idUser = preferenceManager.getString("userId");
        Call<BaseResponse> call = apiService.deleteMessage(token, idUser, msgID);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        runOnUiThread(() -> {
                            // re-render message deleted
                            mAdapter.updateMessage(response.body().getMessage());
//                            mSocket.emit("user-update-chat", mMessages.getMessage());
                            getDataMessage(conversationID, mUserSelected);
                        });
                    } else {
                        runOnUiThread(() -> {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(ChatActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, response.body().getMessage());
                            }
                        });
                    }
                } else {
                    AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, "error get data conversation");
                }
                LoadingDialog.dismissProgressDialog();
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, t.getMessage());
                });
            }
        });
    }

    @Override
    public void doAction(String typeAction, String msgID, String status, Conversation conversation) {
        if (typeAction.equals("DELETE")) {
            deleteMessage(msgID);
        }
    }
}