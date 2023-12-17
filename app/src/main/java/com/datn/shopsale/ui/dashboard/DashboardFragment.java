package com.datn.shopsale.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.ChatScreenAdminActivity;
import com.datn.shopsale.activities.VoucherActivity;
import com.datn.shopsale.response.GetUserByIdResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.dashboard.address.AddressActivity;
import com.datn.shopsale.ui.dashboard.chat.ConversationActivity;
import com.datn.shopsale.ui.dashboard.order.MyOrderActivity;
import com.datn.shopsale.ui.dashboard.setting.SettingActivity;
import com.datn.shopsale.ui.dashboard.store.StoreActivity;
import com.datn.shopsale.ui.login.LoginActivity;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.GetImgIPAddress;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;
import com.facebook.AccessToken;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {
    private static final String TAG = DashboardFragment.class.getSimpleName();

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount acct;
    private AccessToken accessToken;
    private LoginButton btnLoginWithFacebook;
    private Button btnLogOut;
    private FrameLayout lnChat;
    private FrameLayout lnVoucher;
    private FrameLayout lnLocation;
    private FrameLayout lnClause;
    private FrameLayout lnSetting;
    private FrameLayout lnOrder;
    private FrameLayout lnStore;
    private LinearLayout lnlProfile;
    private TextView tvName;
    private TextView tvEmail;
    private PreferenceManager preferenceManager;
    private TextView tvTitle;
    private CircleImageView imgAvatarUsers;
    private ApiService apiService;
    private GetUserByIdResponse.User user;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private LinearLayout lnlCall;

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFragmentResult();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        acct = GoogleSignIn.getLastSignedInAccount(requireActivity());
        preferenceManager = new PreferenceManager(requireActivity());

        btnLogOut = view.findViewById(R.id.btn_log_out);
        btnLoginWithFacebook = view.findViewById(R.id.login_button);

        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        lnlCall = (LinearLayout) view.findViewById(R.id.lnl_call);
        imgAvatarUsers = view.findViewById(R.id.img_avatarUsers);
        apiService = RetrofitConnection.getApiService();
        getUser();

        lnlProfile = view.findViewById(R.id.lnl_profile);
        lnChat = view.findViewById(R.id.ln_chat);
        lnLocation = view.findViewById(R.id.ln_location);
        lnClause = view.findViewById(R.id.ln_clause);
        lnSetting = view.findViewById(R.id.ln_setting);
        lnOrder = view.findViewById(R.id.ln_order);
        lnStore = view.findViewById(R.id.ln_store);
        tvName = view.findViewById(R.id.tv_name);
        lnVoucher = view.findViewById(R.id.ln_voucher);

        tvEmail = view.findViewById(R.id.tv_email);
        tvEmail.setText("");
        tvName.setText("");

        lnlProfile.setOnClickListener(view1 -> activityResultLauncher.launch(new Intent(getContext(), InformationUserActivity.class)));
        lnLocation.setOnClickListener(view1 -> startActivity(new Intent(getContext(), AddressActivity.class)));
        lnChat.setOnClickListener(view1 -> startActivity(new Intent(getContext(), ConversationActivity.class)));
        lnClause.setOnClickListener(view1 -> {
            Toast.makeText(getContext(), "Feature in development", Toast.LENGTH_SHORT).show();
        });
        lnSetting.setOnClickListener(view1 -> startActivity(new Intent(getContext(), SettingActivity.class)));
        lnOrder.setOnClickListener(view1 -> startActivity(new Intent(getContext(), MyOrderActivity.class)));
        lnStore.setOnClickListener(view1 -> startActivity(new Intent(getContext(), StoreActivity.class)));
        lnVoucher.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireActivity(), VoucherActivity.class);
            intent.putExtra("action", 1);
            startActivity(intent);
        });

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            btnLogOut.setVisibility(View.GONE);
            btnLoginWithFacebook.setVisibility(View.VISIBLE);
        } else {
            btnLogOut.setVisibility(View.VISIBLE);
            btnLoginWithFacebook.setVisibility(View.GONE);
        }

        btnLoginWithFacebook.setOnClickListener(v -> {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        String result = btnLoginWithFacebook.getText().toString();
                        if (result.equals("Continue with Facebook") || result.equals("Tiếp tục với Facebook")) {
                            updateUI();
                            break;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });

            thread.start();
        });

        btnLogOut.setOnClickListener(view1 -> {
            Dialog dialog = new Dialog(view1.getContext());
            dialog.setContentView(R.layout.dialog_log_out);
            Window window = dialog.getWindow();

            if (window != null) {
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(view1.getContext().getDrawable(R.drawable.dialog_bg));
                window.getAttributes().windowAnimations = R.style.DialogAnimation;
                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                window.setAttributes(windowAttributes);
                windowAttributes.gravity = Gravity.BOTTOM;
                dialog.show();
            }
            ImageButton btnCancel = dialog.findViewById(R.id.btn_cancel);
            Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
            btnCancel.setOnClickListener(view2 -> dialog.cancel());
            btnConfirm.setOnClickListener(view2 -> {
                dialog.dismiss();
                signOut();
            });
        });
        lnlCall.setOnClickListener(v -> {
            requets_permistion();
            CallPhone();
        });
    }
    public boolean requets_permistion() {
        if (getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && getContext().checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                && getContext().checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.CALL_PHONE
            }, 1);
            return false;
        }
    }
    public void CallPhone(){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_call_phone);
        dialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.dialog_bg));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        window.setAttributes(windowAttributes);
        windowAttributes.gravity = Gravity.BOTTOM;
        Button btnCall = (Button) dialog.findViewById(R.id.btn_call);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);

        String phone = "0961803120";
        btnCall.setText("Gọi: "+phone);
        btnCall.setOnClickListener(v2 -> {
            Toast.makeText(getContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
//            Intent intent_call = new Intent(Intent.ACTION_CALL, Uri.parse("tel: " +phone));
//            startActivity(intent_call);
        });
        btnCancel.setOnClickListener(v2 -> {
            dialog.cancel();
        });
        dialog.show();
    }
    private void updateUI() {
        startActivity(new Intent(getContext(), LoginActivity.class));
        requireActivity().finish();
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful() && acct != null) {
                Log.d(TAG, "signOutGoogle: " + task.getResult());
            }
        });
        startActivity(new Intent(getActivity(), LoginActivity.class));
        preferenceManager.clear();
        requireActivity().finish();
    }

    private void getUser() {
        LoadingDialog.showProgressDialog(getActivity(), "Loading...");
        Call<GetUserByIdResponse.Root> call = apiService.getUserById(preferenceManager.getString("token"), preferenceManager.getString("userId"));
        call.enqueue(new Callback<GetUserByIdResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetUserByIdResponse.Root> call, @NonNull Response<GetUserByIdResponse.Root> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    requireActivity().runOnUiThread(() -> {
                        user = response.body().getUser();
                        Picasso.get().load(GetImgIPAddress.convertLocalhostToIpAddress(user.getAvatar())).into(imgAvatarUsers);
                        tvName.setText(user.getFull_name());
                        tvEmail.setText(user.getEmail());
                        LoadingDialog.dismissProgressDialog();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetUserByIdResponse.Root> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());
                });
            }
        });
    }

    private void onFragmentResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                getUser();
            }
        });
    }

}