package com.datn.shopsale.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.shopsale.Interface.ApiService;
import com.datn.shopsale.MainActivity;
import com.datn.shopsale.R;
import com.datn.shopsale.request.AddFcmRequest;
import com.datn.shopsale.request.CusLoginRequest;
import com.datn.shopsale.response.GetUserGoogleResponse;
import com.datn.shopsale.responsev2.AddFcmResponse;
import com.datn.shopsale.responsev2.CusLoginResponse;
import com.datn.shopsale.retrofit.RetrofitConnection;
import com.datn.shopsale.utils.AlertDialogUtil;
import com.datn.shopsale.utils.CheckLoginUtil;
import com.datn.shopsale.utils.Constants;
import com.datn.shopsale.utils.LoadingDialog;
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
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount acct;
    private final int RC_SIGN_IN = 2;
    private boolean isRemember = false;

    private CallbackManager callbackManager;

    private AccessToken accessToken;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        apiService = RetrofitConnection.getApiService();
        preferenceManager = new PreferenceManager(this);
        if (preferenceManager.isRemember()) {
            isRemember = preferenceManager.getBoolean(Constants.KEY_REMEMBER);
            if (isRemember) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } else {
            edEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
            edPass.setText(preferenceManager.getString(Constants.KEY_PASS));
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            signOut();
        }

        callbackManager = CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            showToast(getString(R.string.facebook_login_session) + " with ID: " + accessToken.getUserId());
        }
        eventClick();
    }

    private void eventClick() {
        tvForgotPass.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ForgotPassActivity.class)));
        cbRemember.setOnClickListener(v -> isRemember = cbRemember.isChecked());
        tvSignUp.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        btnLoginWithEmail.setOnClickListener(v -> login());
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
        tvForgotPass = findViewById(R.id.tv_forgot_pass);
        btnLoginWithGoogle.setColorScheme(SignInButton.COLOR_AUTO);
        setGooglePlusButtonText(btnLoginWithGoogle, getString(R.string.login_google));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loginWithFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                updateUI();
            }

            @Override
            public void onCancel() {
                showToast("facebook:onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException exception) {
                showToast(exception.getMessage());
            }
        });
    }

    private void login() {
        String username = edEmail.getText().toString().trim();
        String pass = edPass.getText().toString().trim();

        if (validForm(username, pass)) {
            if (isRemember) {
                preferenceManager.putString(Constants.KEY_EMAIL, username);
                preferenceManager.putString(Constants.KEY_PASS, pass);
                preferenceManager.putBoolean(Constants.KEY_REMEMBER, isRemember);
            } else {
                preferenceManager.putString(Constants.KEY_EMAIL, username);
            }
            LoadingDialog.showProgressDialog(this, "Loading...");
            CusLoginRequest request = new CusLoginRequest();
            request.setUsername(username);
            request.setPassword(pass);
            Call<CusLoginResponse> call = apiService.loginCus(request);
            call.enqueue(new Callback<CusLoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<CusLoginResponse> call, @NonNull Response<CusLoginResponse> response) {
                    runOnUiThread(LoadingDialog::dismissProgressDialog);
                    if (response.body() != null) {
                        if (response.body().getCode() == 1) {
                            runOnUiThread(() -> {
                                showToast(response.body().getMessage());
                                String idUser = response.body().getId();
                                Intent i = new Intent(LoginActivity.this, VerifyOTPSignInActivity.class);
                                i.putExtra("idUser", idUser);
                                startActivity(i);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> {
                                if (response.body().getMessage().equals("wrong token")) {
                                    CheckLoginUtil.gotoLogin(LoginActivity.this, response.body().getMessage());
                                } else {
                                    AlertDialogUtil.showAlertDialogWithOk(LoginActivity.this, response.body().getMessage());
                                }
                            });
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<CusLoginResponse> call, @NonNull Throwable t) {
                    runOnUiThread(() -> AlertDialogUtil.showAlertDialogWithOk(LoginActivity.this, t.getMessage()));
                }
            });

        }
    }

    private void addTokenFMC(String token, String userId, String fcm) {
        LoadingDialog.showProgressDialog(this, "Loading...");
        AddFcmRequest request = new AddFcmRequest();
        request.setCusId(userId);
        request.setFcm(fcm);
        Call<AddFcmResponse> call = apiService.addFCMCus(token, request);
        call.enqueue(new Callback<AddFcmResponse>() {
            @Override
            public void onResponse(@NonNull Call<AddFcmResponse> call, @NonNull Response<AddFcmResponse> response) {
                runOnUiThread(LoadingDialog::dismissProgressDialog);
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        runOnUiThread(() -> {
                            if (response.body().getMessage().equals("wrong token")) {
                                CheckLoginUtil.gotoLogin(LoginActivity.this, response.body().getMessage());
                            } else {
                                AlertDialogUtil.showAlertDialogWithOk(LoginActivity.this, response.body().getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddFcmResponse> call, @NonNull Throwable t) {
                runOnUiThread(() -> AlertDialogUtil.showAlertDialogWithOk(LoginActivity.this, t.getMessage()));
            }
        });
    }

    private void doLoginWithGoogle(String email, String id, String displayName, String expirationTime, String photoUrl) {
        LoadingDialog.showProgressDialog(this, "Loading...");
        Call<GetUserGoogleResponse.Root> call = apiService.loginWithGoogle(email, id, displayName, expirationTime, photoUrl);
        call.enqueue(new Callback<GetUserGoogleResponse.Root>() {
            @Override
            public void onResponse(@NonNull Call<GetUserGoogleResponse.Root> call, @NonNull Response<GetUserGoogleResponse.Root> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 1) {
                        runOnUiThread(() -> {
                            String token = response.body().getToken();
                            String userID = response.body().getUser().get_id();
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                                String fcm = task.getResult();
                                preferenceManager.putString("token", token);
                                preferenceManager.putString("fcm", fcm);
                                preferenceManager.putString("userId", userID);
                                preferenceManager.putString(Constants.KEY_EMAIL, email);
                                preferenceManager.putBoolean(Constants.KEY_REMEMBER, true);
                                addTokenFMC(token, userID, fcm);
                            });
                        });
                    } else {
                        AlertDialogUtil.showAlertDialogWithOk(LoginActivity.this, response.body().getMessage());
                    }
                } else {
                    AlertDialogUtil.showAlertDialogWithOk(LoginActivity.this, "error get response user");
                }
                LoadingDialog.dismissProgressDialog();
            }

            @Override
            public void onFailure(@NonNull Call<GetUserGoogleResponse.Root> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    LoadingDialog.dismissProgressDialog();
                    AlertDialogUtil.showAlertDialogWithOk(LoginActivity.this, t.getMessage());
                });
            }
        });
    }

    private boolean validForm(@NonNull String username, String pass) {
        String phonePattern = "^(0|\\+84)[3789][0-9]{8}$";
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";

        Pattern patternEmail = Pattern.compile(emailPattern);
        Matcher matcherEmail = patternEmail.matcher(username);

        Pattern patternPhone = Pattern.compile(phonePattern);
        Matcher matcherPhone = patternPhone.matcher(username);

        if (username.isEmpty()) {
            Toast.makeText(this, R.string.re_enter_email_or_phone_number, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass.isEmpty()) {
            Toast.makeText(this, R.string.re_enter_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!matcherPhone.matches() && !matcherEmail.matches()) {
            Toast.makeText(this, R.string.email_or_phone_number_wong, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateUI() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void getInformationUser(GoogleSignInAccount acct) {
        if (acct != null) {
            String typeOfLogin = Objects.requireNonNull(acct.getAccount()).type;
            if (typeOfLogin.equals("com.google")) {
                String zad = acct.zad();
                try {
                    JSONObject jsonUser = new JSONObject(zad);
                    String id = jsonUser.getString("id");
                    String email = jsonUser.getString("email");
                    String displayName = jsonUser.getString("displayName");
                    String photoUrl = replaceURLString(jsonUser.getString("photoUrl"));
                    String expirationTime = jsonUser.getString("expirationTime");
                    doLoginWithGoogle(email, id, displayName, expirationTime, photoUrl);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @NonNull
    private String replaceURLString(@NonNull String preURL) {
        if (preURL.length() == 0) return preURL;
        return preURL.replace("\\", "");
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
        startActivityIfNeeded(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && acct != null) {
                showToast(getString(R.string.logout_success));
            }
        });
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