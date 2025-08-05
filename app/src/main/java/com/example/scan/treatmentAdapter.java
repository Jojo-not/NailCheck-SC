package com.example.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class treatmentAdapter extends RecyclerView.Adapter<treatmentAdapter.TreatmentViewHolder> {
    private Context context;
    private List<treatment> treatmentList;

    public treatmentAdapter(Context context, List<treatment> treatmentList) {
        this.context = context;
        this.treatmentList = treatmentList;
    }

    @NonNull
    @Override
    public treatmentAdapter.TreatmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.treatmentitem, parent, false);
        return new treatmentAdapter.TreatmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull treatmentAdapter.TreatmentViewHolder holder, int position) {
        treatment Treatment = treatmentList.get(position);
        holder.TreatmentText.setText(Treatment.getTreatmentTxt());
        holder.TreatmentDate.setText(Treatment.getTreatmentDate());
        holder.Remarks.setText(Treatment.getRemarks());
        Glide.with(holder.itemView.getContext())
                .load(Treatment.getTreatmentImage())
                .into(holder.TreatmentImage);


        holder.TreatmentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TreatmentID,TreatmentTxt,Remarks,TreatmentDate,TreatmentImage,PatientID,DiagnosisId;

                TreatmentImage = treatmentList.get(holder.getAdapterPosition()).getTreatmentImage();
                Remarks = treatmentList.get(holder.getAdapterPosition()).getRemarks();
                TreatmentTxt = treatmentList.get(holder.getAdapterPosition()).getTreatmentTxt();
                TreatmentID = treatmentList.get(holder.getAdapterPosition()).getTreatmentID();
                TreatmentDate = treatmentList.get(holder.getAdapterPosition()).getTreatmentDate();
                PatientID = treatmentList.get(holder.getAdapterPosition()).getPatientID();
                DiagnosisId = treatmentList.get(holder.getAdapterPosition()).getDiagnosisId();

                Bundle bundle = new Bundle();
                bundle.putString("TreatmentImage",TreatmentImage);
                bundle.putString("Remarks",Remarks);
                bundle.putString("TreatmentTxt",TreatmentTxt);
                bundle.putString("TreatmentID",TreatmentID);
                bundle.putString("PatientID",PatientID);
                bundle.putString("DiagnosisId",DiagnosisId);

                Intent intent = new Intent(holder.itemView.getContext(), treatmentUpdate.class);
                intent.putExtras(bundle);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return treatmentList.size();
    }

    public static class TreatmentViewHolder extends RecyclerView.ViewHolder {
        ImageView TreatmentImage;
        ImageButton TreatmentEdit;
        TextView TreatmentText,TreatmentDate,Remarks;
        public TreatmentViewHolder(@NonNull View itemView) {
            super(itemView);

           TreatmentImage = itemView.findViewById(R.id.treatmentIMG);
           TreatmentText = itemView.findViewById(R.id.TreatmentTxt);
           TreatmentDate = itemView.findViewById(R.id.TreatmentDate);
           TreatmentEdit = itemView.findViewById(R.id.TreatmentEdit);
           Remarks = itemView.findViewById(R.id.remarks);


        }
    }
}
