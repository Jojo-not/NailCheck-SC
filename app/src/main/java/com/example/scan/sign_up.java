package com.example.scan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class sign_up extends AppCompatActivity {
    private TextView txt_sign_up, forgotPassword;
    private Button btn_sign_in;
    private ImageButton hidepassword;
    private EditText signin_email,signin_password;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private static final String Tag = "sign_up";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        hideSystemUI();

        hidepassword = findViewById(R.id.hidepassword);
        forgotPassword = findViewById(R.id.fg_password);
        txt_sign_up = findViewById(R.id.txt_Sign_Up);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        progressBar = findViewById(R.id.progressBar);
        signin_email = findViewById(R.id.email);
        signin_password = findViewById(R.id.password);
        auth = FirebaseAuth.getInstance();


    hidepassword.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(signin_password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                signin_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }else {
                signin_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        }
    });

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = signin_email.getText().toString();
                String password = signin_password.getText().toString();

                if (email.isEmpty()) {
                    signin_email.setError("Email cannot be empty");
                    signin_email.requestFocus();

                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    signin_email.setError("Valid email is required");
                    signin_email.requestFocus();
                }else if (password.isEmpty()) {
                    signin_password.setError("Password cannot be empty");
                    signin_password.requestFocus();

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    checkUser(email,password);
                }
            }
        });


        txt_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_up = new Intent(sign_up.this,sign_in.class);
                startActivity(sign_up);
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(sign_up.this, "You can reset your password", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(sign_up.this,forgot_password.class);
                startActivity(intent);
                finish();

            }
        });


    }

    private void checkUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(sign_up.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        Toast.makeText(sign_up.this, "You are now signed in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(sign_up.this, home.class);
                        startActivity(intent);
                        finish();
                    } else if (user != null) {
                        user.sendEmailVerification();
                        auth.signOut();
                        showAlertDialog(); // Show a dialog asking the user to verify their email
                    }
                } else {
                    handleSignInError(task.getException());
                }
            }
        });
    }

    private void handleSignInError(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(this, "User not found. Please check your email.", Toast.LENGTH_LONG).show();
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(this, "Invalid credentials. Please check your password or email.", Toast.LENGTH_LONG).show();
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            Toast.makeText(this, "Email is already in use.", Toast.LENGTH_LONG).show();
        } else if (exception instanceof FirebaseNetworkException) {
            Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Authentication failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(sign_up.this);
        builder.setTitle("Email not Verified");
        builder.setMessage("Please verify your email now. You can not sign in without email verification.");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null){
            Toast.makeText(this, "Already Signed In!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(sign_up.this, home.class);
            startActivity(intent);
            finish();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}