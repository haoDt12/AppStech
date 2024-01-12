package com.datn.shopsale.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.modelsv2.Customer;
import com.datn.shopsale.request.EditCusRequest;
import com.datn.shopsale.responsev2.EditCusResponse;
import com.datn.shopsale.responsev2.GetCusInfoResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformationUserActivity extends AppCompatActivity {
    private static final String TAG = "UploadImageActivity";
    private FirebaseStorage mStorage;
    private static final int REQUEST_IMAGE_PICKER = 100;
    private Uri imageUri;
    private ImageView imgCamera;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvPhone;
    private ImageView cancelAction;
    private CircleImageView imgUser;
    private EditText edName;
    private EditText edEmail;
    private EditText edPhone;
    private Button btnSave;
    private ImageView imgUpdate;
    private LinearLayout lnlLayoutText;
    private LinearLayout lnlLayoutEdit;
    private PreferenceManager preferenceManager;
    private ApiService apiService;
    private Customer mCustomer;
    private String newName = null;
    private String newNumberPhone = null;
    private String newEmail = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);
        preferenceManager = new PreferenceManager(getApplicationContext());
        mStorage = FirebaseStorage.getInstance();
        apiService = RetrofitConnection.getApiService();
        FindViewById();
        getDataUser();
        onEdit();
        onCancel();
        openCamera();
        getUpdateUser();
    }

    private void FindViewById() {
        Toolbar toolbarInfoUser = findViewById(R.id.toolbar_info_user);
        imgCamera = findViewById(R.id.img_camera);
        imgUser = findViewById(R.id.img_user);
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        edName = findViewById(R.id.ed_name);
        edEmail = findViewById(R.id.ed_email);
        edPhone = findViewById(R.id.ed_phone);
        btnSave = findViewById(R.id.btn_save);
        cancelAction = findViewById(R.id.cancel_action);
        imgUpdate = findViewById(R.id.img_update);
        lnlLayoutText = findViewById(R.id.lnl_layout_text);
        lnlLayoutEdit = findViewById(R.id.lnl_layout_edit);

        setSupportActionBar(toolbarInfoUser);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.angle_left);
        toolbarInfoUser.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void getDataUser() {
        LoadingDialog.showProgressDialog(InformationUserActivity.this, "Loading...");
        Call<GetCusInfoResponse> call = apiService.getInfoCus(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetCusInfoResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetCusInfoResponse> call, @NonNull Response<GetCusInfoResponse> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        runOnUiThread(() -> {
                            Customer customer = response.body().getCus();
                            Glide.with(getApplicationContext()).load(GetImgIPAddress.convertLocalhostToIpAddress(customer.getAvatar())).into(imgUser);
                            tvEmail.setText(customer.getEmail());
                            tvName.setText(customer.getFull_name());
                            tvPhone.setText(customer.getPhone_number());
                            edEmail.setText(customer.getEmail());
                            edName.setText(customer.getFull_name());
                            edPhone.setText(customer.getPhone_number());
                            mCustomer = customer;
                            LoadingDialog.dismissProgressDialog();
                        });
                    } else {
                        runOnUiThread(() -> {
                            LoadingDialog.dismissProgressDialog();
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(InformationUserActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(InformationUserActivity.this, response.body().getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetCusInfoResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialogWithOk(InformationUserActivity.this, t.getMessage());
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
    }

    private void getUpdateUser() {
        btnSave.setOnClickListener(v -> {
            EditCusRequest request = new EditCusRequest();
            if (!edEmail.getText().toString().isEmpty()) {
                newEmail = edEmail.getText().toString();
                request.setEmail(newEmail);
            } else {
                Toast.makeText(this, "new email is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!edPhone.getText().toString().isEmpty()) {
                newNumberPhone = edPhone.getText().toString();
                request.setPhone_number(newNumberPhone);
            } else {
                Toast.makeText(this, "new number phone is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!edName.getText().toString().isEmpty()) {
                newName = edName.getText().toString();
                request.setFull_name(newName);
            } else {
                Toast.makeText(this, "new name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (imageUri != null) {
                uploadImageToFirebase(imageUri,request);
            }
            LoadingDialog.showProgressDialog(this, "Loading...");
            Call<EditCusResponse> call = apiService.sendOtpEditCus(preferenceManager.getString("token"), request);
            call.enqueue(new Callback<EditCusResponse>() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onResponse(@NonNull Call<EditCusResponse> call, @NonNull Response<EditCusResponse> response) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
                                LoadingDialog.dismissProgressDialog();
                                Dialog dialog = new Dialog(InformationUserActivity.this);
                                dialog.setContentView(R.layout.dialog_otp_change_password);
                                Window window = dialog.getWindow();
                                if (window != null) {
                                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                    window.setBackgroundDrawable(InformationUserActivity.this.getDrawable(R.drawable.dialog_bg));
                                    window.getAttributes().windowAnimations = R.style.DialogAnimation;
                                    WindowManager.LayoutParams windowAttributes = window.getAttributes();
                                    window.setAttributes(windowAttributes);
                                    windowAttributes.gravity = Gravity.BOTTOM;
                                }
                                TextInputEditText edOtp = dialog.findViewById(R.id.ed_otp);
                                Button btnSendOtp = dialog.findViewById(R.id.btn_sendOtp);
                                ImageButton btnCancel = dialog.findViewById(R.id.btn_cancel);
                                btnCancel.setOnClickListener(view -> dialog.dismiss());
                                btnSendOtp.setOnClickListener(view -> {
                                    if (TextUtils.isEmpty(Objects.requireNonNull(edOtp.getText()).toString().trim())) {
                                        Toast.makeText(InformationUserActivity.this, getResources().getString(R.string.ma_otp_khong_hop_le), Toast.LENGTH_SHORT).show();
                                    }
                                    request.setOtp(edOtp.getText().toString().trim());
                                    Call<EditCusResponse> call1 = apiService.editCus(preferenceManager.getString("token"), request);
                                    LoadingDialog.showProgressDialog(InformationUserActivity.this, "Loading....");
                                    call1.enqueue(new Callback<EditCusResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<EditCusResponse> call, @NonNull Response<EditCusResponse> response) {
                                            runOnUiThread(LoadingDialog::dismissProgressDialog);
                                            if (response.body() != null) {
                                                if (response.body().getCode() == 1) {
                                                    runOnUiThread(() -> {
                                                        dialog.dismiss();
                                                        onCancel();
                                                        getDataUser();
                                                        AlertDialogUtil.showAlertDialogWithOk(InformationUserActivity.this, response.body().getMessage());
                                                    });
                                                } else {
                                                    runOnUiThread(() -> {
                                                        if (response.body().getMessage().equals("wrong token")) {
                                                            CheckLoginUtil.gotoLogin(InformationUserActivity.this, response.body().getMessage());
                                                        } else {
                                                            AlertDialogUtil.showAlertDialogWithOk(InformationUserActivity.this, response.body().getMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<EditCusResponse> call, @NonNull Throwable t) {
                                            runOnUiThread(() -> {
                                                AlertDialogUtil.showAlertDialogWithOk(InformationUserActivity.this, t.getMessage());
                                                LoadingDialog.dismissProgressDialog();
                                            });
                                        }
                                    });
                                });
                                dialog.show();
                            });
                        } else {
                            runOnUiThread(() -> {
                                LoadingDialog.dismissProgressDialog();
                                if (response.body().getMessage().equals("wrong token")) {
                                    CheckLoginUtil.gotoLogin(InformationUserActivity.this, response.body().getMessage());
                                } else {
                                    AlertDialogUtil.showAlertDialogWithOk(InformationUserActivity.this, response.body().getMessage());
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EditCusResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        AlertDialogUtil.showAlertDialogWithOk(InformationUserActivity.this, t.getMessage());
                    });
                }
            });
        });
    }

    private void openCamera() {
        imgCamera.setOnClickListener(view -> ImagePicker.Companion.with(this)
                .cropSquare()
                .start(REQUEST_IMAGE_PICKER));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                imgUser.setImageURI(imageUri);
            }
        }
    }

    private void onCancel() {
        cancelAction.setOnClickListener(v -> Cancel());
    }

    private void onEdit() {
        imgUpdate.setOnClickListener(view -> Update());
    }

    private void Cancel() {
        imgUpdate.setVisibility(View.VISIBLE);
        lnlLayoutText.setVisibility(View.VISIBLE);
        lnlLayoutEdit.setVisibility(View.INVISIBLE);
        imgCamera.setVisibility(View.INVISIBLE);
        cancelAction.setVisibility(View.INVISIBLE);
        edEmail.setText(mCustomer.getEmail());
        edName.setText(mCustomer.getFull_name());
        edPhone.setText(mCustomer.getPhone_number());
        Glide.with(this).load(GetImgIPAddress.convertLocalhostToIpAddress(mCustomer.getAvatar())).into(imgUser);
    }

    private void Update() {
        imgUpdate.setVisibility(View.INVISIBLE);
        lnlLayoutText.setVisibility(View.INVISIBLE);
        lnlLayoutEdit.setVisibility(View.VISIBLE);
        imgCamera.setVisibility(View.VISIBLE);
        cancelAction.setVisibility(View.VISIBLE);
    }

    public void uploadImageToFirebase(Uri imageUri, EditCusRequest request) {
        String userId = mCustomer.get_id();
        String imageName = "image_" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = mStorage.getReference().child("images").child(userId).child(imageName);

        storageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return storageRef.getDownloadUrl();
                })
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return task.getResult().toString();
                })
                .addOnSuccessListener(imageUrl -> {
                    request.setAvatar(imageUrl);
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Upload failed: " + exception.getMessage()));
    }
}
