package com.example.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
        else if(countryName.equals("Korea, South") && countryCode.equals(""))
            imageViewFlag.setImageResource(World.getFlagOf("KR"));
        else if(countryName.equals("Congo (Brazzaville)") && countryCode.equals(""))
            imageViewFlag.setImageResource(World.getFlagOf("CD"));
        else if(countryName.equals("Saint Kitts and Nevis") && countryCode.equals(""))
            imageViewFlag.setImageResource(World.getFlagOf("KN"));
        else if(countryName.equals("Saint Vincent and the Grenadines") && countryCode.equals(""))
            imageViewFlag.setImageResource(World.getFlagOf("VC"));
        else if(countryName.equals("Sao Tome and Principe") && countryCode.equals(""))
            imageViewFlag.setImageResource(World.getFlagOf("ST"));
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
//        setDataToChartAndRecyclerView();

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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

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
        adapter.notifyDataSetChanged();

        // Assign data to linecharts from listItemsCountryTimeLine
        ArrayList<Entry> yValuesChart1 = new ArrayList<>();
        ArrayList<Entry> yValuesChart2 = new ArrayList<>();
        String[] xAxisValues = new String[(int)listItemsCountryTimeLine.size()];

        CountryTimelineListItem aListItem;

        // yValues Chart1 - Total Confirmed cases
        for(int index = 0; index < listItemsCountryTimeLine.size(); index+=4){
            aListItem = listItemsCountryTimeLine.get(index);
            yValuesChart1.add(new Entry(index, Integer.parseInt(aListItem.getConfirmedCases())));
        }

        // yValues Chart2 - Total Deaths
        for(int index = 0; index < listItemsCountryTimeLine.size(); index+=4){
            aListItem = listItemsCountryTimeLine.get(index);
            yValuesChart2.add(new Entry(index, Integer.parseInt(aListItem.getDeaths())));
        }

        // Get x-axis indices (dates) for both the charts
        for(int index = 0; index < listItemsCountryTimeLine.size(); index++){
            aListItem = listItemsCountryTimeLine.get(index);
            String date = aListItem.getDate();
            try {
                Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                //DateFormat dateFormat = new SimpleDateFormat("dd/M");
                DateFormat dateFormat = new SimpleDateFormat("dd MMM");
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

}
