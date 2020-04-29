package com.example.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.anychart.data.View;
import com.blongho.country_data.World;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Objects;

public class IndiaActivity extends AppCompatActivity {

    private static final String TAG = "IndiaActivity";
    String stateName;
    String totalCases;
    String totalDeath;
    String totalRecovered;
    String totalActive;
    String totalNewCases;
    String totalNewDeaths;
    //HashMap<String, String> hashMap;
    HashMap<String, String> stateMap;

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    MyPagerAdapter myPagerAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_india);

        // Another library that needs initialization
        World.init(getApplicationContext()); // Initializes the library that loads all data

        // Handle incoming intent and get data
        Log.d(TAG, "getIncomingIntent: checking for incoming intents");
        Intent intent = getIntent();
        if (intent.hasExtra("countryName") &&
                intent.hasExtra("totalCases") &&
                intent.hasExtra("totalDeath") &&
                intent.hasExtra("totalRecovered") &&
                intent.hasExtra("totalActive") &&
                intent.hasExtra("totalNewCases") &&
                intent.hasExtra("totalNewDeaths")) {

            Log.d(TAG, "getIncomingIntent: found intent extras");
            stateName = intent.getStringExtra("countryName");
            totalCases = intent.getStringExtra("totalCases");
            totalDeath = intent.getStringExtra("totalDeath");
            totalRecovered = intent.getStringExtra("totalRecovered");
            totalActive = intent.getStringExtra("totalActive");
            totalNewCases = intent.getStringExtra("totalNewCases");
            totalNewDeaths = intent.getStringExtra("totalNewDeaths");
            //hashMap = (HashMap<String, String>) intent.getSerializableExtra("map");
            stateMap = (HashMap<String, String>) intent.getSerializableExtra("mapState");
        }

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Add a back button in Action Bar to go back to the previous activity
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), 0);
        myPagerAdapter.addFragment(IndiaFragment.newInstance(stateName, totalCases, totalDeath, totalRecovered, totalActive, stateMap), "TIMELINE");
        myPagerAdapter.addFragment(DistrictFragment.newInstance(stateName, totalCases, totalDeath, totalRecovered, totalActive, totalNewCases, totalNewDeaths), "DISTRICT DATA");

        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setAdapter(myPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
