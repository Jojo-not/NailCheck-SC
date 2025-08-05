package com.example.scan;

import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;

public class Details extends AppCompatActivity {

    TextView Title,Desc;
    ImageView DetailsImage;
    ImageButton btnBack;
    ScrollView text;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.details), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        hideSystemUI();

        Title = findViewById(R.id.header_title);
        DetailsImage = findViewById(R.id.detailimage);
        Desc = findViewById(R.id.detaildes);
        btnBack = findViewById(R.id.btnBACK);
        text = findViewById(R.id.textcon);



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Details.this,Info.class);
                startActivity(intent);
                finish();
            }
        });

        bundle = getIntent().getExtras();
        if(bundle != null){
            Title.setText(bundle.getString("Title"));
            DetailsImage.setImageResource(bundle.getInt("Image"));
            Desc.setText(bundle.getInt("Desc"));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Desc.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            }
        }
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