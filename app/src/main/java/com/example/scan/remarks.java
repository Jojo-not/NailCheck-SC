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
import android.widget.LinearLayout;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class remarks extends AppCompatActivity {

    private ImageButton ButtonBack,add, back;
    private   treatmentAdapter TreatmentAdapter;
    private   List<treatment> treatmentList;
    private   RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private LinearLayout loadData,addlayout;
    private EditText remarks,Treatmenttxt;
    private ImageView diseaseImage;
    private Button AddTreatment;
    StorageReference storageReference;
    android.net.Uri imageUri;
    TextView Uri;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_remarks);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        hideSystemUI();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        ButtonBack = findViewById(R.id.treatmentBack);
        recyclerView= findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        add  = findViewById(R.id.Add);
        addlayout = findViewById(R.id.TreatmentForm);
        loadData = findViewById(R.id.treatmentload);
        back =findViewById(R.id.Back);
        diseaseImage = findViewById(R.id.diagnosisImg);
        remarks = findViewById(R.id.Remarks);
        Treatmenttxt = findViewById(R.id.treatment);
        AddTreatment = findViewById(R.id.treatmentAdd);
        progressBar = findViewById(R.id.progressBar);




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gone();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Visible();
            }
        });
        ButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(remarks.this,MedicalHistory.class);
                startActivity(intent);
                finish();

            }
        });

        diseaseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });



        progressBar.setVisibility(View.VISIBLE);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String userId = auth.getCurrentUser().getUid();
            String DiagnosisID = bundle.getString("DiagnosisId");
            String patientID = bundle.getString("PatientID");
            DatabaseReference doctorRef = database.getReference("users").child(userId).child("patients").child(patientID).child("diagnosis").child(DiagnosisID).child("Treatments");

            doctorRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<treatment> treatmentList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        treatment Treatment = dataSnapshot.getValue(treatment.class);
                        treatmentList.add(Treatment);
                    }
                    TreatmentAdapter = new treatmentAdapter (remarks.this,treatmentList);
                    recyclerView.setAdapter(TreatmentAdapter);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", error.getMessage());
                }
            });


        }

        AddTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                database = FirebaseDatabase.getInstance();
                reference = database.getReference();
                String remarkTXT = remarks.getText().toString().trim();
                String TreatmentTXT = Treatmenttxt.getText().toString().trim();

                if (bundle !=null){
                    String userId = auth.getCurrentUser().getUid();
                    String DiagnosisID = bundle.getString("DiagnosisId");
                    String patientID = bundle.getString("PatientID");

                    DatabaseReference doctorRef = database.getReference("users")
                            .child(userId).child("patients").child(patientID).child("diagnosis").child(DiagnosisID).child("Treatments");

                    String TreatmentID = reference.child("Treatments").push().getKey();

                    doctorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                            if (imageUri != null) {


                                storageReference = FirebaseStorage.getInstance().getReference("images/" + System.currentTimeMillis());
                                storageReference.putFile(imageUri)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            // Get the image URL after successful upload
                                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                String imageUrl = uri.toString();
                                                if(TreatmentTXT != null){

                                                    Map<String, Object> TreatmentData = new HashMap<>();
                                                    TreatmentData.put("DiagnosisId",DiagnosisID);
                                                    TreatmentData.put("PatientID",patientID);
                                                    TreatmentData.put("Remarks",remarkTXT);
                                                    TreatmentData.put("TreatmentID",TreatmentID);
                                                    TreatmentData.put("TreatmentTxt",TreatmentTXT);
                                                    TreatmentData.put("TreatmentImage", imageUrl);
                                                    TreatmentData.put("TreatmentDate",currentDate);

                                                    reference.child("users").child(userId).child("patients").child(patientID).child("diagnosis").child(DiagnosisID).child("Treatments").child(TreatmentID).setValue(TreatmentData)
                                                            .addOnSuccessListener(aVoid -> {
                                                                progressBar.setVisibility(View.GONE);
                                                                Gone();
                                                                Intent intent = new Intent(remarks.this, MedicalHistory.class);
                                                                startActivity(intent);
                                                                finish();

                                                            })
                                                            .addOnFailureListener(e -> {;
                                                                Toast.makeText(remarks.this, "Failed to add Treatment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                Log.e("TreatmentError", e.getMessage());
                                                            });

                                                }else {


                                                    Map<String, Object> TreatmentData = new HashMap<>();
                                                    TreatmentData.put("DiagnosisId",DiagnosisID);
                                                    TreatmentData.put("PatientID",patientID);
                                                    TreatmentData.put("Remarks",remarkTXT);
                                                    TreatmentData.put("TreatmentID",TreatmentID);
                                                    TreatmentData.put("TreatmentTxt","No Treatment");
                                                    TreatmentData.put("TreatmentImage", imageUrl);
                                                    TreatmentData.put("TreatmentDate",currentDate);

                                                    reference.child("users").child(userId).child("patients").child(patientID).child("diagnosis").child(DiagnosisID).child("Treatments").child(TreatmentID).setValue(TreatmentData)
                                                            .addOnSuccessListener(aVoid -> {
                                                                progressBar.setVisibility(View.GONE);
                                                                Gone();
                                                                Intent intent = new Intent(remarks.this, MedicalHistory.class);
                                                                startActivity(intent);
                                                                finish();

                                                            })
                                                            .addOnFailureListener(e -> {;
                                                                Toast.makeText(remarks.this, "Failed to add Treatment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                Log.e("TreatmentError", e.getMessage());
                                                            });
                                                }




                                            });
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle any errors
                                            Log.e("Upload Error", e.getMessage());
                                        });
                            }else{
                                Map<String, Object> TreatmentData = new HashMap<>();
                                TreatmentData.put("DiagnosisId",DiagnosisID);
                                TreatmentData.put("PatientID",patientID);
                                TreatmentData.put("Remarks",remarkTXT);
                                TreatmentData.put("TreatmentID",TreatmentID);
                                TreatmentData.put("TreatmentTxt",TreatmentTXT);
                                TreatmentData.put("TreatmentImage", " ");
                                TreatmentData.put("TreatmentDate",currentDate);


                                reference.child("users").child(userId).child("patients").child(patientID).child("diagnosis").child(DiagnosisID).child("Treatments").child(TreatmentID).setValue(TreatmentData)
                                        .addOnSuccessListener(aVoid -> {


                                        })
                                        .addOnFailureListener(e -> {;

                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
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
                diseaseImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    private void Visible(){
        loadData.setVisibility(View.GONE);
        addlayout.setVisibility(View.VISIBLE);
    }


    private void Gone(){
        loadData.setVisibility(View.VISIBLE);
        addlayout.setVisibility(View.GONE);
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