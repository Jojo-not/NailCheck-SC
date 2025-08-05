package com.example.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

public class DiagnosisAdapter extends RecyclerView.Adapter<DiagnosisAdapter.DiagnosisViewHolder> {

    private List<Diagnosis> diagnosisList;
    private Context context;

    public DiagnosisAdapter(Context context, List<Diagnosis> diagnosisList) {
        this.context = context;
        this.diagnosisList = diagnosisList;
    }

    @NonNull
    @Override
    public DiagnosisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.diagnosis, parent, false);
        return new DiagnosisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiagnosisViewHolder holder, int position) {
        Diagnosis diagnosis = diagnosisList.get(position);
        holder.diagnosisText.setText(diagnosis.getDiagnosisText());
        holder.date.setText(diagnosis.getDiagnosisDate());
        Glide.with(holder.itemView.getContext())
                .load(diagnosis.getDiagnosisImage())
                .into(holder.diagnosisImage);

        holder.cardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String DiagnosisID,DiagnosisText,DiagnosisDate,DiagnosisImage,PatientID;


                DiagnosisDate = diagnosisList.get(holder.getAdapterPosition()).getDiagnosisDate();
                DiagnosisID = diagnosisList.get(holder.getAdapterPosition()).getDiagnosisID();
                DiagnosisImage = diagnosisList.get(holder.getAdapterPosition()).getDiagnosisImage();
                DiagnosisText = diagnosisList.get(holder.getAdapterPosition()).getDiagnosisText();
                PatientID = diagnosisList.get(holder.getAdapterPosition()).getPatientID();


                Bundle bundle = new Bundle();
                bundle.putString("DiagnosisDate", DiagnosisDate);
                bundle.putString("DiagnosisText", DiagnosisText);
                bundle.putString("DiagnosisImage",DiagnosisImage);
                bundle.putString("DiagnosisId",DiagnosisID);
                bundle.putString("PatientID",PatientID);

                Intent intent = new Intent(holder.itemView.getContext(), remarks.class);
                intent.putExtras(bundle);
                holder.itemView.getContext().startActivity(intent);

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String DiagnosisID,DiagnosisText,DiagnosisDate,DiagnosisImage,PatientID;


                DiagnosisDate = diagnosisList.get(holder.getAdapterPosition()).getDiagnosisDate();
                DiagnosisID = diagnosisList.get(holder.getAdapterPosition()).getDiagnosisID();
                DiagnosisImage = diagnosisList.get(holder.getAdapterPosition()).getDiagnosisImage();
                DiagnosisText = diagnosisList.get(holder.getAdapterPosition()).getDiagnosisText();
                PatientID = diagnosisList.get(holder.getAdapterPosition()).getPatientID();


                Bundle bundle = new Bundle();
                bundle.putString("DiagnosisDate", DiagnosisDate);
                bundle.putString("DiagnosisText", DiagnosisText);
                bundle.putString("DiagnosisImage",DiagnosisImage);
                bundle.putString("DiagnosisId",DiagnosisID);
                bundle.putString("PatientID",PatientID);

                Intent intent = new Intent(holder.itemView.getContext(), diagnosisUpdate.class);
                intent.putExtras(bundle);
                holder.itemView.getContext().startActivity(intent);

            }
        });

    }



    @Override
    public int getItemCount() {
        return diagnosisList.size();
    }

    public static class DiagnosisViewHolder extends RecyclerView.ViewHolder {
        TextView diagnosisText, date;
        ImageView diagnosisImage;
        CardView cardBTN;
        ImageButton edit,delete;

        public DiagnosisViewHolder(@NonNull View itemView) {
            super(itemView);
            diagnosisText = itemView.findViewById(R.id.diagnosis);
            date = itemView.findViewById(R.id.diagnosisDate);
            diagnosisImage = itemView.findViewById(R.id.diagnosisImage);
            cardBTN = itemView.findViewById(R.id.cardbtn);
            edit = itemView.findViewById(R.id.editDiagnosis);

        }
    }
}

