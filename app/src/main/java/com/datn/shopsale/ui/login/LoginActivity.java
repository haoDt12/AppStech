package com.datn.shopsale.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.shopsale.MainActivity;
import com.datn.shopsale.R;
import com.datn.shopsale.activities.SignUpActivity;
import com.datn.shopsale.models.User;
import com.datn.shopsale.utils.HashPassword;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText edEmail, edPass;
    private Button btnLogin;
    private ImageView imgLoginGoogle;
    private TextView tvSignUp;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount acct;
    private final int RC_SIGN_IN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        acct = GoogleSignIn.getLastSignedInAccount(this);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        if (acct != null) {
            getInformationUser(acct);
            showToast("Đang trong phiên đăng nhập google");
            updateUI();
        }

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        tvSignUp.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            finish();
        });



        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

//        if (currentUser != null) {
//            showToast("Đang trong phiên đăng nhập email firebase");
//            firebaseAuth.signOut();
//        }

    }

    private void initView() {
        edEmail = findViewById(R.id.ed_email);
        edPass = findViewById(R.id.ed_pass);
        tvSignUp = findViewById(R.id.tv_sign_up);

        // auto fill
        edEmail.setText("accounttest@gmail.com");
        edPass.setText("123456");
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        if (idView == R.id.sign_in_button) {
            signOut();
            signInWithGoogle();
        } else if (idView == R.id.btn_login) {
            loginWithEmail();
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loginWithEmail() {
        String email = edEmail.getText().toString().trim();
        String pass = edPass.getText().toString().trim();
        if (validForm(email, pass)) {
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        showToast("Đăng nhập thành công");
                        User user = new User();
                        String newPass = HashPassword.hashPassword(pass);
                        user.setEmail(email);
                        user.setPassword(newPass);

                        // push data user to db
                        updateUI();
                    } else {
                        String message = Objects.requireNonNull(task.getException()).getMessage();
                        showToast(message);
                    }

                }
            });
        }
    }

    private boolean validForm(String email, String pass) {
        if (email.length() == 0 || pass.length() == 0) {
            showToast("Điền đủ thông tin");
            return false;
        } else if (pass.length() < 6) {
            showToast("Password > 6");
            return false;
        }
        return true;
    }

    private void logoutEmailFirebase() {
        if (currentUser != null) {
            firebaseAuth.signOut();
            showToast("Đăng xuất thành công");
        } else {
            showToast("Chưa đăng nhập");
        }

    }

    private void updateUI(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    private void getInformationUser(GoogleSignInAccount acct) {
        if (acct != null) {
            String typeOfLogin = Objects.requireNonNull(acct.getAccount()).type;
            if (typeOfLogin.equals("com.google")) {
                /// login with google
            }
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


    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            getInformationUser(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            getInformationUser(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful() && acct != null){
                            showToast("Đăng xuất thành công");
                        }
                    }
                });
    }

    // Disconnect accounts
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

}