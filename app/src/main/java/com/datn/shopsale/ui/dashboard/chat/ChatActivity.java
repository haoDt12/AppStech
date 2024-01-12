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
import com.datn.shopsale.R;
import com.datn.shopsale.response.GetMessageResponse;
import com.datn.shopsale.response.GetUserByIdResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ImageView imgAvatar;
    private TextView tvName;
    private PreferenceManager preferenceManager;

    private ApiService apiService;
    private GetMessageResponse.MessageAdded mMessages;
    private MessageAdapter mAdapter;
    private RecyclerView recyclerViewChat;


    private static final int TYPING_TIMER_LENGTH = 600;
    private EditText inputMessage;
    private boolean isTyping = false;

    private final Handler mTypingHandler = new Handler();
    private String mUsername;
    private String avatarUser;
    private String conversationID;
    private String idUserLog;
    private String idUserSelected;
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
        mSocket.connect();

        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        idUserLog = preferenceManager.getString("userId");
        conversationID = getIntent().getStringExtra("idConversation");
        idUserSelected = getIntent().getStringExtra("idUser");
        getDataUserSelected(idUserSelected);

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

    private void addMessage(String message) {

        String token = preferenceManager.getString("token");
        RequestBody requestBodyConversation = RequestBody.create(MediaType.parse("text/plain"), conversationID);
        RequestBody requestBodySenderId = RequestBody.create(MediaType.parse("text/plain"), idUserLog);
        RequestBody requestBodyReceiverId = RequestBody.create(MediaType.parse("text/plain"), idUserSelected);
        RequestBody requestBodyMessage = RequestBody.create(MediaType.parse("text/plain"), message);
        if (imageUri != null) {
            File file = new File(Objects.requireNonNull(imageUri.getPath()));
            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("images", file.getName(), imageRequestBody);
            Call<GetMessageResponse.ResponseMessage> call = apiService.addMessage(token, requestBodyConversation, requestBodySenderId, requestBodyReceiverId, requestBodyMessage, imagePart, null);
            call.enqueue(new Callback<GetMessageResponse.ResponseMessage>() {
                @Override
                public void onResponse(@NonNull Call<GetMessageResponse.ResponseMessage> call, @NonNull Response<GetMessageResponse.ResponseMessage> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
                                mMessages = response.body().getDataMessage();
                                JSONObject messageAdded = new JSONObject();
                                try {
                                    messageAdded.put("conversation", mMessages.getConversation());
                                    messageAdded.put("senderId", mMessages.getSenderId());
                                    messageAdded.put("receiverId", mMessages.getReceiverId());
                                    messageAdded.put("message", mMessages.getMessage());
                                    messageAdded.put("filess", mMessages.getFiless());
                                    messageAdded.put("images", mMessages.getImages());
                                    messageAdded.put("video", mMessages.getVideo());
                                    messageAdded.put("status", mMessages.getStatus());
                                    messageAdded.put("deleted", mMessages.isDeleted());
                                    messageAdded.put("timestamp", mMessages.getTimestamp());
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
                                if(response.body().getMessage().equals("wrong token")){
                                    CheckLoginUtil.gotoLogin(ChatActivity.this,response.body().getMessage());
                                }else {
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
                public void onFailure(@NonNull Call<GetMessageResponse.ResponseMessage> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, t.getMessage());
                    });
                }
            });
        } else {
            Call<GetMessageResponse.ResponseMessage> call = apiService.addMessage(
                    token, requestBodyConversation, requestBodySenderId,
                    requestBodyReceiverId, requestBodyMessage, null, null
            );
            call.enqueue(new Callback<GetMessageResponse.ResponseMessage>() {
                @Override
                public void onResponse(@NonNull Call<GetMessageResponse.ResponseMessage> call, @NonNull Response<GetMessageResponse.ResponseMessage> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
                                mMessages = response.body().getDataMessage();
                                JSONObject messageAdded = new JSONObject();
                                try {
                                    messageAdded.put("conversation", mMessages.getConversation());
                                    messageAdded.put("senderId", mMessages.getSenderId());
                                    messageAdded.put("receiverId", mMessages.getReceiverId());
                                    messageAdded.put("message", mMessages.getMessage());
                                    messageAdded.put("filess", mMessages.getFiless());
                                    messageAdded.put("images", mMessages.getImages());
                                    messageAdded.put("video", mMessages.getVideo());
                                    messageAdded.put("status", mMessages.getStatus());
                                    messageAdded.put("deleted", mMessages.isDeleted());
                                    messageAdded.put("timestamp", mMessages.getTimestamp());
                                    messageAdded.put("_id", mMessages.get_id());
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                JSONObject jsonMessage = new JSONObject();
                                try {
                                    jsonMessage.put("message", messageAdded);
                                    mSocket.emit("on-chat", jsonMessage);
                                    mSocket.emit("user-chat", mMessages.getMessage());
                                    scrollToBottom();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                            });
                        } else {
                            runOnUiThread(() -> {
                                if(response.body().getMessage().equals("wrong token")){
                                    CheckLoginUtil.gotoLogin(ChatActivity.this,response.body().getMessage());
                                }else {
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
                public void onFailure(@NonNull Call<GetMessageResponse.ResponseMessage> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
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

    private final Emitter.Listener onUserChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                try {
                    String idConversation = data.getString("conversation");
                    getDataDataMessage(idConversation, avatarUser, false);
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
        }
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

    private void displayUserSelected(@NonNull GetUserByIdResponse.User dataUser) {
        getDataDataMessage(conversationID, dataUser.getAvatar(), true);
        tvName.setText(dataUser.getFull_name());
        Glide.with(this)
                .load(GetImgIPAddress.convertLocalhostToIpAddress(dataUser.getAvatar()))
                .into(imgAvatar);
    }

    private void displayMessage(ArrayList<GetMessageResponse.Message> dataMessage, String avatar) {
        avatarUser = avatar;
        mAdapter = new MessageAdapter(ChatActivity.this, dataMessage, idUserLog, avatar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(linearLayoutManager);
        recyclerViewChat.setAdapter(mAdapter);
        scrollToBottom();

    }

    private void getDataDataMessage(String conversationID, String avatar, boolean isLoad) {
        if (isLoad) {
            LoadingDialog.showProgressDialog(ChatActivity.this, "Loading...");
        }
        String token = preferenceManager.getString("token");
        Call<GetMessageResponse.Root> call = apiService.getMessageByIDConversation(token, conversationID);
        call.enqueue(new Callback<GetMessageResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetMessageResponse.Root> call, @NonNull Response<GetMessageResponse.Root> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        runOnUiThread(() -> {
                            displayMessage(response.body().getDataMessage(), avatar);
                        });
                    } else {
                        runOnUiThread(() -> {
                            if(response.body().getMessage().equals("wrong token")){
                                CheckLoginUtil.gotoLogin(ChatActivity.this,response.body().getMessage());
                            }else {
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
            public void onFailure(@NonNull Call<GetMessageResponse.Root> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, t.getMessage());
                });
            }
        });
    }

    private void getDataUserSelected(String idUser) {
        LoadingDialog.showProgressDialog(ChatActivity.this, "Loading...");
        String token = preferenceManager.getString("token");
        Call<GetUserByIdResponse.Root> call = apiService.getAnyUserById(token, idUser);
        call.enqueue(new Callback<GetUserByIdResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetUserByIdResponse.Root> call, @NonNull Response<GetUserByIdResponse.Root> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        runOnUiThread(() -> displayUserSelected(response.body().getUser()));
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, response.body().getMessage());
                    }
                } else {
                    runOnUiThread(() -> {
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(ChatActivity.this, response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, response.body().getMessage());
                        }
                    });
                }
                LoadingDialog.dismissProgressDialog();
            }

            @Override
            public void onFailure(@NonNull Call<GetUserByIdResponse.Root> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
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
}