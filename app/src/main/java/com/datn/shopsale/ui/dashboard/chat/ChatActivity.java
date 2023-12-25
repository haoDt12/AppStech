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
import android.view.KeyEvent;
import android.view.View;
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
    private static final int REQUEST_IMAGE_PICKER = 100;
    private Toolbar toolbar;
    private ImageView imgAvatar;
    private TextView tvName;
    private PreferenceManager preferenceManager;

    private ApiService apiService;
    private GetMessageResponse.MessageAdded mMessages;
    private MessageAdapter mAdapter;
    private RecyclerView recyclerViewChat;


    private static final int REQUEST_LOGIN = 0;
    private static final int TYPING_TIMER_LENGTH = 600;
    private EditText inputMessage;
    private ImageButton btnOption;
    private boolean isTyping = false;

    private final Handler mTypingHandler = new Handler();
    private String mUsername;
    private String avatarUser;
    private String conversationID;
    private String idUserLoged;
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
//        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
//        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on("on-chat", onNewMessage);
        mSocket.on("user-chat", onUserChat);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.connect();

        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        idUserLoged = preferenceManager.getString("userId");
        conversationID = getIntent().getStringExtra("idConversation");
        idUserSelected = getIntent().getStringExtra("idUser");
        getDataUserSelected(idUserSelected);

        btnOption = (ImageButton) findViewById(R.id.img_option);

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
            btnCancel.setOnClickListener(view2 -> {
                dialog.cancel();
            });
            dialog.show();
        });

        inputMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollToBottom();
                }
            }
        });

        inputMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.send || actionId == EditorInfo.IME_NULL) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });

        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                scrollToBottom();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == idUserLoged) return;
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
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });

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

    private void addLog(String message) {
//        mMessages.add(new Message.Builder(Message.TYPE_LOG)
//                .message(message).build());
//        mAdapter.notifyItemInserted(mMessages.size() - 1);
//        scrollToBottom();
    }

    private void addParticipantsLog(int numUsers) {
        Log.d(TAG, "addParticipantsLog: " + numUsers);
//        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
    }


    private void addMessage(String message) {
        Log.d(TAG, "addMessage: " + message);

        String token = preferenceManager.getString("token");
        RequestBody requestBodyConversation = RequestBody.create(MediaType.parse("text/plain"), conversationID);
        RequestBody requestBodySenderId = RequestBody.create(MediaType.parse("text/plain"), idUserLoged);
        RequestBody requestBodyReceiverId = RequestBody.create(MediaType.parse("text/plain"), idUserSelected);
        RequestBody requestBodyMessage = RequestBody.create(MediaType.parse("text/plain"), message);
        Log.d(TAG, "addMessage: message " + requestBodyMessage);
        Log.d(TAG, "addMessage: senderID " + requestBodySenderId);
//        LoadingDialog.showProgressDialog(this, "Loading...");
        if (imageUri != null) {
//            Toast.makeText(this, requestBodyMessage.toString(), Toast.LENGTH_SHORT).show();
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
                                Log.d(TAG, "onResponse: " + response.body().getDataMessage());
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

//                                mAdapter.notifyItemInserted(mMessages.size() - 1);
                                scrollToBottom();

                            });
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, response.body().getMessage());
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
                        Log.d(TAG, "onFailure: " + t.getMessage());
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
//                                mAdapter.notifyItemInserted(mMessages.size() - 1);
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
                            AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, response.body().getMessage());
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
                        Log.d(TAG, "onFailure add message: " + t.getMessage());
                        AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, t.getMessage());
                    });
                }
            });
        }
//        scrollToBottom();
    }

//    private void addTyping(GetMessageResponse.MessageAdded message) {
//        mMessages.add(message);
//        mAdapter.notifyItemInserted(mMessages.size() - 1);
//        scrollToBottom();
//    }

//    private void removeTyping(String username) {
//        for (int i = mMessages.size() - 1; i >= 0; i--) {
//            GetMessageResponse.MessageAdded message = mMessages.get(i);
//            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
//                mMessages.remove(i);
//                mAdapter.notifyItemRemoved(i);
//            }
//        }
//    }

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
//                    GetMessageResponse.Message message = new GetMessageResponse.Message();
//                    try {
//                        GetMessageResponse.Conversation conversation = new GetMessageResponse.Conversation();
//                        conversation.set_id(data.getString("conversation"));
//                        message.set_id(data.getString("_id"));
//                        message.setConversation(conversation);
//                        message.setSenderID(data.getString("senderId"));
//                        message.setReceiverId(data.getString("receiverId"));
//                        message.setMessage(data.getString("message"));
//                        JSONArray filess = data.getJSONArray("filess");
//                        ArrayList<String> listFiless = new ArrayList<String>();
//                        JSONArray jArray = (JSONArray) data.getJSONArray("filess");
//                        for (int i = 0; i < jArray.length(); i++) {
//                            listFiless.add(jArray.getString(i));
//                        }
//                        message.setFiless(listFiless);
//                        ArrayList<String> listImages = new ArrayList<String>();
//                        JSONArray jArrayImages = (JSONArray) data.getJSONArray("images");
//                        for (int i = 0; i < jArray.length(); i++) {
//                            listImages.add(jArray.getString(i));
//                        }
//                        message.setImages(listImages);
//                        message.setVideo(data.getString("video"));
//                        message.setStatus(data.getString("status"));
//                        message.setTimestamp(data.getString("timestamp"));
//                        message.setDeleted(data.getBoolean("deleted"));
//
////                        mAdapter.addMessage(message);
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }

//                    mAdapter.notifyItemInserted(mMessages.size() - 1);
                    String demoData = "{\"conversation\":\"657ada688499cb405d63c9c4\"," +
                            "\"senderId\":\"65355a1058bae97dfefa0cc3\"," +
                            "\"receiverId\":\"65378419a95ea66b3ffda00d\"," +
                            "\"message\":\"pp\"," +
                            "\"filess\":[]," +
                            "\"images\":[\"http:\\/\\/localhost:3000\\/images\\/messages\\/657ada688499cb405d63c9c4\\/images\\/657be86352bf1506995c4917\\/6f4715d5-7bf4-497c-8dd5-bbc2b5014e1b.jpg\"]," +
                            "\"video\":\"\"," +
                            "\"status\":\"unseen\"," +
                            "\"deleted\":false," +
                            "\"timestamp\":\"2023-12-15-12:47:15\"," +
                            "\"_id\":\"657be86352bf1506995c4917\"," +
                            "\"__v\":0}";
                    try {
//                        Toast.makeText(ChatActivity.this, "" + data.getString("message"), Toast.LENGTH_SHORT).show();
                        String idConversation = data.getString("conversation");
                        getDataDataMessage(idConversation, avatarUser, false);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    };

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isConnected) {
                        if (null != mUsername)
                            mSocket.emit("add user", mUsername);
                        Toast.makeText(getApplicationContext(),
                                R.string.connect, Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };

    private final Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "disconnected");
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private final Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                    Toast.makeText(getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                        return;
                    }
//                    removeTyping(username);
//                    addMessage(message);
                }
            });
        }
    };

    private final Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                    addLog(getResources().getString(R.string.message_user_joined, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };

    private final Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                    addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);
//                    removeTyping(username);
                }
            });
        }
    };

    private final Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                        return;
                    }
//                    addTyping(username);
                }
            });
        }
    };


    private final Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                        return;
                    }
//                    removeTyping(username);
                }
            });
        }
    };

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
        mAdapter = new MessageAdapter(ChatActivity.this, dataMessage, idUserLoged, avatar);
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
//                            Log.d(TAG, "onResponse: " + response.body().getDataMessage().get(0).getTimestamp());
                            displayMessage(response.body().getDataMessage(), avatar);
                        });
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, response.body().getMessage());
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
                    Log.d(TAG, "onFailure: " + t.getMessage());
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
                        runOnUiThread(() -> {
//                            Log.d(TAG, "onResponse: " + response.body().getUser().getFull_name());
                            displayUserSelected(response.body().getUser());
                        });
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, response.body().getMessage());
                    }
                } else {
                    AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, "error get data conversation");
                }
                LoadingDialog.dismissProgressDialog();
            }

            @Override
            public void onFailure(@NonNull Call<GetUserByIdResponse.Root> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    AlertDialogUtil.showAlertDialogWithOk(ChatActivity.this, t.getMessage());
                });
            }
        });
    }

//    private void openCamera() {
//        imgCamera.setOnClickListener(view -> {
//            ImagePicker.Companion.with(this)
//                    .cropSquare() // Cắt hình ảnh thành hình vuông
//                    .start(REQUEST_IMAGE_PICKER);
//        });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
//            Log.d(TAG, "onActivityResult: " + imageUri);
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
//        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
//        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off("on-chat", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
    }
}