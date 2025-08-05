package com.example.scan;

import java.util.Map;

public class Patient {
    String Name,Date,Specialist,Age,BDay,PatientID,ContactNo,Sex;
    private Map<String, Diagnosis> diagnosis;
    Patient(){

    }

    public Patient(String name, String date, String specialist, String age, String BDay, String patientID, String contactNo, String sex, Map<String, Diagnosis> diagnosis) {
        Name = name;
        Date = date;
        Specialist = specialist;
        Age = age;
        this.BDay = BDay;
        this.PatientID = patientID;
        ContactNo = contactNo;
        Sex = sex;
        this.diagnosis = diagnosis;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getSpecialist() {
        return Specialist;
    }

    public void setSpecialist(String specialist) {
        Specialist = specialist;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getBDay() {
        return BDay;
    }

    public void setBDay(String BDay) {
        this.BDay = BDay;
    }

    public String getPatientID() {
        return PatientID;
    }

    public void setPatientID(String patientID) {
        this.PatientID = patientID;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }
    
    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        this.Sex = sex;
    }

    public Map<String, Diagnosis> getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(Map<String, Diagnosis> diagnosis) {
        this.diagnosis = diagnosis;
    }
}