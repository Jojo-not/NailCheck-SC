package com.example.scan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class treatmentUpdate extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    StorageReference storageReference;
    android.net.Uri imageUri;
    TextView Uri;
    ImageView TreatmentImage;
    EditText  TreatmentTxt,Remarks;
    Button Done;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_treatment_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        TreatmentImage = findViewById(R.id.diagnosisImg);
        TreatmentTxt = findViewById(R.id.treatment);
        Remarks = findViewById(R.id.remarks);
        Done = findViewById(R.id.add);
        progressBar = findViewById(R.id.progressBar);

       TreatmentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            TreatmentTxt.setText(bundle.getString("TreatmentTxt"));
            Remarks.setText(bundle.getString("Remarks"));
            // Load the diagnosis image using Glide
            String diagnosisImageUrl = bundle.getString("TreatmentImage");
            Glide.with(this)
                    .load(diagnosisImageUrl)
                    .into(TreatmentImage);

        }
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                if (TreatmentTxt.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(treatmentUpdate.this, "Diagnosis text cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String patientId = bundle.getString("PatientID");
                String diagnosisId = bundle.getString("DiagnosisId");
                String TreatmentId = bundle.getString("TreatmentID");
                String ImageTreatmentUri = bundle.getString("TreatmentImage");

                if (patientId == null || diagnosisId == null || TreatmentId == null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(treatmentUpdate.this, "Invalid patient, diagnosis, or treatment ID.", Toast.LENGTH_SHORT).show();
                    return;
                }

                reference = FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)
                        .child("patients")
                        .child(patientId)
                        .child("diagnosis")
                        .child(diagnosisId)
                        .child("Treatments")
                        .child(TreatmentId);

                if (imageUri != null) {
                    storageReference = FirebaseStorage.getInstance().getReference("images/" + System.currentTimeMillis());
                    storageReference.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        String imageUrl = uri.toString();
                                        Map<String, Object> TreatmentData = new HashMap<>();
                                        TreatmentData.put("TreatmentTxt", TreatmentTxt.getText().toString());
                                        TreatmentData.put("TreatmentImage", imageUrl);
                                        TreatmentData.put("Remarks", Remarks.getText().toString());

                                        reference.updateChildren(TreatmentData)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(treatmentUpdate.this, "Medical History updated.", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    startActivity(new Intent(treatmentUpdate.this, MedicalHistory.class));
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(treatmentUpdate.this, "Update failed.", Toast.LENGTH_SHORT).show();
                                                    Log.e("Database Update Error", e.getMessage());
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(treatmentUpdate.this, "Failed to get image URL.", Toast.LENGTH_SHORT).show();
                                        Log.e("URL Retrieval Error", e.getMessage());
                                    }))
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(treatmentUpdate.this, "Image upload failed.", Toast.LENGTH_SHORT).show();
                                Log.e("Image Upload Error", e.getMessage());
                            });
                } else {
                    Map<String, Object> TreatmentData = new HashMap<>();
                    TreatmentData.put("TreatmentTxt", TreatmentTxt.getText().toString());
                    TreatmentData.put("TreatmentImage", ImageTreatmentUri);
                    TreatmentData.put("Remarks", Remarks.getText().toString());

                    reference.updateChildren(TreatmentData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(treatmentUpdate.this, "Medical History updated.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(treatmentUpdate.this, MedicalHistory.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(treatmentUpdate.this, "Update failed.", Toast.LENGTH_SHORT).show();
                                Log.e("Database Update Error", e.getMessage());
                            });
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                TreatmentImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}