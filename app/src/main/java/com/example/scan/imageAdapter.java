package com.example.scan;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class imageAdapter extends RecyclerView.Adapter<imageAdapter.ImageViewHolder> {

    ArrayList <imageitem> arrayList;

    public imageAdapter(ArrayList<imageitem> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public imageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull imageAdapter.ImageViewHolder holder, int position) {
        imageitem imageitem = arrayList.get(position);
        holder.text.setText(imageitem.getText());
        holder.title.setText(imageitem.getTitle());
        holder.image.setImageResource(imageitem.getImage());



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ImageViewHolder extends  RecyclerView.ViewHolder{
        TextView title,text;
        ImageView image;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
        }
    }
}
