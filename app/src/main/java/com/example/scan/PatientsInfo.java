package com.example.scan;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class PatientsInfo extends AppCompatActivity {
    EditText Fname,Bday,Age,Sex,ContactNo;
    RadioButton R1,R2;
    Bundle bundle;
    RecyclerView recyclerView;
    ImageView selectImage , imageView;
    ImageButton Edit, backButton, deleteButton, update,addDiagnosisBtn,DiagnosisBack;
    TextView Name,age,bday,sex,contact;
    NestedScrollView form,editform;
    LinearLayout top,DiagnosisFrom,DiagnosisUpdateForm;
    ProgressBar progressBar;
    EditText diagnosisText,Remarks,Treatments;
    ImageView DiagnosisImage;
    Button addButton;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    StorageReference storageReference;
    Uri imageUri;
    TextView Uri;
    List<Diagnosis> diagnosisList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patients_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        hideSystemUI();

        recyclerView = findViewById(R.id.recycleView);
        Name = findViewById(R.id.Name);
        bday = findViewById(R.id.Bday);
        age = findViewById(R.id.Age);
        sex = findViewById(R.id.sex);
        contact = findViewById(R.id.Contact);
        form = findViewById(R.id.Form);
        editform = findViewById(R.id.editform);
        backButton = findViewById(R.id.backButton);
        update = findViewById(R.id.done);
        deleteButton = findViewById(R.id.deleteButton);
        progressBar = findViewById(R.id.progressBar);
        addDiagnosisBtn = findViewById(R.id.AddDiagnosisButton);
        DiagnosisFrom = findViewById(R.id.Diagnosisfrom);
        top = findViewById(R.id.top);
        DiagnosisBack = findViewById(R.id.diagnosisback);


        diagnosisText =findViewById(R.id.editTextDiagnosis);
        Remarks = findViewById(R.id.remarks);
        Treatments = findViewById(R.id.treatment);
        DiagnosisImage  = findViewById(R.id.diagnosisImg);
        addButton = findViewById(R.id.add);
        


        Fname = findViewById(R.id.name);
        Bday = findViewById(R.id.editBirthday);
        Age = findViewById(R.id.editTextAge);
        ContactNo = findViewById(R.id.contact_No);
        Edit = findViewById(R.id.edit);

        //RadioButton
        R1 = findViewById(R.id.rdmale);
        R2 = findViewById(R.id.rdfemale);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        DiagnosisImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String diagnosisImageUrl = bundle.getString("DiagnosisImage");
            Name.setText(bundle.getString("Name"));
            bday.setText(bundle.getString("BDay"));
            contact.setText(bundle.getString("ContactNo"));
            age.setText(bundle.getString("Age"));
            sex.setText(bundle.getString("Sex"));


            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            String patientId = bundle.getString("PatientID");
            String userId = auth.getCurrentUser().getUid();

            DatabaseReference doctorRef = database.getReference("users").child(userId).child("patients").child(patientId).child("diagnosis");

            doctorRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    diagnosisList = new ArrayList<>();
                    for (DataSnapshot diagnosisSnapshot : snapshot.getChildren()) {
                        Diagnosis diagnosis = diagnosisSnapshot.getValue(Diagnosis.class);
                        if (diagnosis != null) {
                            diagnosisList.add(diagnosis);
                        }

                    }
                    DiagnosisAdapter diagnosisAdapter = new DiagnosisAdapter(PatientsInfo.this, diagnosisList);
                    recyclerView.setAdapter(diagnosisAdapter);
                    diagnosisAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(PatientsInfo.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                database = FirebaseDatabase.getInstance();
                reference = database.getReference();
                String DiagnosisTxt = diagnosisText.getText().toString().toUpperCase().trim();
                String TreatmentTxt = Treatments.getText().toString().trim();
                String RemarkTxt = Remarks.getText().toString().trim();

                Bundle bundle = getIntent().getExtras();
                if(bundle != null){
                    String patientId = bundle.getString("PatientID");
                    String userId = auth.getCurrentUser().getUid();
                    DatabaseReference userRef = reference.child("users").child(userId);
                    DatabaseReference doctorRef = database.getReference("users")
                            .child(userId).child("patients").child(patientId).child("diagnosis");

                    String DiagnosisId = reference.child("diagnosis").push().getKey();
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

                                                Map<String,Object> diagnosisData = new HashMap<>();
                                                diagnosisData.put("DiagnosisId",DiagnosisId);
                                                diagnosisData.put("DiagnosisText", DiagnosisTxt);
                                                diagnosisData.put("DiagnosisImage", imageUrl);
                                                diagnosisData.put("DiagnosisDate", currentDate);
                                                diagnosisData.put("PatientID",patientId);

                                                Map<String, Object> TreatmentData = new HashMap<>();
                                                TreatmentData.put("Remarks", RemarkTxt);
                                                TreatmentData.put("TreatmentTxt",TreatmentTxt);
                                                TreatmentData.put("TreatmentDate",currentDate);
                                                TreatmentData.put("TreatmentID",TreatmentID);
                                                TreatmentData.put("TreatmentImage", imageUrl);
                                                TreatmentData.put("PatientID",patientId);
                                                TreatmentData.put("DiagnosisId",DiagnosisId);


                                                reference.child("users").child(userId).child("patients").child(patientId).child("diagnosis").child(DiagnosisId).setValue(diagnosisData)
                                                        .addOnSuccessListener(aVoid -> {



                                                        })
                                                        .addOnFailureListener(e -> {;

                                                        });


                                                        reference.child("users").child(userId).child("patients").child(patientId).child("diagnosis").child(DiagnosisId).child("Treatments").child(TreatmentID).setValue(TreatmentData)
                                                        .addOnSuccessListener(aVoid -> {
                                                            progressBar.setVisibility(View.GONE);
                                                            Intent intent = new Intent(PatientsInfo.this, MedicalHistory.class);
                                                            startActivity(intent);
                                                            finish();

                                                        })
                                                        .addOnFailureListener(e -> {;
                                                            Toast.makeText(PatientsInfo.this, "Failed to add Treatment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            Log.e("TreatmentError", e.getMessage());
                                                        });


                                            });
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle any errors
                                            Log.e("Upload Error", e.getMessage());
                                        });
                            }else{
                                Map<String, Object> diagnosisData = new HashMap<>();
                                diagnosisData.put("DiagnosisId",DiagnosisId);
                                diagnosisData.put("DiagnosisText", "Need to update");
                                diagnosisData.put("DiagnosisImage", " ");
                                diagnosisData.put("DiagnosisDate", currentDate);
                                diagnosisData.put("PatientID",patientId);

                                Map<String, Object> TreatmentData = new HashMap<>();
                                TreatmentData .put("Remarks", "Need to Update");
                                TreatmentData .put("TreatmentTxt","Need to update");
                                TreatmentData .put("TreatmentDate",currentDate);
                                TreatmentData .put("TreatmentID",TreatmentID);
                                TreatmentData.put("TreatmentImage", " ");
                                TreatmentData.put("PatientID",patientId);
                                TreatmentData.put("DiagnosisId",DiagnosisId);


                                reference.child("users").child(userId).child("patients").child(patientId).child("diagnosis").child(DiagnosisId).setValue(diagnosisData)
                                        .addOnSuccessListener(aVoid -> {


                                        })
                                        .addOnFailureListener(e -> {;

                                        });
                                reference.child("users").child(userId).child("patients").child(patientId).child("diagnosis").child(DiagnosisId).child("Treatments").child(TreatmentID).setValue(TreatmentData)
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

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                visibleEditForm();

                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    Fname.setText(bundle.getString("Name"));
                    Bday.setText(bundle.getString("BDay"));
                    Age.setText(bundle.getString("Age"));
                    String Sex = bundle.getString("Sex");
                    ContactNo.setText(bundle.getString("ContactNo"));
                    if (bundle.getString("Sex").equals("Male") ) {
                        R1.setChecked(true);
                    } else if (bundle.getString("Sex").equals("Female") ){
                        R2.setChecked(true);
                    }

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            visibleFrom();
                            progressBar.setVisibility(View.VISIBLE);
                            String userId = auth.getInstance().getCurrentUser().getUid();
                            String patientId = bundle.getString("PatientID");
                            database = FirebaseDatabase.getInstance();
                            reference = database.getReference("users").child(userId).child("patients").child(patientId);
                            storageReference = FirebaseStorage.getInstance().getReference("images/" + System.currentTimeMillis());

                            Map<String, Object> patients = new HashMap<>();
                            patients.put("ContactNo", ContactNo.getText().toString());
                            patients.put("Name", Fname.getText().toString().toUpperCase());
                            patients.put("BDay", Bday.getText().toString());
                            patients.put("Age", Age.getText().toString());

                            if (R1.isChecked()) {
                                patients.put("Sex", "Male");
                            } else if (R2.isChecked()) {
                                patients.put("Sex", "Female");
                            }

                            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                            patients.put("Date", currentDate);

                            reference.updateChildren(patients)
                                    .addOnSuccessListener(aVoid -> {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(PatientsInfo.this, "Medical History of the Patient is Updated ", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(PatientsInfo.this, MedicalHistory.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(PatientsInfo.this, "Failed to  Updated the Patient Medical History : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });


                        }
                    });

                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientsInfo.this, MedicalHistory.class);
                startActivity(intent);
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PatientsInfo.this);
                builder.setTitle("Are you Sure?");
                builder.setMessage("Deleted data can't be Undo.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String userId = auth.getInstance().getCurrentUser().getUid();

                        String patientId = bundle.getString("PatientID");
                        database = FirebaseDatabase.getInstance();
                        reference = database.getReference("users").child(userId).child("patients").child(patientId);
                        reference.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Deletion successful
                                Intent intent = new Intent(PatientsInfo.this, MedicalHistory.class);
                                startActivity(intent);
                                finish();

                            } else {
                                // Deletion failed
                                Toast.makeText(getApplicationContext(), "Failed to delete patient data", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(getApplicationContext(), "Patient data deleted", Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        });


        addDiagnosisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiagnosisFrom();
            }
        });

        DiagnosisBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InveDiagnosisFrom();
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
                DiagnosisImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private  void DiagnosisFrom(){
        DiagnosisFrom.setVisibility(View.VISIBLE);
        form.setVisibility(View.GONE);
        top.setVisibility(View.GONE);
    }
    private void InveDiagnosisFrom(){
        DiagnosisFrom.setVisibility(View.GONE);
        form.setVisibility(View.VISIBLE);
        top.setVisibility(View.VISIBLE);
    }
    private  void visibleEditForm(){
        editform.setVisibility(View.VISIBLE);
        update.setVisibility(View.VISIBLE);
        form.setVisibility(View.GONE);
        Edit.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        }

        private void visibleFrom(){
        editform.setVisibility(View.GONE);
        update.setVisibility(View.GONE);
        form.setVisibility(View.VISIBLE);
        Edit.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
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

