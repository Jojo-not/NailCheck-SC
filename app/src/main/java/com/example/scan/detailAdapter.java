package com.example.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class detailAdapter extends RecyclerView.Adapter<MyViewHolder>{
    private Context context;
    private List<dataClass> dataClassList;


    public detailAdapter(Context context, List<dataClass> dataClassList){
        this.context = context;
        this.dataClassList = dataClassList;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.irecycle_item_info,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.recImage.setImageResource(dataClassList.get(position).getDataImagee());
        holder.recTitle.setText(dataClassList.get(position).getDataTitle());
        holder.recDes.setText(dataClassList.get(position).getDataDes());
        holder.recDescImage.setImageResource(dataClassList.get(position).getDataDescImage());
        holder.recdescInfo.setText(dataClassList.get(position).getDataDescInfo());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = dataClassList.get(holder.getAdapterPosition()).getDataTitle();
                int desc = dataClassList.get(holder.getAdapterPosition()).getDataDescInfo();
                int image = dataClassList.get(holder.getAdapterPosition()).getDataDescImage();



                Bundle bundle = new Bundle();
                bundle.putString("Title", title);
                bundle.putInt("Desc", desc);
                bundle.putInt("Image", image); // assuming it's an image resource ID

                // Attach the bundle to the fragment


                Intent intent = new Intent(context, Details.class);
                intent.putExtras(bundle);  // Attach the bundle with the patient data
                holder.itemView.getContext().startActivity(intent);



            }
        });


    }

    @Override
    public int getItemCount() {
        return dataClassList.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage, recDescImage;
    TextView recTitle,recDes, recdescInfo;
    CardView recCard;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.cardImage);
        recTitle = itemView.findViewById(R.id.cardTitle);
        recDes = itemView.findViewById(R.id.description);
        recCard = itemView.findViewById(R.id.cardInfo);
        recdescInfo = itemView.findViewById(R.id.descInfo);
        recDescImage = itemView.findViewById(R.id.descImg);

    }


}