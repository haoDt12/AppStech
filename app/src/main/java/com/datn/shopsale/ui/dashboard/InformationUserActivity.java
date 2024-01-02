package com.datn.shopsale.ui.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.modelsv2.Customer;
import com.datn.shopsale.responsev2.GetCusInfoResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformationUserActivity extends AppCompatActivity {
    private static final String TAG = "UploadImageActivity";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
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
        apiService = RetrofitConnection.getApiService();
        FirebaseApp.initializeApp(this);
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
                            Toast.makeText(InformationUserActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetCusInfoResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(InformationUserActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    LoadingDialog.dismissProgressDialog();
                });
            }
        });
    }

    private void getUpdateUser() {
        btnSave.setOnClickListener(v -> {
            String token = preferenceManager.getString("token");
            String userId = preferenceManager.getString("userId");
            if (!edEmail.getText().toString().isEmpty()) {
                newEmail = edEmail.getText().toString();
            } else {
                Toast.makeText(this, "new email is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!edPhone.getText().toString().isEmpty()) {
                newNumberPhone = edPhone.getText().toString();
            } else {
                Toast.makeText(this, "new number phone is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!edName.getText().toString().isEmpty()) {
                newName = edName.getText().toString();
            } else {
                Toast.makeText(this, "new name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            RequestBody requestBodyName = RequestBody.create(MediaType.parse("text/plain"), newName);
            RequestBody requestBodyEmail = RequestBody.create(MediaType.parse("text/plain"), newEmail);
            RequestBody requestBodyPhone = RequestBody.create(MediaType.parse("text/plain"), newNumberPhone);
            RequestBody requestBodyId = RequestBody.create(MediaType.parse("text/plain"), userId);
            LoadingDialog.showProgressDialog(this, "Loading...");
            if (imageUri == null) {
                Call<ResApi> call = apiService.editUser(token, requestBodyEmail, requestBodyName, requestBodyPhone, requestBodyId);
                call.enqueue(new Callback<ResApi>() {
                    @Override
                    public void onResponse(@NonNull Call<ResApi> call, @NonNull Response<ResApi> response) {
                        if (response.body() != null) {
                            if (response.body().code == 1) {
                                runOnUiThread(() -> {
                                    Toast.makeText(InformationUserActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                    LoadingDialog.dismissProgressDialog();
                                    getDataUser();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(InformationUserActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                    LoadingDialog.dismissProgressDialog();
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResApi> call, @NonNull Throwable t) {
                        runOnUiThread(() -> {
                            Log.d("onFailure", "onFailure: " + t.getMessage());
                            LoadingDialog.dismissProgressDialog();
                        });
                    }
                });
            } else {
                File file = new File(Objects.requireNonNull(imageUri.getPath()));
                RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", file.getName(), imageRequestBody);
                Call<ResApi> call = apiService.editUserImg(token, requestBodyEmail, requestBodyName, requestBodyPhone, imagePart, requestBodyId);
                call.enqueue(new Callback<ResApi>() {
                    @Override
                    public void onResponse(@NonNull Call<ResApi> call, @NonNull Response<ResApi> response) {
                        if (response.body() != null) {
                            if (response.body().code == 1) {
                                runOnUiThread(() -> {
                                    Toast.makeText(InformationUserActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                    LoadingDialog.dismissProgressDialog();
                                    getDataUser();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(InformationUserActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                    LoadingDialog.dismissProgressDialog();
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResApi> call, @NonNull Throwable t) {
                        runOnUiThread(() -> {
                            Log.d("onFailure", "onFailure: " + t.getMessage());
                            LoadingDialog.dismissProgressDialog();
                        });
                    }
                });
            }
            Cancel();
        });
    }

    private void openCamera() {
        imgCamera.setOnClickListener(view -> ImagePicker.Companion.with(this)
                .cropSquare() // Cắt hình ảnh thành hình vuông
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
        Picasso.get().load(GetImgIPAddress.convertLocalhostToIpAddress(mCustomer.getAvatar())).into(imgUser);
    }

    private void Update() {
        imgUpdate.setVisibility(View.INVISIBLE);
        lnlLayoutText.setVisibility(View.INVISIBLE);
        lnlLayoutEdit.setVisibility(View.VISIBLE);
        imgCamera.setVisibility(View.VISIBLE);
        cancelAction.setVisibility(View.VISIBLE);
    }
}