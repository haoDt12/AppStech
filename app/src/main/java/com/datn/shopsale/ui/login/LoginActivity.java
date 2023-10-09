package com.datn.shopsale.ui.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.shopsale.MainActivity;
import com.datn.shopsale.R;
import com.datn.shopsale.models.User;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.HashPassword;
import com.datn.shopsale.utils.PreferenceManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private PreferenceManager preferenceManager;

    private EditText edEmail, edPass;
    private CheckBox cbRemember;
    private Button btnLoginWithEmail;
    private SignInButton btnLoginWithGoogle;
    private LoginButton btnLoginWithFacebook;
    private TextView tvSignUp;
    private TextView tvForgotPass;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount acct;
    private final int RC_SIGN_IN = 2;
    private boolean isRemember = false;

    private CallbackManager callbackManager;

    private AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        tvForgotPass.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),ForgotPassActivity.class));
        });
        // Share preference
        preferenceManager = new PreferenceManager(this);

        if (preferenceManager.isRemember()) {
            isRemember = preferenceManager.getBoolean(Constants.KEY_REMEMBER);
            if (isRemember) {
                String email = preferenceManager.getString(Constants.KEY_EMAIL);
                String pass = preferenceManager.getString(Constants.KEY_PASS);
                edEmail.setText(email);
                edPass.setText(pass);
                cbRemember.setChecked(isRemember);
            }
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(this);

        if (acct != null) {
            getInformationUser(acct);
            showToast(getString(R.string.google_login_session));
            updateUI();
        }
        // Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            showToast(getString(R.string.fb_auth_login_session));
            updateUI();
        }

        // Facebook
        callbackManager = CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            showToast(getString(R.string.facebook_login_session) + " with ID: " + accessToken.getUserId());
            updateUI();
        }

        eventClick();
    }

    private void eventClick() {
        cbRemember.setOnClickListener(v -> isRemember = cbRemember.isChecked());
        tvSignUp.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            finish();
        });
        btnLoginWithEmail.setOnClickListener(v -> loginWithEmail());
        btnLoginWithGoogle.setOnClickListener(v -> {
            signOut();
            signInWithGoogle();
        });
        btnLoginWithFacebook.setOnClickListener(v -> {
            loginWithFacebook();
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        String result = btnLoginWithFacebook.getText().toString();
                        if (result.equals(Constants.CONTEXT_LOGOUT_FACEBOOK_EN) || result.equals(Constants.CONTEXT_LOGOUT_FACEBOOK_VI)) {
                            updateUI();
                            break;
                        }
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        });
    }

    private void initView() {
        edEmail = findViewById(R.id.ed_email);
        edPass = findViewById(R.id.ed_pass);
        cbRemember = findViewById(R.id.cb_remember);
        tvSignUp = findViewById(R.id.tv_sign_up);
        btnLoginWithEmail = findViewById(R.id.btn_login_email);
        btnLoginWithGoogle = findViewById(R.id.btn_login_google);
        btnLoginWithFacebook = findViewById(R.id.btn_login_facebook);
        tvForgotPass = (TextView) findViewById(R.id.tv_forgot_pass);
        // custom btn google
        //btnLoginWithGoogle.setSize(SignInButton.SIZE_WIDE);
        btnLoginWithGoogle.setColorScheme(SignInButton.COLOR_AUTO);
        setGooglePlusButtonText(btnLoginWithGoogle, getString(R.string.login_google));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loginWithFacebook() {

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        accessToken = loginResult.getAccessToken();
                        String userID = accessToken.getUserId();
                        String applicationID = accessToken.getApplicationId();
                        String token = accessToken.getToken();
                        String expires = String.valueOf(accessToken.getExpires());
                        Log.d(TAG, "onSuccess FB: " + "ID: " + userID + " - ApplicationID: " + applicationID + " - token: " + token + " -expires: " + expires);

                        updateUI();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        showToast("facebook:onCancel");
                    }

                    @Override
                    public void onError(@NonNull FacebookException exception) {
                        // App code
                        showToast(exception.getMessage());
                    }
                });
    }

    private void loginWithEmail() {
        String email = edEmail.getText().toString().trim();
        String pass = edPass.getText().toString().trim();
        if (validForm(email, pass)) {
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        boolean isEmailVerified = currentUser.isEmailVerified();
                        if (isEmailVerified) {
                            if (isRemember) {
                                preferenceManager.putString(Constants.KEY_EMAIL, email);
                                preferenceManager.putString(Constants.KEY_PASS, pass);
                            }
                            preferenceManager.putBoolean(Constants.KEY_REMEMBER, isRemember);
                            showToast(getString(R.string.login_success));
                            User user = new User();
                            String newPass = HashPassword.hashPassword(pass);
                            user.setEmail(email);
                            user.setPassword(newPass);

                            // push data user to db
                            updateUI();
                        } else {
                            //showToast(getString(R.string.verify_email_msg));
                            showConfirmVerifyEmail();
                        }
                    }
                } else {
                    String message = Objects.requireNonNull(task.getException()).getMessage();
                    showToast(message);
                }
            });
        }
    }

    private boolean validForm(@NonNull String email, String pass) {
        if (email.length() == 0 || pass.length() == 0) {
            showToast(getString(R.string.requireForm));
            return false;
        } else if (pass.length() < 6) {
            showToast(getString(R.string.requirePass));
            return false;
        }
        return true;
    }

    private void updateUI() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void getInformationUser(GoogleSignInAccount acct) {
        if (acct != null) {
            String typeOfLogin = Objects.requireNonNull(acct.getAccount()).type;
            if (typeOfLogin.equals("com.google")) {
                /// login with google

                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                String idToken = acct.getIdToken();
                if (idToken != null) {
                    showToast(idToken);
                }
                User user = new User();
                user.set_id(personId);
                user.setAvatar(String.valueOf(personPhoto));
                user.setEmail(personEmail);
                user.setFull_name(personName);
                user.setRole("end_user");

                // push data user to db
                Log.d(TAG, "updateUI: " + personGivenName + " - " + personFamilyName + " - " + personEmail + " - " + personId + " - " + personPhoto);
                updateUI();
            }
        }
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            getInformationUser(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            getInformationUser(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && acct != null) {
                showToast(getString(R.string.logout_success));
            }
        });
    }

    // Disconnect accounts
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, task -> {
            // ...
        });
    }

    private void sendVerifyEmail() {
        currentUser.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToast(getString(R.string.check_email_verify));
                Log.d(TAG, "Email sent.");
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showConfirmVerifyEmail() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_verify_email);
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
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
            sendVerifyEmail();
        });
        dialog.show();
    }

    private void setGooglePlusButtonText(@NonNull SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }
}