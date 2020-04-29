package com.example.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blongho.country_data.World;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CountryActivity extends AppCompatActivity {

    private static final String TAG = "CountryActivity";
    private static final String URL_DATA_WORLD_TIME_SERIES = "https://pomber.github.io/covid19/timeseries.json";
    private static final String URL_DATA_INDIA_STATE_TIME_SERIES = "https://api.covid19india.org/states_daily.json";
    private static final String URL_DATA_WORLD_TIME_SERIES2_BASE = "https://api.thevirustracker.com/free-api?countryTimeline=";
    private List<CountryTimelineListItem> listItemsCountryTimeLine;
    private String countryName;
    private String countryCode;
    RecyclerView recyclerViewLinear;
    LineChart lineChart1;
    LineChart lineChart2;
    HashMap<String, String> hashMap;
    HashMap<String, String> stateMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_layout);
        Log.d(TAG, "onCreate: started.");

        // Add a back button in Action Bar to go back to the previous activity
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Another library that needs initialization
        World.init(getApplicationContext()); // Initializes the library that loads all data

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
            hashMap = (HashMap<String, String>) intent.getSerializableExtra("map");
            stateMap = (HashMap<String, String>) intent.getSerializableExtra("mapState");

            // Set data to views
            setDataToViews(totalCases, totalDeath, totalRecovered, totalActive);
        }
    }

    private void setDataToViews(String totalCases, String totalDeath, String totalRecovered, String totalActive) {

        Log.d(TAG, "setDataToViews: setting data to views");

        // Get ImageView and set image to the view
        //ImageView imageViewFlag = findViewById(R.id.image_view_flag);
        //String flagImageUrl = getFlagImageUrl(countryName, hashMap);
        //Glide.with(this).asBitmap().load(flagImageUrl).into(imageViewFlag);

        // Get ImageView and set image to the view
        ImageView imageViewFlag = findViewById(R.id.image_view_flag);
        String countryCode = getCountryCode(countryName, hashMap);
        String stateCode = getStateCode(countryName, stateMap);
        if(!stateCode.equals("") && TextUtils.isEmpty(countryCode))
            imageViewFlag.setImageResource(World.getFlagOf("IN"));
        else {
            //int flag = -1;
            if (countryName.equals("US") && countryCode.equals(""))
                imageViewFlag.setImageResource(World.getFlagOf("US"));
            else if (countryName.equals("Korea, South") && countryCode.equals(""))
                imageViewFlag.setImageResource(World.getFlagOf("KR"));
            else if (countryName.equals("Congo (Brazzaville)") && countryCode.equals(""))
                imageViewFlag.setImageResource(World.getFlagOf("CD"));
            else if (countryName.equals("Saint Kitts and Nevis") && countryCode.equals(""))
                imageViewFlag.setImageResource(World.getFlagOf("KN"));
            else if (countryName.equals("Saint Vincent and the Grenadines") && countryCode.equals(""))
                imageViewFlag.setImageResource(World.getFlagOf("VC"));
            else if (countryName.equals("Sao Tome and Principe") && countryCode.equals(""))
                imageViewFlag.setImageResource(World.getFlagOf("ST"));
            else if (countryName.equals("Cote d'lvoire") && countryCode.equals(""))
                imageViewFlag.setImageResource(World.getFlagOf("CI"));
            else
                imageViewFlag.setImageResource(World.getFlagOf(countryCode));
        }

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

        // Populate data from URL
        loadRecyclerViewCountryTimelineData();

    }

    // Load Country timeline data from URL for countryName
    private void loadRecyclerViewCountryTimelineData() {

        countryCode = getCountryCode(countryName, hashMap);
        if (countryName.equals("US")){
            countryCode = "US";
        }
        String stateCode = getStateCode(countryName, stateMap);
        StringRequest stringRequest;

        if(TextUtils.isEmpty(stateCode)) {
            stringRequest = new StringRequest(StringRequest.Method.GET,
//                    URL_DATA_WORLD_TIME_SERIES,
                    URL_DATA_WORLD_TIME_SERIES2_BASE + countryCode,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                // Get the data for relevant country
                                JSONObject jsonObject = new JSONObject(response);



//                                JSONArray array = jsonObject.getJSONArray(countryName);
//                                // Assuming we got the country name and associated data
//                                for (int i = 0; i < array.length(); i++) {
//                                    JSONObject countryDataWithDate = array.getJSONObject(i);
//                                    String currDate = countryDataWithDate.getString("date");
//                                    String currConfirmed = countryDataWithDate.getString("confirmed");
//                                    String currDeaths = countryDataWithDate.getString("deaths");
//                                    String currRecovered = countryDataWithDate.getString("recovered");
//                                    // Assign values to list item
//                                    CountryTimelineListItem item = new CountryTimelineListItem(currDate, currConfirmed, currDeaths, currRecovered);
//                                    listItemsCountryTimeLine.add(item);
//                                }



                                JSONObject countryObject = jsonObject.getJSONArray("timelineitems").getJSONObject(0);
                                JSONArray keysArray = countryObject.names();
                                // Assuming we got the country name and associated data
                                for (int i = 0; i < countryObject.length(); i++) {
                                    try{
                                        assert keysArray != null;
                                        String key = keysArray.getString(i);
                                        JSONObject countryDataWithDate = countryObject.getJSONObject(key);

                                        // Format Date
                                        String currDate = key;
                                        // Format date data correctly
                                        try {
                                            Date date1 = new SimpleDateFormat("MM/dd/yy", Locale.US).parse(currDate);
                                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                            assert date1 != null;
                                            currDate = dateFormat.format(date1);
                                        } catch (ParseException e) {
                                            currDate = key;
                                            e.printStackTrace();
                                        }

                                        String currConfirmed = countryDataWithDate.getString("total_cases");
                                        String currDeaths = countryDataWithDate.getString("total_deaths");
                                        String currRecovered = countryDataWithDate.getString("total_recoveries");

                                        // Fix the bug related to total recoveries data 0 for certain dates for certain countries
                                        if(i > 0){
                                            CountryTimelineListItem prevItem = listItemsCountryTimeLine.get(i-1);
                                            if(Integer.parseInt(currRecovered) < Integer.parseInt(prevItem.getRecovered()))
                                                currRecovered = String.valueOf(Integer.parseInt(prevItem.getRecovered()) + Integer.parseInt(currRecovered));
                                        }

                                        // Assign values to list item
                                        CountryTimelineListItem item = new CountryTimelineListItem(currDate, currConfirmed, currDeaths, currRecovered);
                                        listItemsCountryTimeLine.add(item);

                                    } catch (JSONException exc){
                                        exc.printStackTrace();
                                    }
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


        } else {
            // India Case (State timeline data)
            stringRequest = new StringRequest(StringRequest.Method.GET,
                    URL_DATA_INDIA_STATE_TIME_SERIES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                // Get the data for relevant country
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray array = jsonObject.getJSONArray("states_daily");
                                int totalConfirmed = 0;
                                int totalRecovered = 0;
                                int totalDeaths = 0;

                                // Assuming we got the country name and associated data
                                for (int i = 0; i < array.length(); i+=3) {
                                    JSONObject stateDataConfirmedWithDate = array.getJSONObject(i);
                                    JSONObject stateDataRecoveredWithDate = array.getJSONObject(i+1);
                                    JSONObject stateDataDeathWithDate = array.getJSONObject(i+2);

                                    String currDate = stateDataConfirmedWithDate.getString("date");
                                    // Format date data correctly
                                    try {
                                        Date date1 = new SimpleDateFormat("dd-MMM-yy", Locale.US).parse(currDate);
                                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                        assert date1 != null;
                                        currDate = dateFormat.format(date1);
                                    } catch (ParseException e) {
                                        currDate = stateDataConfirmedWithDate.getString("date");
                                        e.printStackTrace();
                                    }

                                    try {
                                        totalConfirmed += Integer.parseInt(stateDataConfirmedWithDate.getString(stateCode));
                                    } catch (NumberFormatException nfe) {
                                        nfe.printStackTrace();
                                    }
                                    try {
                                        totalDeaths += Integer.parseInt(stateDataDeathWithDate.getString(stateCode));
                                    } catch (NumberFormatException nfe) {
                                        nfe.printStackTrace();
                                    }
                                    try {
                                        totalRecovered += Integer.parseInt(stateDataRecoveredWithDate.getString(stateCode));
                                    } catch (NumberFormatException nfe) {
                                        nfe.printStackTrace();
                                    }

                                    String currConfirmed = String.valueOf(totalConfirmed);
                                    String currDeaths = String.valueOf(totalDeaths);
                                    String currRecovered = String.valueOf(totalRecovered);

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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void setDataToChartAndRecyclerView() {
        RecyclerView.Adapter adapter = new CountryTimelineAdapter(listItemsCountryTimeLine, getApplicationContext());
        recyclerViewLinear.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Assign data to line charts from listItemsCountryTimeLine
        ArrayList<Entry> yValuesChart1 = new ArrayList<>();
        ArrayList<Entry> yValuesChart2 = new ArrayList<>();
        String[] xAxisValues = new String[listItemsCountryTimeLine.size()];

        CountryTimelineListItem aListItem;
        int timelineDataLength = listItemsCountryTimeLine.size();
        int dataDivider = 4;

        // yValues Chart1 - Total Confirmed cases
        for(int index = 0; index < timelineDataLength; index+=dataDivider){
            aListItem = listItemsCountryTimeLine.get(index);
            yValuesChart1.add(new Entry(index, Integer.parseInt(aListItem.getConfirmedCases())));
        }
        // Add the last item if not already added
        if ((timelineDataLength-1) % dataDivider != 0){
            aListItem = listItemsCountryTimeLine.get(timelineDataLength - 1);
            yValuesChart1.add(new Entry(timelineDataLength - 1, Integer.parseInt(aListItem.getConfirmedCases())));
        }

        // yValues Chart2 - Total Deaths
        for(int index = 0; index < timelineDataLength; index+=dataDivider){
            aListItem = listItemsCountryTimeLine.get(index);
            yValuesChart2.add(new Entry(index, Integer.parseInt(aListItem.getDeaths())));
        }
        // Add the last item if not already added
        if ((timelineDataLength-1) % dataDivider != 0){
            aListItem = listItemsCountryTimeLine.get(timelineDataLength - 1);
            yValuesChart2.add(new Entry(timelineDataLength - 1, Integer.parseInt(aListItem.getDeaths())));
        }

        // Get x-axis indices (dates) for both the charts
        for(int index = 0; index < timelineDataLength; index++){
            aListItem = listItemsCountryTimeLine.get(index);
            String date = aListItem.getDate();
            try {
                Date date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
                //DateFormat dateFormat = new SimpleDateFormat("dd/M");
                DateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.US);
                assert date1 != null;
                date = dateFormat.format(date1);
            } catch (ParseException e) {
                date = aListItem.getDate();
                e.printStackTrace();
            }
            xAxisValues[index] = date;
        }

        //////// Create data for Chart 1 /////////////
        LineDataSet setChart1 = new LineDataSet(yValuesChart1, "Total Confirmed Cases (since 22nd Jan 2020)");
        //setChart1.setFillAlpha(110);
        setChart1.setColor(Color.YELLOW);
        setChart1.setLineWidth(3f);
        //setChart1.setDrawValues(false);
        setChart1.setValueTextSize(5f);
        //setChart1.setValueTextColor(Color.BLUE);

        ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
        dataSets1.add(setChart1);
        LineData data1 = new LineData(dataSets1);
        lineChart1.setData(data1);
        lineChart1.getAxisRight().setEnabled(false);

        // Hide description
        Description desc1 = new Description();
        desc1.setEnabled(false);
        desc1.setText("Cases since 22nd Jan");
        lineChart1.setDescription(desc1);

        XAxis xAxis1 = lineChart1.getXAxis();
        xAxis1.setLabelCount(7, true);
        xAxis1.mAxisMaximum = 100;
        xAxis1.mAxisMinimum = 0;
        xAxis1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisValues[(int)value];
            }
        });

        // Refresh chart
        lineChart1.notifyDataSetChanged();
        lineChart1.invalidate();


        //////// Create data for Chart 2 /////////////
        LineDataSet setChart2 = new LineDataSet(yValuesChart2, "Total Deaths (since 22nd Jan 2020)");
        //setChart2.setFillAlpha(110);
        setChart2.setColor(Color.RED);
        setChart2.setLineWidth(3f);
        //setChart2.setDrawValues(false);
        setChart2.setValueTextSize(5f);
        //setChart2.setValueTextColor(Color.BLUE);

        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
        dataSets2.add(setChart2);
        LineData data2 = new LineData(dataSets2);
        lineChart2.setData(data2);
        lineChart2.getAxisRight().setEnabled(false);

        // Hide description
        Description desc2 = new Description();
        desc2.setEnabled(false);
        desc2.setText("Deaths since 22nd Jan");
        lineChart2.setDescription(desc2);

        XAxis xAxis2 = lineChart2.getXAxis();
        xAxis2.setLabelCount(7, true);
        xAxis2.mAxisMaximum = 100;
        xAxis2.mAxisMinimum = 0;
        xAxis2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisValues[(int)value];
            }
        });

        // Refresh chart
        lineChart2.notifyDataSetChanged();
        lineChart2.invalidate();


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

    private String getCountryCode(String aCountryName, HashMap<String, String> aMap) {
        // Get value from aMap and use it to get url
        if (aMap.containsKey(aCountryName)) {
            String countryCode = aMap.get(aCountryName);
            assert countryCode != null;
            return countryCode;
        }
        return "";
    }

    private String getStateCode(String aStateName, HashMap<String, String> aMap) {
        // Get value from aMap and use it to get url
        if (aMap.containsKey(aStateName)) {
            String stateCode = aMap.get(aStateName);
            assert stateCode != null;
            return stateCode;
        }
        return "";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
