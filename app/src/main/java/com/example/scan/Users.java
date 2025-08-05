package com.example.scan;

import java.util.List;
import java.util.Map;

public class Users {
    String name;
    private Map<String, Boolean> Patients;

    public Users(){

    }

    public Users(String name, Map<String, Boolean> patients) {
        this.name = name;
       this.Patients = patients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getPatients() {
        return Patients;
    }

    public void setPatients(Map<String, Boolean> patients) {
        Patients = patients;
    }
}
