package com.example.scan;


import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;


import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
public class home extends AppCompatActivity {
    ImageButton menuList;
    private FirebaseAuth auth;
    private ViewPager2 viewPager;
    imageAdapter imageAdapter;
    private Handler handler;
    private Runnable runnable;
    private int currentItem = 0;
    private CardView camera, history, gallery, info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        hideSystemUI();
        menuList = findViewById(R.id.menuList);
        auth = FirebaseAuth.getInstance();
        viewPager = findViewById(R.id.imageSlider);
        camera = findViewById(R.id.scanner);
        gallery = findViewById(R.id.gallery);
        info = findViewById(R.id.info);
        history = findViewById(R.id.medical);

        PopupMenu popupMenu = new PopupMenu(new ContextThemeWrapper(this, R.style.PopupMenuStyle), menuList);
        popupMenu.getMenuInflater().inflate(R.menu.menutab, popupMenu.getMenu());


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int Item = item.getItemId();

                if (Item == R.id.signOut) {
                    auth.signOut();
                    Intent intent = new Intent(home.this, sign_up.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    return false;
                }
                return false;
            }
        });

        menuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < popupMenu.getMenu().size(); i++) {
                    MenuItem menuItem = popupMenu.getMenu().getItem(i);
                    View view = popupMenu.getMenu().findItem(menuItem.getItemId()).getActionView();

                    // Apply custom background drawable if view is not null
                    if (view != null) {
                        view.setBackgroundResource(R.drawable.shape1);
                    }
                }
                popupMenu.show();
            }
        });

        int[] image = {R.drawable.iconss1, R.drawable.iconss2, R.drawable.iconss3};
        String[] text = {"NailCheck is an app for detecting nail abnormalities using the camera to capture nail images for analysis.",
                "NailCheck is an application focused on detecting nail abnormalities. However, improper use of the app may lead to inaccurate or unreliable results.",
                "Please capture your fingernail to asses of nail abnormalities throughout the entire nail."};
        String[] title = {"CAMERA", "DISCLAIMER", "NOTES"};

        ArrayList<imageitem> mList = new ArrayList<>();

        for (int i = 0; i < image.length; i++) {
            mList.add(new imageitem(text[i], title[i], image[i]));
        }

        handler = new Handler(Looper.getMainLooper());
        imageAdapter = new imageAdapter(mList);
        viewPager.setAdapter(imageAdapter);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager.getAdapter() != null) {
                    int itemCount = viewPager.getAdapter().getItemCount();
                    currentItem = (currentItem + 1) % itemCount; // Cycle back to the first item
                    viewPager.setCurrentItem(currentItem, true); // Smooth scroll
                    handler.postDelayed(this, 5000); // Delay of 3 seconds
                }
            }
        };

        handler.postDelayed(runnable, 3000);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 5000); // Restart auto-swipe after interaction
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, camera.class);
                startActivity(intent);

            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, about_app.class);
                startActivity(intent);

            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, Info.class);
                startActivity(intent);

            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, MedicalHistory.class);
                startActivity(intent);

            }
        });

        int primaryColor = ContextCompat.getColor(this, R.color.colorBackground);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Prevent memory leaks
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