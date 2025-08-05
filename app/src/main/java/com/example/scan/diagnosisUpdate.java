package com.example.scan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class diagnosisUpdate extends AppCompatActivity {

    ImageButton back;
    Button Done;
    EditText diagnosisText,TreatmentTxt,Remarks;
    ImageView DiagnosisImage;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    StorageReference storageReference;
    android.net.Uri imageUri;
    TextView Uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diagnosis_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        hideSystemUI();


        back = findViewById(R.id.diagnosisback);
        Done = findViewById(R.id.add);
        diagnosisText = findViewById(R.id.editTextDiagnosis);
        DiagnosisImage = findViewById(R.id.diagnosisImg);
        progressBar = findViewById(R.id.progressBar);

       DiagnosisImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(diagnosisUpdate.this,MedicalHistory.class);
                startActivity(intent);
                finish();
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // Set the diagnosis text
            diagnosisText.setText(bundle.getString("DiagnosisText"));

            // Load the diagnosis image using Glide
            String diagnosisImageUrl = bundle.getString("DiagnosisImage");
            Glide.with(this)
                    .load(diagnosisImageUrl)
                    .into(DiagnosisImage);

            // Set up the Done button listener
            Done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (diagnosisText.getText().toString().isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(diagnosisUpdate.this, "Diagnosis text cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    database = FirebaseDatabase.getInstance();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String patientId = bundle.getString("PatientID");
                    String diagnosisId = bundle.getString("DiagnosisId");

                    if (patientId == null || diagnosisId == null) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(diagnosisUpdate.this, "Invalid patient or diagnosis ID.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    reference = database.getReference("users")
                            .child(userId)
                            .child("patients")
                            .child(patientId)
                            .child("diagnosis")
                            .child(diagnosisId);

                    if (imageUri != null) {
                        // Upload the image to Firebase Storage
                        storageReference = FirebaseStorage.getInstance().getReference("images/" + System.currentTimeMillis());
                        storageReference.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Retrieve the download URL
                                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        String imageUrl = uri.toString();

                                        // Prepare data to update
                                        Map<String, Object> diagnosisData = new HashMap<>();
                                        diagnosisData.put("DiagnosisText", diagnosisText.getText().toString().toUpperCase());
                                        diagnosisData.put("DiagnosisImage", imageUrl);

                                        // Update the database
                                        reference.updateChildren(diagnosisData)
                                                .addOnSuccessListener(aVoid -> {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(diagnosisUpdate.this, "Medical History of the Patient is Updated.", Toast.LENGTH_SHORT).show();

                                                    // Navigate to MedicalHistory activity
                                                    Intent intent = new Intent(diagnosisUpdate.this, MedicalHistory.class);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(diagnosisUpdate.this, "Failed to Update the Patient Medical History: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // Handle upload error
                                    Log.e("Upload Error", e.getMessage());
                                    Toast.makeText(diagnosisUpdate.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // No new image, update only the diagnosis text
                        Map<String, Object> diagnosisData = new HashMap<>();
                        diagnosisData.put("DiagnosisText", diagnosisText.getText().toString());
                        diagnosisData.put("DiagnosisImage", diagnosisImageUrl);

                        reference.updateChildren(diagnosisData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(diagnosisUpdate.this, "Medical History of the Patient is Updated.", Toast.LENGTH_SHORT).show();

                                    // Navigate to MedicalHistory activity
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(diagnosisUpdate.this, MedicalHistory.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(diagnosisUpdate.this, "Failed to Update the Patient Medical History: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                DiagnosisImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
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