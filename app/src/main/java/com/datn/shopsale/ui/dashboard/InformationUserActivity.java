package com.datn.shopsale.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.models.ResApi;
import com.datn.shopsale.response.ResponseAddress;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformationUserActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICKER = 100;
    private Uri imageUri;
    private ImageButton imgBack;
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
    private ArrayList<ResponseAddress.User> list = new ArrayList<>();
    private ResponseAddress.User mUser;
    private String newName = null;
    private String newNumberPhone = null;
    private String newEmail = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);
        preferenceManager = new PreferenceManager(getApplicationContext());
        apiService = RetrofitConnection.getApiService();
        FindViewById();
        getDataUser();
        imgBack.setOnClickListener(view -> {
            setResult(Activity.RESULT_OK);
            finish();
        });
        onEdit();
        onCancel();
        openCamera();
        getUpdateUser();
    }

    private void FindViewById() {
        imgBack = (ImageButton) findViewById(R.id.img_back);
        imgCamera = (ImageView) findViewById(R.id.img_camera);
        imgUser = findViewById(R.id.img_user);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        edName = (EditText) findViewById(R.id.ed_name);
        edEmail = (EditText) findViewById(R.id.ed_email);
        edPhone = (EditText) findViewById(R.id.ed_phone);
        btnSave = (Button) findViewById(R.id.btn_save);
        cancelAction = (ImageView) findViewById(R.id.cancel_action);
        imgUpdate = (ImageView) findViewById(R.id.img_update);
        lnlLayoutText = (LinearLayout) findViewById(R.id.lnl_layout_text);
        lnlLayoutEdit = (LinearLayout) findViewById(R.id.lnl_layout_edit);

    }

    private void getDataUser() {
        LoadingDialog.showProgressDialog(InformationUserActivity.this, "Loading...");
        list.clear();
        String idUser = preferenceManager.getString("userId");

        Call<ResponseAddress.Root> call = apiService.getAddress(preferenceManager.getString("token"), idUser);
        call.enqueue(new Callback<ResponseAddress.Root>() {
            @Override
            public void onResponse(Call<ResponseAddress.Root> call, Response<ResponseAddress.Root> response) {
                if (response.body().getCode() == 1) {
                    runOnUiThread(() -> {
                        ResponseAddress.User user = response.body().getUser();
                        Glide.with(getApplicationContext()).load(user.getAvatar()).into(imgUser);
                        tvEmail.setText(user.getEmail());
                        tvName.setText(user.getFull_name());
                        tvPhone.setText(user.getPhone_number());
                        edEmail.setText(user.getEmail());
                        edName.setText(user.getFull_name());
                        edPhone.setText(user.getPhone_number());
                        mUser = user;
                        LoadingDialog.dismissProgressDialog();
                    });
                } else {
                    runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        Toast.makeText(InformationUserActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseAddress.Root> call, Throwable t) {
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
            if(!edEmail.getText().toString().isEmpty()){
                newEmail = edEmail.getText().toString();
            }else {
                Toast.makeText(this, "new email is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!edPhone.getText().toString().isEmpty()){
                newNumberPhone = edPhone.getText().toString();
            }else {
                Toast.makeText(this, "new number phone is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!edName.getText().toString().isEmpty()){
                newName = edName.getText().toString();
            }else {
                Toast.makeText(this, "new name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            RequestBody requestBodyName = RequestBody.create(MediaType.parse("text/plain"),newName);
            RequestBody requestBodyEmail = RequestBody.create(MediaType.parse("text/plain"),newEmail);
            RequestBody requestBodyPhone = RequestBody.create(MediaType.parse("text/plain"),newNumberPhone);
            RequestBody requestBodyId = RequestBody.create(MediaType.parse("text/plain"),userId);
            LoadingDialog.showProgressDialog(this,"Loading...");
            if(imageUri == null){
                Call<ResApi> call = apiService.editUser(token,requestBodyEmail,requestBodyName,requestBodyPhone,requestBodyId);
                call.enqueue(new Callback<ResApi>() {
                    @Override
                    public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                        if(response.body().code == 1){
                            runOnUiThread(()->{
                                Toast.makeText(InformationUserActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                LoadingDialog.dismissProgressDialog();
                                getDataUser();
                            });
                        }else {
                            runOnUiThread(() -> {
                                Toast.makeText(InformationUserActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                LoadingDialog.dismissProgressDialog();
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ResApi> call, Throwable t) {
                        runOnUiThread(() -> {
                            Log.d("onFailure", "onFailure: " + t.getMessage());
                            LoadingDialog.dismissProgressDialog();
                        });
                    }
                });
            }else {
                File file = new File(imageUri.getPath());
                RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", file.getName(), imageRequestBody);
                Call<ResApi> call = apiService.editUserImg(token,requestBodyEmail,requestBodyName,requestBodyPhone,imagePart,requestBodyId);
                call.enqueue(new Callback<ResApi>() {
                    @Override
                    public void onResponse(Call<ResApi> call, Response<ResApi> response) {
                        if(response.body().code == 1){
                            runOnUiThread(()->{
                                Toast.makeText(InformationUserActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                LoadingDialog.dismissProgressDialog();
                                getDataUser();
                            });
                        }else {
                            runOnUiThread(() -> {
                                Toast.makeText(InformationUserActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                LoadingDialog.dismissProgressDialog();
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ResApi> call, Throwable t) {
                        runOnUiThread(() -> {
                            Log.d("onFailure", "onFailure: " + t.getMessage());
                            LoadingDialog.dismissProgressDialog();
                        });
                    }
                });
            }
        });
    }

    private void openCamera() {
        imgCamera.setOnClickListener(view -> {
            ImagePicker.Companion.with(this)
                    .cropSquare() // Cắt hình ảnh thành hình vuông
                    .start(REQUEST_IMAGE_PICKER);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imgUser.setImageURI(imageUri);
        }
    }
    private void onCancel(){
        cancelAction.setOnClickListener(v -> {
            imgUpdate.setVisibility(View.VISIBLE);
            lnlLayoutText.setVisibility(View.VISIBLE);
            lnlLayoutEdit.setVisibility(View.INVISIBLE);
            imgCamera.setVisibility(View.INVISIBLE);
            cancelAction.setVisibility(View.INVISIBLE);
            edEmail.setText(mUser.getEmail());
            edName.setText(mUser.getFull_name());
            edPhone.setText(mUser.getPhone_number());
            Picasso.get().load(mUser.getAvatar()).into(imgUser);
        });
    }
    private void onEdit(){
        imgUpdate.setOnClickListener(view -> {
            imgUpdate.setVisibility(View.INVISIBLE);
            lnlLayoutText.setVisibility(View.INVISIBLE);
            lnlLayoutEdit.setVisibility(View.VISIBLE);
            imgCamera.setVisibility(View.VISIBLE);
            cancelAction.setVisibility(View.VISIBLE);
        });
    }
}