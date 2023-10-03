package com.datn.shopsale.ui.dashboard;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.datn.shopsale.R;
import com.datn.shopsale.ui.dashboard.address.AddressActivity;
import com.datn.shopsale.ui.login.LoginActivity;
import com.datn.shopsale.ui.dashboard.setting.SettingActivity;
import com.facebook.AccessToken;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardFragment extends Fragment {

    private GoogleSignInClient mGoogleSignInClient;
    private AccessToken accessToken;
    private LoginButton btnLoginWithFacebook;
    private Button btnLogOut;
    private LinearLayout lnCart;
    private LinearLayout lnLocation;
    private LinearLayout lnSetting;
    private LinearLayout lnOrder;
    private LinearLayout lnStore;

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);


        btnLogOut = view.findViewById(R.id.btn_log_out);
        btnLoginWithFacebook = view.findViewById(R.id.login_button);

        lnCart = view.findViewById(R.id.ln_cart);
        lnLocation = view.findViewById(R.id.ln_location);
        lnSetting = view.findViewById(R.id.ln_setting);
        lnOrder = view.findViewById(R.id.ln_order);
        lnStore = view.findViewById(R.id.ln_store);

        lnCart.setOnClickListener(view1 -> {
//            startActivity(new Intent(getContext(), AddressActivity.class));
        });
        lnLocation.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), AddressActivity.class));
        });
        lnSetting.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), SettingActivity.class));
        });
        lnOrder.setOnClickListener(view1 -> {
//            startActivity(new Intent(getContext(), AddressActivity.class));
        });
        lnStore.setOnClickListener(view1 -> {
//            startActivity(new Intent(getContext(), AddressActivity.class));
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
//            updateUI();
//            LoginManager.getInstance().logOut();
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
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
            });

            thread.start();
        });

        btnLogOut.setOnClickListener(view1 -> {
            Dialog dialog = new Dialog(view1.getContext());
            dialog.setContentView(R.layout.dialog_log_out);
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(view1.getContext().getDrawable(R.drawable.dialog_bg));
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            window.setAttributes(windowAttributes);
            windowAttributes.gravity = Gravity.BOTTOM;

            ImageButton btnCancel = dialog.findViewById(R.id.btn_cancel);
            Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
            btnCancel.setOnClickListener(view2 -> {
                dialog.cancel();
            });
            btnConfirm.setOnClickListener(view2 -> {
                signOut();
            });
            dialog.show();
        });
    }

    private void updateUI() {
        startActivity(new Intent(getContext(), LoginActivity.class));
        onDestroy();
    }

    private void signOut() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            showToast("Đăng xuất thành công");
            firebaseAuth.signOut();
            updateUI();
        } else {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast("Đăng xuất thành công");
                                updateUI();
                            }
                        }
                    });
        }

    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}