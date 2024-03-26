package com.example.mychat.ui;

import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.mychat.R;
import com.example.mychat.constants.AppKeys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class signup extends AppCompatActivity {

    private RoundedImageView layout_image;
    private Button signup;
    private EditText username;
    private EditText email;
    private EditText pass;
    private EditText repass;
    private TextView login;
    private ScrollView view;
    private RelativeLayout progressBarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        signup = findViewById(R.id.signup_procced);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        repass = findViewById(R.id.repass);
        login = findViewById(R.id.loginAccount);
        layout_image = findViewById(R.id.layoutimage);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        view = findViewById(R.id.scroll_view);
        progressBarLayout = findViewById(R.id.progress_bar_layout);
        onLoginClicked();
        onSignUpClicked();
        setPermission();
    }

    private void setPermission() {
        if (!checkPermission()) {
            requestPermissions(new String[]{ACCESS_NOTIFICATION_POLICY}, 1);
        }
    }

    private boolean checkPermission() {
        int result = ActivityCompat.checkSelfPermission(this, ACCESS_NOTIFICATION_POLICY);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void onSignUpClicked() {
        signup.setOnClickListener(view -> {
            final String user_name = username.getText().toString().replaceAll("\\s", "");
            final String Email = email.getText().toString();
            final String password = pass.getText().toString();

            if (!user_name.isEmpty()) {
                if (!Email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    if (password.length() >= 6 && password.equals(repass.getText().toString())) {
                        setProgressbarView(true);
                        hideKeyboard(true);
                        User user = new User();
                        user.setUid(user_name);
                        user.setName(Email);
                        setDefaultAvatar(user);
                        signupFirebase(user, password);
                        createUser(user);
                    } else if (password.length() < 6) {
                        hideKeyboard(false);
                        pass.setError("Password must be 6 characters or more");
                    } else {
                        hideKeyboard(false);
                        repass.setError("Passwords do not match");
                    }
                } else if (Email.isEmpty()) {
                    hideKeyboard(false);
                    email.setError("Enter Email!!");
                } else {
                    hideKeyboard(false);
                    email.setError("Enter a valid Email!!");
                }
            } else {
                hideKeyboard(false);
                username.setError("Enter a valid username!!");
            }
        });
    }

    private void hideKeyboard(boolean var) {
        if (var) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        }
    }

    private void setProgressbarView(boolean var) {
        if (var) {
            progressBarLayout.setVisibility(View.VISIBLE);
        } else {
            progressBarLayout.setVisibility(View.GONE);
        }
    }

    private void createUser(User user) {
        CometChat.createUser(user, AppKeys.COMETCHAT_AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("MYTAG", "User Created Successfully");
                try {
                    Thread.sleep ( 50 );
                } catch (InterruptedException e) {
                    throw new RuntimeException ( e );
                }
                loginUser(user.getUid());
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("MYTAG", e.getMessage() + " Cometchat user creation failed ");
            }
        });
    }

    private void loginUser(String password) {
        CometChat.login(password, AppKeys.COMETCHAT_AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("MYTAG", "Cometchat logged in");
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("MYTAG", e.getMessage());
            }
        });
    }

    private void onLoginClicked() {
        login.setOnClickListener(v -> {
            startActivity(new Intent(signup.this, com.example.mychat.ui.login.class));
            finishAffinity();
        });
    }

    private void setDefaultAvatar(User user) {
        user.setAvatar("https://img.icons8.com/?size=512&id=ABBSjQJK83zf&format=png");
        Picasso.get().load("https://img.icons8.com/?size=512&id=ABBSjQJK83zf&format=png").into(layout_image);
    }

    private void signupFirebase(User user, String pass) {
        Log.d("MYTAG", user.getName() + pass);
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(user.getName(), pass)
                .addOnSuccessListener(authResult -> loginFirebase(user, pass));
    }

    private void addUserToFirebaseDatabase(User user) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        usersRef.child(userId).setValue(user).addOnSuccessListener(unused -> {
            Log.d("MYATG", "User added to database");
            setProgressbarView(false);
            startActivity(new Intent(signup.this, main_dashboard.class));
            finish();
        }).addOnFailureListener(e -> {
            setProgressbarView(false);
            Toast.makeText(getApplicationContext(), "Failed to add user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loginFirebase(User user, String pass) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("MYTAG", "Logged in user " + FirebaseAuth.getInstance().getCurrentUser().toString());
            FirebaseAuth.getInstance().signOut();
        }
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(user.getName(), pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addUserToFirebaseDatabase(user);
                    }
                });
    }

}
