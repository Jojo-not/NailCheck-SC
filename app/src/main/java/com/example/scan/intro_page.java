package com.example.scan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class intro_page extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout layout;
    Button btn_next,btn_getstarted;
    int position = 0;
    Animation btn_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro_page);

        hideSystemUI();

        if(restorePrefData()){
            Intent intent = new Intent(getApplicationContext(), sign_up.class);
            startActivity(intent);
            finish();

        }

        // view
        layout=findViewById(R.id.tab_screen);
        btn_getstarted = findViewById(R.id.btn_getstarted);
        btn_next = findViewById(R.id.btn_next);
        btn_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.btn_anim);

        //fill the list screen

        List<screenitem> mList = new ArrayList<>();
        mList.add(new screenitem("You can capture an Image with your fingernail on our App.",R.drawable.imagev1));
        mList.add(new screenitem("You can check Information about various Nail Abnormalities.",R.drawable.imagev2));
        mList.add(new screenitem("You can Visualizes the result of the Analysis.a",R.drawable.imagev3));


        //setup viewpager
        screenPager = findViewById(R.id.page_screen);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        //setup tablayout with viewpager

        layout.setupWithViewPager(screenPager);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()){
                    position++;
                    screenPager.setCurrentItem(position);

                }
                if (position == mList.size()-1){
                   // TODO: show the getstarted button and hide the indicator and new button
                    loadLastScreen();
                }


            }

        });

        layout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == mList.size()-1){
                    loadLastScreen();

                }else {
                    LastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                LastScreen();

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition() == mList.size()-2){
                    loadLastScreen();
                    btn_next.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.VISIBLE);
                    btn_getstarted.setVisibility(View.INVISIBLE);

                }else {
                    LastScreen();
                }

            }
        });


        btn_getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),sign_up.class);
                startActivity(intent);
                savePersData();
                finish();
            }
        });

    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityopendBefore = pref.getBoolean("open",false);
        return isIntroActivityopendBefore;

    }

    private void savePersData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("open", true);
        editor.commit();
    }


    private void loadLastScreen() {
        btn_next.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.INVISIBLE);
        btn_getstarted.setVisibility(View.VISIBLE);
        btn_getstarted.setAnimation(btn_anim);

    }
    private void LastScreen() {
        btn_next.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        btn_getstarted.setVisibility(View.GONE);
        btn_getstarted.setAnimation(btn_anim);

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