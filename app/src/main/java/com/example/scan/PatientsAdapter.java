package com.example.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.PatientViewHolder>{
    private List<Patient> patientList;
    private List<Diagnosis> diagnosisList;
    private Context context;

    public PatientsAdapter( List<Patient> patientList) {
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patients_item,parent,false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patientList.get(position);

        holder.Name.setText(patient.getName());
        holder.Date.setText(String.valueOf(patient.getDate()));
        holder.Specialist.setText(patient.getSpecialist());

        Map<String, Diagnosis> diagnosisMap = patient.getDiagnosis();

        if (diagnosisMap != null && !diagnosisMap.isEmpty()) {
            // Assuming you want to show the first diagnosis (can be improved to get the latest diagnosis)
            Diagnosis latestDiagnosis = diagnosisMap.values().iterator().next();
            holder.Diagnosis.setText(latestDiagnosis.getDiagnosisText());
        } else {
            holder.Diagnosis.setText("None");
        }

        holder.cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name,Date,Specialist,Age,Bday,Sex,patientID,
                        contact;

                Name = patientList.get(holder.getAdapterPosition()).getName();
                Date = patientList.get(holder.getAdapterPosition()).getDate();
                Specialist = patientList.get(holder.getAdapterPosition()).getSpecialist();
                Age = patientList.get(holder.getAdapterPosition()).getAge();
                Bday = patientList.get(holder.getAdapterPosition()).getBDay();
                Sex = patientList.get(holder.getAdapterPosition()).getSex();
                patientID = patientList.get(holder.getAdapterPosition()).getPatientID();
                contact = patientList.get(holder.getAdapterPosition()).getContactNo();


                Bundle bundle = new Bundle();
                bundle.putString("Name", Name);
                bundle.putString("Date", Date);
                bundle.putString("Specialist", Specialist);
                bundle.putString("ContactNo",contact);
                bundle.putString("Age",Age);
                bundle.putString("BDay",Bday);
                bundle.putString("Sex",Sex);
                bundle.putString("PatientID", patientID);

                Intent intent = new Intent(holder.itemView.getContext(),PatientsInfo.class);
                intent.putExtras(bundle);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }
    class  PatientViewHolder extends RecyclerView.ViewHolder{
        TextView Name,Date,Diagnosis,Specialist;
        CardView cardButton, cardDiagnosis;
        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);

            cardButton = itemView.findViewById(R.id.cardbtn);
            Name = (TextView)itemView.findViewById(R.id.name);
            Date = (TextView)itemView.findViewById(R.id.Date);
            Diagnosis = (TextView)itemView.findViewById(R.id.diagnosis);
            Specialist = (TextView)itemView.findViewById(R.id.specialist);

        }
    }
}
