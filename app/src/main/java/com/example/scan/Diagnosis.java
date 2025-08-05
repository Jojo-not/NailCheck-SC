package com.example.scan;

import java.util.List;
import java.util.Map;

public class Diagnosis {
    private String DiagnosisText, DiagnosisId,PatientID;
    private String DiagnosisImage;
    private String DiagnosisDate ;


    public Diagnosis() {
        // Default constructor
    }

    public Diagnosis(String diagnosisText, String diagnosisID, String patientID, String diagnosisImage, String diagnosisDate) {
        DiagnosisText = diagnosisText;
        DiagnosisId = diagnosisID;
        PatientID = patientID;
        DiagnosisImage = diagnosisImage;
        DiagnosisDate = diagnosisDate;


    }

    public String getPatientID() {
        return PatientID;
    }

    public void setPatientID(String patientID) {
        PatientID = patientID;
    }

    public String getDiagnosisText() {
        return DiagnosisText;
    }

    public void setDiagnosisText(String diagnosisText) {
        DiagnosisText = diagnosisText;
    }

    public String getDiagnosisID() {
        return DiagnosisId;
    }

    public void setDiagnosisID(String diagnosisID) {
        DiagnosisId = diagnosisID;
    }

    public String getDiagnosisImage() {
        return DiagnosisImage;
    }

    public void setDiagnosisImage(String diagnosisImage) {
        DiagnosisImage = diagnosisImage;
    }

    public String getDiagnosisDate() {
        return DiagnosisDate;
    }

    public void setDiagnosisDate(String diagnosisDate) {
        DiagnosisDate = diagnosisDate;
    }

}
