package com.example.scan;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class sign_in extends AppCompatActivity {


   private TextView txt_sign_in, txt_term,txt_txt;
   private CheckBox terms_condition;
   private ScrollView terms_box;
   private ImageButton hidePassword;
   private Button btn_sign_up, btn_agree,cancel;
   private static final String Tag = "sign_in";

   //database connection
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;

   private EditText signup_Name, signup_Email,signup_Password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        hideSystemUI();


        // Initialize Firebase Authentication and Database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        // check box button
        terms_condition = findViewById(R.id.check_terms);

        // ScrollView
        terms_box = findViewById(R.id.terms_box);

        // all button
        cancel = findViewById (R.id.cancel);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_agree = findViewById(R.id.btn_agree);
        hidePassword = findViewById(R.id.hidePasswordButton);


        // all text
        txt_sign_in = findViewById(R.id.txt_sign_in);
        txt_txt = findViewById(R.id.lbe);
        txt_term= findViewById(R.id.txt_terms_condition);

        boolean isChecked = terms_condition.isChecked();

        hidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signup_Password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    signup_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else {
                    signup_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terms_condition.setChecked(false);
                unloadscreen();

                if (isChecked){
                    btn_sign_up.setEnabled(true);
                }else {
                    btn_sign_up.setEnabled(false);

                }


            }

        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String name = signup_Name.getText().toString();
                String email = signup_Email.getText().toString();
                String password = signup_Password.getText().toString();

                if ( validateInputs(email, password, name)) {
                    registerUser(name, email, password);
                } else {
                    Toast.makeText(sign_in.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }


            }
        });


        terms_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadscreed();
            }
        });

        txt_term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadscreed();
            }
        });

        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unloadscreen();
                terms_condition.setChecked(true);

                if (isChecked){
                    btn_sign_up.setEnabled(false);
                }else {
                    btn_sign_up.setEnabled(true);

                }

            }
        });

        txt_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_in = new Intent(sign_in.this,sign_up.class);
                startActivity(sign_in);
                finish();
            }
        });

        signup_Name = findViewById(R.id.user_name);
        signup_Email = findViewById(R.id.email);
        signup_Password = findViewById(R.id.password);

    }
    // Validate inputs
    private boolean validateInputs(String email, String password, String name) {

        if (TextUtils.isEmpty(name)) {
            signup_Name.setError("Name is required.");
            signup_Name.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            signup_Email.setError("Email is required.");
            signup_Email.requestFocus();
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signup_Email.setError("Valid Email is required.");
            signup_Email.requestFocus();
            return false;
        }else if (TextUtils.isEmpty(password)) {
            signup_Password.setError("Password is required.");
            signup_Password.requestFocus();
            return false;
        }else if (password.length() < 6) {
            signup_Password.setError("Password must be at least 6 characters.");
            signup_Password.requestFocus();
            return false;
        }


        return true;
    }

    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Registration successful, get the FirebaseUser
                FirebaseUser user = mAuth.getCurrentUser();

                // Save user data to Firebase Realtime Database

                Helper email_key = new Helper(password, email, name);


                reference.child(user.getUid()).setValue(email_key).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(sign_in.this, "User account successfully created. Please verify your email ", Toast.LENGTH_SHORT).show();
                            if(user.isEmailVerified()){

                            }else {
                                mAuth.signOut();
                                user.sendEmailVerification();

                                showAlertDialog();

                            }

                        }else {

                            Toast.makeText(sign_in.this, "Account failed created.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            } else {

                try{
                    throw task.getException();
                }catch (FirebaseAuthWeakPasswordException e){
                    signup_Password.setError("Your password is too weak. kindly use a mix of alphabet, numbers and special characters");

                }catch (FirebaseAuthInvalidCredentialsException e){
                    signup_Email.setError("Your email is invalid or already in use.");

                }catch (Exception e){
                    Log.e(Tag, e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(sign_in.this);
        builder.setTitle("Email not Verified");
        builder.setMessage("Please verify your email now. You can not sign in without email verification.");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void unloadscreen() {
        terms_box.setVisibility(View.INVISIBLE);
        btn_sign_up.setVisibility(View.VISIBLE);
        terms_condition.setVisibility(View.VISIBLE);
        txt_term.setVisibility(View.VISIBLE);
        txt_txt.setVisibility(View.VISIBLE);
        txt_sign_in.setVisibility(View.VISIBLE);
    }

    private void loadscreed() {
        terms_box.setVisibility(View.VISIBLE);
        btn_sign_up.setVisibility(View.INVISIBLE);
        terms_condition.setVisibility(View.INVISIBLE);
        txt_term.setVisibility(View.INVISIBLE);
        txt_txt.setVisibility(View.INVISIBLE);
        txt_sign_in.setVisibility(View.INVISIBLE);

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