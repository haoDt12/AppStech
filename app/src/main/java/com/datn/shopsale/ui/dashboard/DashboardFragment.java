package com.datn.shopsale.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.bumptech.glide.Glide;
import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.VoucherActivity;
import com.datn.shopsale.modelsv2.Customer;
import com.datn.shopsale.responsev2.GetCusInfoResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.ui.dashboard.address.AddressActivity;
import com.datn.shopsale.ui.dashboard.chat.ConversationActivity;
import com.datn.shopsale.ui.dashboard.order.MyOrderActivity;
import com.datn.shopsale.ui.dashboard.setting.SettingActivity;
import com.datn.shopsale.ui.dashboard.store.StoreActivity;
import com.datn.shopsale.ui.login.LoginActivity;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.LoadingDialog;
import com.datn.shopsale.utils.PreferenceManager;
import com.facebook.AccessToken;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {
    private static final String TAG = DashboardFragment.class.getSimpleName();

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount acct;
    private LoginButton btnLoginWithFacebook;
    private TextView tvName;
    private TextView tvEmail;
    private PreferenceManager preferenceManager;
    private CircleImageView imgAvatarUsers;
    private ApiService apiService;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
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

        Button btnLogOut = view.findViewById(R.id.btn_log_out);
        btnLoginWithFacebook = view.findViewById(R.id.login_button);

        LinearLayout lnlCall = view.findViewById(R.id.lnl_call);
        imgAvatarUsers = view.findViewById(R.id.img_avatarUsers);
        apiService = RetrofitConnection.getApiService();
        getUser();

        LinearLayout lnlProfile = view.findViewById(R.id.lnl_profile);
        FrameLayout lnChat = view.findViewById(R.id.ln_chat);
        FrameLayout lnLocation = view.findViewById(R.id.ln_location);
        FrameLayout lnClause = view.findViewById(R.id.ln_clause);
        FrameLayout lnSetting = view.findViewById(R.id.ln_setting);
        FrameLayout lnOrder = view.findViewById(R.id.ln_order);
        FrameLayout lnStore = view.findViewById(R.id.ln_store);
        tvName = view.findViewById(R.id.tv_name);
        FrameLayout lnVoucher = view.findViewById(R.id.ln_voucher);

        tvEmail = view.findViewById(R.id.tv_email);
        tvEmail.setText("");
        tvName.setText("");

        lnlProfile.setOnClickListener(view1 -> activityResultLauncher.launch(new Intent(getContext(), InformationUserActivity.class)));
        lnLocation.setOnClickListener(view1 -> startActivity(new Intent(getContext(), AddressActivity.class)));
        lnChat.setOnClickListener(view1 -> startActivity(new Intent(getContext(), ConversationActivity.class)));
        lnSetting.setOnClickListener(view1 -> startActivity(new Intent(getContext(), SettingActivity.class)));
        lnOrder.setOnClickListener(view1 -> startActivity(new Intent(getContext(), MyOrderActivity.class)));
        lnStore.setOnClickListener(view1 -> startActivity(new Intent(getContext(), StoreActivity.class)));
        lnVoucher.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireActivity(), VoucherActivity.class);
            intent.putExtra("action", 1);
            startActivity(intent);
        });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            btnLogOut.setVisibility(View.GONE);
            btnLoginWithFacebook.setVisibility(View.VISIBLE);
        } else {
            btnLogOut.setVisibility(View.VISIBLE);
            btnLoginWithFacebook.setVisibility(View.GONE);
        }

        btnLoginWithFacebook.setOnClickListener(v -> {
            Thread thread = new Thread(this::run);

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
        lnClause.setOnClickListener(view1 -> {
            Dialog dialog = new Dialog(view1.getContext());
            dialog.setContentView(R.layout.dialog_chose_option);
            Window window = dialog.getWindow();

            if (window != null) {
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(view1.getContext().getDrawable(R.drawable.dialog_bg));
                window.getAttributes().windowAnimations = R.style.DialogAnimation;
                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                window.setAttributes(windowAttributes);
                windowAttributes.gravity = Gravity.BOTTOM;
                dialog.show();

                ImageButton btnCancel = dialog.findViewById(R.id.btn_cancel);
                Button btnBuy = dialog.findViewById(R.id.btn_buy);
                btnCancel.setOnClickListener(view2 -> dialog.cancel());
                btnBuy.setOnClickListener(view2 -> dialog.dismiss());
            }
        });
    }

    public void requets_permistion() {
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || requireContext().checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                || requireContext().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.CALL_PHONE
            }, 1);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void CallPhone() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_call_phone);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(requireContext().getDrawable(R.drawable.dialog_bg));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        window.setAttributes(windowAttributes);
        windowAttributes.gravity = Gravity.BOTTOM;
        Button btnCall = dialog.findViewById(R.id.btn_call);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        String phone = "0961803120";
        btnCall.setText(String.format("Gọi: %s", phone));
        btnCall.setOnClickListener(v2 -> {
            Toast.makeText(getContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v2 -> dialog.cancel());
        dialog.show();
    }

    private void updateUI() {
        startActivity(new Intent(getContext(), LoginActivity.class));
        requireActivity().finish();
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful() && acct != null) {
            }
        });
        startActivity(new Intent(getActivity(), LoginActivity.class));
        preferenceManager.clear();
        requireActivity().finish();
    }

    private void getUser() {
        LoadingDialog.showProgressDialog(getActivity(), "Loading...");
        Call<GetCusInfoResponse> call = apiService.getInfoCus(preferenceManager.getString("token"));
        call.enqueue(new Callback<GetCusInfoResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetCusInfoResponse> call, @NonNull Response<GetCusInfoResponse> response) {
                assert response.body() != null;
                if (response.body().getCode() == 1) {
                    requireActivity().runOnUiThread(() -> {
                        Customer customer = response.body().getCus();
                        Glide.with(requireActivity()).load(customer.getAvatar()).into(imgAvatarUsers);
                        tvName.setText(customer.getFull_name());
                        tvEmail.setText(customer.getEmail());
                        LoadingDialog.dismissProgressDialog();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        LoadingDialog.dismissProgressDialog();
                        if (response.body().getMessage().equals("wrong token")) {
                            CheckLoginUtil.gotoLogin(requireActivity(), response.body().getMessage());
                        } else {
                            AlertDialogUtil.showAlertDialogWithOk(requireActivity(), response.body().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetCusInfoResponse> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(requireActivity(), t.getMessage());
                });
            }
        });
    }

    private void onFragmentResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            getUser();
        });
    }

    private void run() {
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
    }
}