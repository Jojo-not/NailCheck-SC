package com.example.scan;

public class treatment {
    String TreatmentID,TreatmentTxt,Remarks,TreatmentDate,TreatmentImage,PatientID,DiagnosisId;

    treatment(){
    }

    public treatment(String treatmentID, String treatmentTxt, String remarks, String treatmentDate, String treatmentImage, String patientID, String diagnosisId) {
        TreatmentID = treatmentID;
        TreatmentTxt = treatmentTxt;
        Remarks = remarks;
        TreatmentDate = treatmentDate;
        TreatmentImage = treatmentImage;
        PatientID = patientID;
        DiagnosisId = diagnosisId;
    }


    public String getPatientID() {
        return PatientID;
    }

    public void setPatientID(String patientID) {
        PatientID = patientID;
    }

    public String getDiagnosisId() {
        return DiagnosisId;
    }

    public void setDiagnosisId(String diagnosisId) {
        DiagnosisId = diagnosisId;
    }

    public String getTreatmentImage() {
        return TreatmentImage;
    }

    public void setTreatmentImage(String treatmentImage) {
        TreatmentImage = treatmentImage;
    }

    public String getTreatmentID() {
        return TreatmentID;
    }

    public void setTreatmentID(String treatmentID) {
        TreatmentID = treatmentID;
    }

    public String getTreatmentTxt() {
        return TreatmentTxt;
    }

    public void setTreatmentTxt(String treatmentTxt) {
        TreatmentTxt = treatmentTxt;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getTreatmentDate() {
        return TreatmentDate;
    }

    public void setTreatmentDate(String treatmentDate) {
        TreatmentDate = treatmentDate;
    }
}
