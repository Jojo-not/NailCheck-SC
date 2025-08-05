package com.example.scan;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MedicalForm extends AppCompatActivity {

    ImageButton backButton ,calendarPicker,Save;
    ImageView selectImage;
    RadioButton R1,R2;
    EditText Contact, diagnosis, Firstname,Bday,Age,remarks,treatment;
    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference storageReference;
    FirebaseAuth auth;
    Uri imageUri;
    TextView Uri;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medical_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewss), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        hideSystemUI();

        backButton = findViewById(R.id.backButton);
        calendarPicker = findViewById(R.id.calendarPickerButton);
        Save = findViewById(R.id.done);
        progressBar = findViewById(R.id.progressBar);



        Firstname = findViewById(R.id.editTextFirstName);
        Bday = findViewById(R.id.editBirthday);
        Age = findViewById(R.id.editTextAge);
        diagnosis = findViewById(R.id.editTextDiagnosis);
        Contact = findViewById(R.id.editTextContact);
        selectImage  = findViewById(R.id.diagnosisImg);
        remarks = findViewById(R.id.remarks);
        treatment = findViewById(R.id.treatment);
        //RadioButton
        R1 = findViewById(R.id.rdmale);
        R2 = findViewById(R.id.rdfemale);




        calendarPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MedicalForm.this,MedicalHistory.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                database = FirebaseDatabase.getInstance();
                reference = database.getReference();
                String name = Firstname.getText().toString().trim().toUpperCase();
                String contact = Contact.getText().toString().trim();
                String Diagnosis = diagnosis.getText().toString().trim().toUpperCase();
                String bday = Bday.getText().toString().trim();
                String age = Age.getText().toString().trim();
                String remark = remarks.getText().toString().trim();
                String treatments = treatment.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Firstname.setError("Name cannot be empty");
                    Firstname.requestFocus();
                } else if (TextUtils.isEmpty(contact)) {
                    Contact.setError("Contact cannot be empty");
                    Contact.requestFocus();
                } else if (TextUtils.isEmpty(bday)) {
                    Bday.setError("Birthday cannot be empty");
                    Bday.requestFocus();
                } else if (contact.length() < 11) {
                    Contact.setError("Contact number must be at least 11 digits");
                    Contact.requestFocus();

                }else if (contact.length() > 11) {
                    Contact.setError("Contact number must be at least 11 digits");
                    Contact.requestFocus();

                }   else if (TextUtils.isEmpty(age)) {
                    Age.setError("Age cannot be empty");
                    Age.requestFocus();
                }else {

                    String userId = auth.getInstance().getCurrentUser().getUid();
                    String PatientId = reference.child("patients").push().getKey();
                    String DiagnosisId = reference.child("diagnosis").push().getKey();
                    String TreatmentID = reference.child("Treatments").push().getKey();
                    DatabaseReference userRef = reference.child("users").child(userId);
                    userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String doctorName = dataSnapshot.getValue(String.class);// Get the doctor's name
                                String M = "MALE",F = "FEMALE";

                                Map<String, String> patients = new HashMap<>();
                                patients.put("UserID", userId);
                                patients.put("Specialist", doctorName);
                                patients.put("ContactNo", contact);
                                patients.put("PatientID", PatientId);
                                patients.put("Name", name);
                                patients.put("BDay", bday);
                                patients.put("Age", age);

                                // Add conditional logic for answers
                                if (R1.isChecked()) {
                                    patients.put("Sex",M);
                                } else if (R2.isChecked()) {
                                    patients.put("Sex",F);
                                }


                                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                                patients.put("Date", currentDate); // Store only the date


                                if (imageUri != null) {

                                    storageReference = FirebaseStorage.getInstance().getReference("images/" + System.currentTimeMillis());
                                    storageReference.putFile(imageUri)
                                            .addOnSuccessListener(taskSnapshot -> {
                                                // Get the image URL after successful upload
                                                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                    String imageUrl = uri.toString();

                                                    Map<String, Object> diagnosisData = new HashMap<>();
                                                    diagnosisData.put("DiagnosisId",DiagnosisId);
                                                    diagnosisData.put("DiagnosisText", Diagnosis);
                                                    diagnosisData.put("DiagnosisImage", imageUrl);
                                                    diagnosisData.put("DiagnosisDate", currentDate);
                                                    diagnosisData.put("PatientID",PatientId);

                                                    Map<String, Object> TreatmentData = new HashMap<>();
                                                    TreatmentData .put("Remarks", remark);
                                                    TreatmentData .put("TreatmentTxt",treatments);
                                                    TreatmentData .put("TreatmentDate",currentDate);
                                                    TreatmentData .put("TreatmentID",TreatmentID);
                                                    TreatmentData.put("TreatmentImage",imageUrl);
                                                    TreatmentData.put("PatientID",PatientId);
                                                    TreatmentData.put("DiagnosisId",DiagnosisId);




                                                    // Add patient data to Firebase
                                                    reference.child("users").child(userId).child("patients").child(PatientId).setValue(patients)
                                                            .addOnSuccessListener(aVoid -> {
                                                                progressBar.setVisibility(View.GONE);
                                                                Intent intent = new Intent(MedicalForm.this, MedicalHistory.class);
                                                                startActivity(intent);
                                                                finish();

                                                                Toast.makeText(MedicalForm.this, "Patient added successfully", Toast.LENGTH_SHORT).show();
                                                            })
                                                            .addOnFailureListener(e -> {;
                                                                Toast.makeText(MedicalForm.this, "Failed to add patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            });

                                                    reference.child("users").child(userId).child("patients").child(PatientId).child("diagnosis").child(DiagnosisId).setValue(diagnosisData)
                                                            .addOnSuccessListener(aVoid -> {


                                                            })
                                                            .addOnFailureListener(e -> {;
                                                                Toast.makeText(MedicalForm.this, "Failed to add patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            });
                                                    reference.child("users").child(userId).child("patients").child(PatientId).child("diagnosis").child(DiagnosisId).child("Treatments").child(TreatmentID).setValue(TreatmentData)
                                                            .addOnSuccessListener(aVoid -> {


                                                            })
                                                            .addOnFailureListener(e -> {;
                                                                Toast.makeText(MedicalForm.this, "Failed to add patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            });

                                                });
                                            })

                                            .addOnFailureListener(e -> {
                                                // Handle any errors
                                                Log.e("Upload Error", e.getMessage());
                                            });


                                } else{
                                    Map<String, Object> diagnosisData = new HashMap<>();
                                    diagnosisData.put("DiagnosisId",DiagnosisId);
                                    diagnosisData.put("DiagnosisText", "Need to update");
                                    diagnosisData.put("DiagnosisImage", " ");
                                    diagnosisData.put("DiagnosisDate", currentDate);
                                    diagnosisData.put("PatientID",PatientId);

                                    Map<String, Object> TreatmentData = new HashMap<>();
                                    TreatmentData .put("Remarks", "Need to Update");
                                    TreatmentData .put("TreatmentTxt","Need to update");
                                    TreatmentData .put("TreatmentDate",currentDate);
                                    TreatmentData .put("TreatmentID",TreatmentID);
                                    TreatmentData.put("TreatmentImage", " ");
                                    TreatmentData.put("PatientID",PatientId);
                                    TreatmentData.put("DiagnosisId",DiagnosisId);


                                    reference.child("users").child(userId).child("patients").child(PatientId).setValue(patients)
                                            .addOnSuccessListener(aVoid -> {

                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(MedicalForm.this, MedicalHistory.class);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(MedicalForm.this, "Patient added successfully", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {;
                                                Toast.makeText(MedicalForm.this, "Failed to add patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });

                                    reference.child("users").child(userId).child("patients").child(PatientId).child("diagnosis").child(DiagnosisId).setValue(diagnosisData)
                                            .addOnSuccessListener(aVoid -> {


                                            })
                                            .addOnFailureListener(e -> {;
                                                Toast.makeText(MedicalForm.this, "Failed to add patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                    reference.child("users").child(userId).child("patients").child(PatientId).child("diagnosis").child(DiagnosisId).child("Treatments").child(TreatmentID).setValue(TreatmentData)
                                            .addOnSuccessListener(aVoid -> {


                                            })
                                            .addOnFailureListener(e -> {;
                                                Toast.makeText(MedicalForm.this, "Failed to add patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });

                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("FirebaseError", databaseError.getMessage());


                        }

                    });


                }

            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
             imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                selectImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Bday.setText(String.valueOf(month+1)+"/"+ String.valueOf(day)+"/"+String.valueOf(year));

            }
        }, 2024, 2, 6);
        dialog.show();
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