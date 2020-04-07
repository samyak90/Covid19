package com.example.covid19;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.blongho.country_data.World;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.haipq.android.flagkit.FlagImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CountryActivity extends AppCompatActivity {

    private static final String TAG = "CountryActivity";
    private static final String URL_DATA_WORLD_TIME_SERIES = "https://pomber.github.io/covid19/timeseries.json";
    private List<CountryTimelineListItem> listItemsCountryTimeLine;
    private String countryName;
    RecyclerView recyclerViewLinear;
    LineChart lineChart1;
    LineChart lineChart2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_layout);
        Log.d(TAG, "onCreate: started.");

        // Another library
        World.init(getApplicationContext()); // Initializes the library and loads all data

        // Handle incoming intent
        getIncomingIntent();
    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: checking for incoming intents");
        Intent intent = getIntent();
        if (intent.hasExtra("countryName") &&
                intent.hasExtra("totalCases") &&
                intent.hasExtra("totalDeath") &&
                intent.hasExtra("totalRecovered") &&
                intent.hasExtra("totalActive")) {

            Log.d(TAG, "getIncomingIntent: found intent extras");
            countryName = intent.getStringExtra("countryName");
            String totalCases = intent.getStringExtra("totalCases");
            String totalDeath = intent.getStringExtra("totalDeath");
            String totalRecovered = intent.getStringExtra("totalRecovered");
            String totalActive = intent.getStringExtra("totalActive");
            HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("map");

            // Set data to views
            setDataToViews(countryName, totalCases, totalDeath, totalRecovered, totalActive, hashMap);
        }
    }

    private void setDataToViews(String countryName, String totalCases,
                                String totalDeath, String totalRecovered, String totalActive, HashMap<String, String> hashMap) {

        Log.d(TAG, "setDataToViews: setting data to views");


        // Get ImageView and set image to the view
        //ImageView imageViewFlag = findViewById(R.id.image_view_flag);
        //String flagImageUrl = getFlagImageUrl(countryName, hashMap);
        //Glide.with(this).asBitmap().load(flagImageUrl).into(imageViewFlag);

        // Get ImageView and set image to the view
        ImageView imageViewFlag = findViewById(R.id.image_view_flag);
        String countryCode = getCountryCode(countryName, hashMap);
        //int flag = -1;
        if(countryName.equals("US") && countryCode.equals(""))
            imageViewFlag.setImageResource(World.getFlagOf("US"));
        else
            imageViewFlag.setImageResource(World.getFlagOf(countryCode));

        //FlagImageView flagImageView = (FlagImageView) findViewById(R.id.flagView);
        //String countryCode = getCountryCode(countryName, hashMap);
        //if(countryName.equals("US") && countryCode.equals(""))
        //    flagImageView.setCountryCode("US"); // with text code
        //else
        //    flagImageView.setCountryCode(countryCode); // with text code

        // Get all the view from the xml file
        TextView textViewCountryName = findViewById(R.id.country_state_name2);
        TextView textViewTotalCases = findViewById(R.id.total_cases_item_left2);
        TextView textViewTotalDeath = findViewById(R.id.total_deaths_item_left2);
        TextView textViewTotalRecovered = findViewById(R.id.total_recovered_item_left2);
        TextView textViewTotalActive = findViewById(R.id.total_active_item_left2);
        // Set data to text views
        textViewCountryName.setText(countryName);
        textViewTotalCases.setText(totalCases);
        textViewTotalDeath.setText(totalDeath);
        textViewTotalRecovered.setText(totalRecovered);
        textViewTotalActive.setText(totalActive);

        // Get LineChart view and set data for the chart
        lineChart1 = findViewById(R.id.lineChart1);
        lineChart2 = findViewById(R.id.lineChart2);
        lineChart1.setDragEnabled(true);
        lineChart2.setDragEnabled(true);
        lineChart1.setScaleEnabled(false);
        lineChart2.setScaleEnabled(false);

        // Create Data List Item variable
        listItemsCountryTimeLine = new ArrayList<>();

        // Create recycler view item
        recyclerViewLinear = findViewById(R.id.recycler_view_2);
        recyclerViewLinear.setHasFixedSize(true);
        recyclerViewLinear.setLayoutManager(new LinearLayoutManager(this));
        // Assign adapter to the data
        setDataToChartAndRecyclerView();

        // Populate data from URL
        loadRecyclerViewCountryTimelineData();

    }


    // Load Country timeline data from URL for countryName
    private void loadRecyclerViewCountryTimelineData() {

        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET,
                URL_DATA_WORLD_TIME_SERIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Get the data for relevant country
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray(countryName);
                            // Assuming we got the country name and associated data
                            for (int i = 0; i < array.length(); i++){
                                JSONObject countryDataWithDate = array.getJSONObject(i);
                                String currDate = countryDataWithDate.getString("date");
                                String currConfirmed = countryDataWithDate.getString("confirmed");
                                String currDeaths = countryDataWithDate.getString("deaths");
                                String currRecovered = countryDataWithDate.getString("recovered");
                                // Assign values to list item
                                CountryTimelineListItem item = new CountryTimelineListItem(currDate, currConfirmed, currDeaths, currRecovered);
                                listItemsCountryTimeLine.add(item);
                            }

                            // Update View for recycler view and line charts
                            setDataToChartAndRecyclerView();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

    }


    private String getFlagImageUrl(String countryName, HashMap<String, String> aMap) {
        // Get value from aMap and use it to get url
        if (aMap.containsKey(countryName)) {
            String countryCode = aMap.get(countryName);
            assert countryCode != null;
            String imageUrl = "https://www.countryflags.io/" + countryCode.toLowerCase() + "/shiny/64.png";
            Log.d(TAG, "getFlagImageUrl: reached here");
            return imageUrl;
        }
        return "";
    }

    private String getCountryCode(String countryName, HashMap<String, String> aMap) {
        // Get value from aMap and use it to get url
        if (aMap.containsKey(countryName)) {
            String countryCode = aMap.get(countryName);
            assert countryCode != null;
            return countryCode;
        }
        return "";
    }

    private void setDataToChartAndRecyclerView() {
        RecyclerView.Adapter adapter = new CountryTimelineAdapter(listItemsCountryTimeLine, getApplicationContext());
        recyclerViewLinear.setAdapter(adapter);

        // Assign data to linecharts from listItemsCountryTimeLine



    }

}
