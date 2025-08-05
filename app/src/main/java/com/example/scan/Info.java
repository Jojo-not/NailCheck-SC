package com.example.scan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Info extends AppCompatActivity {
    RecyclerView recyclerView;
    List<dataClass> dataClassList;
    detailAdapter adapter;
    dataClass data;
    FragmentManager fragmentManager;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fg_info), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        hideSystemUI();
        backButton=findViewById(R.id.backButton);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Info.this,home.class);
                startActivity(intent);
                finish();
            }
        });


        recyclerView = findViewById(R.id.recycleView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Info.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        dataClassList = new ArrayList<>();

        data = new dataClass("Healthy Nails"," ",R.drawable.healthy,R.string.deschelthy,R.drawable.dhealthy);
        dataClassList.add(data);
        data = new dataClass("Chloronychia","Green nail syndrome",R.drawable.chloro,R.string.Chloronychia,R.drawable.chloronychia);
        dataClassList.add(data);
        data = new dataClass("Median Nails","Median nail dystrophy",R.drawable.median,R.string.DescMedian,R.drawable.dmediannails);
        dataClassList.add(data);
        data = new dataClass("Melanonychia","Melanonychia striata",R.drawable.melanonychia,R.string.melanonychia,R.drawable.melan);
        dataClassList.add(data);
        data = new dataClass("Subungual Hematoma","Bruised nail,",R.drawable.hematoma,R.string.hemetuema,R.drawable.hematoma);
        dataClassList.add(data);
        data = new dataClass("Subungual Melanoma","Bruised nail,",R.drawable.malanoma,R.string.melanoma,R.drawable.melanom);
        dataClassList.add(data);
        adapter = new detailAdapter(this,dataClassList);
        recyclerView.setAdapter(adapter);

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