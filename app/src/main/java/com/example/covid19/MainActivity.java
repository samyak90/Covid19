package com.example.covid19;

import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String URL_DATA_WORLD3 = "https://pomber.github.io/covid19/timeseries.json";
    private static final String URL_DATA_WORLD = "https://coronacache.home-assistant.io/corona.json";
    private static final String URL_DATA_WORLD2 = "https://api.thevirustracker.com/free-api?countryTotals=ALL";
    private static final String URL_DATA_INDIA = "https://api.covid19india.org/data.json";
    // HashMap for countryName and countryCode
    public HashMap<String, String> hmap;
    public HashMap<String, String> stateMap;
    RadioGroup radioGroup;
    int totalCases;
    int totalDeaths;
    int totalRecovered;
    int totalActive;
    int totalNewCases;
    int totalNewDeaths;
    TextView overallTotalCases;
    TextView overallTotalDeaths;
    TextView overallTotalRecovered;
    TextView overallTotalActive;
    TextView overallNewCases;
    TextView overallNewDeaths;
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<ListItem> listItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Get Radio group
        radioGroup = findViewById(R.id.radio_group);

        // Reset values to zero
        totalCases = 0;
        totalDeaths = 0;
        totalRecovered = 0;
        totalActive = 0;
        totalNewCases = 0;
        totalNewDeaths = 0;

        // Set values to textView
        overallTotalCases = findViewById(R.id.overall_cases);
        overallTotalDeaths = findViewById(R.id.overall_deaths);
        overallTotalRecovered = findViewById(R.id.overall_recovered);
        overallTotalActive = findViewById(R.id.overall_active);
        overallNewCases = findViewById(R.id.overall_new_cases);
        overallNewDeaths = findViewById(R.id.overall_new_deaths);

        // Update overall data
        String totalCasesStr = "Total Cases: " + totalCases;
        String totalDeathsStr = "Total Deaths: " + totalDeaths;
        String totalRecoveredStr = "Total Recovered: " + totalRecovered;
        String totalActiveStr = "Total Active: " + totalActive;
        String totalNewCasesStr = "Total New Cases: " + totalNewCases;
        String totalNewDeathsStr = "Total New Deaths: " + totalNewDeaths;
        overallTotalCases.setText(totalCasesStr);
        overallTotalDeaths.setText(totalDeathsStr);
        overallTotalRecovered.setText(totalRecoveredStr);
        overallTotalActive.setText(totalActiveStr);
        overallNewCases.setText(totalNewCasesStr);
        overallNewDeaths.setText(totalNewDeathsStr);

        // Create a HashMap for world and India
        makeCountryNameCountryCodeMap();
        makeIndiaAndStateCodeMap();

        // Create Data List
        listItems = new ArrayList<>();

        // Populate data from URL based on radio button checked
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.india)
            loadRecyclerViewData(1);
        else
            loadRecyclerViewData(0);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.india)
                    loadRecyclerViewData(1);
                else
                    loadRecyclerViewData(0);
            }
        });

    }

    private void loadRecyclerViewData(int id) {

        // Reset values to zero
        totalCases = 0;
        totalDeaths = 0;
        totalRecovered = 0;
        totalActive = 0;
        totalNewCases = 0;
        totalNewDeaths = 0;
        listItems.clear();

        // Request data
//        if (id == 0) {
//
//            StringRequest stringRequest = new StringRequest(StringRequest.Method.GET,
//                    URL_DATA_WORLD,
//                    new Response.Listener<String>() {
//                        @RequiresApi(api = Build.VERSION_CODES.N)
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                JSONArray array = jsonObject.getJSONArray("features");
//
//                                for (int i = 0; i < array.length(); i++) {
//                                    JSONObject countryData = array.getJSONObject(i).getJSONObject("attributes");
//                                    totalCases += Integer.parseInt(countryData.getString("Confirmed"));
//                                    totalDeaths += Integer.parseInt(countryData.getString("Deaths"));
//                                    totalRecovered += Integer.parseInt(countryData.getString("Recovered"));
////                                    totalActive += Integer.parseInt(countryData.getString("Active"));
//                                    int currActive = Integer.parseInt(countryData.getString("Confirmed")) -
//                                            Integer.parseInt(countryData.getString("Deaths")) -
//                                            Integer.parseInt(countryData.getString("Recovered"));
//                                    totalActive += currActive;
//
//                                    ListItem item = new ListItem(
//                                            countryData.getString("Country_Region"),
//                                            "Total Cases: " + getUSFormatForNumber(countryData.getString("Confirmed")),
//                                            "Total Death: " + getUSFormatForNumber(countryData.getString("Deaths")),
//                                            "Total Recovered: " + getUSFormatForNumber(countryData.getString("Recovered")),
//                                            "Total Active: " + getUSFormatForNumber(String.valueOf(currActive)),
//                                            "New Cases: " + getUSFormatForNumber(String.valueOf(currActive)),
//                                            "New Deaths: " + getUSFormatForNumber(String.valueOf(currActive))
//                                    );
//                                    listItems.add(item);
//                                    //adapter.notifyDataSetChanged();
//                                }
//
//                                // Sort based on the number of total cases
//                                Collections.sort(listItems, Collections.reverseOrder(new CasesSorter()));
//
//                                // Update View
//                                setAdapterAndUpdateView();
////                                swipeRefreshLayout.setRefreshing(false);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            error.printStackTrace();
//                        }
//                    });
//
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            requestQueue.add(stringRequest);
//        } else {

        if (id == 0) {
            StringRequest stringRequest = new StringRequest(StringRequest.Method.GET,
                    URL_DATA_WORLD2,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(String response) {
                            try {
                                // Get the data for relevant country
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("countryitems");
                                JSONObject allCountry = jsonArray.getJSONObject(0);
                                for (int i = 1; i < allCountry.length() + 1; i++) {
                                    JSONObject countryData;
                                    try {
                                        countryData = (JSONObject) allCountry.get(String.valueOf(i));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        continue;
                                    }

                                    String countryName;
                                    countryName = countryData.getString("title");
                                    if (countryName.equals("USA")) {
                                        countryName = "US";
                                    }

                                    totalCases += Integer.parseInt(countryData.getString("total_cases"));
                                    totalDeaths += Integer.parseInt(countryData.getString("total_deaths"));
                                    totalRecovered += Integer.parseInt(countryData.getString("total_recovered"));
                                    totalActive += Integer.parseInt(countryData.getString("total_active_cases"));
                                    totalNewCases += Integer.parseInt(countryData.getString("total_new_cases_today"));
                                    totalNewDeaths += Integer.parseInt(countryData.getString("total_new_deaths_today"));

                                    // Assign values to list item
                                    ListItem item = new ListItem(
                                            countryName,
                                            "Total Cases: " + getUSFormatForNumber(countryData.getString("total_cases")),
                                            "Total Death: " + getUSFormatForNumber(countryData.getString("total_deaths")),
                                            "Total Recovered: " + getUSFormatForNumber(countryData.getString("total_recovered")),
                                            "Total Active: " + getUSFormatForNumber(countryData.getString("total_active_cases")),
                                            "New Cases: " + getUSFormatForNumber(countryData.getString("total_new_cases_today")),
                                            "New Deaths: " + getUSFormatForNumber(countryData.getString("total_new_deaths_today"))
                                    );

                                    listItems.add(item);
                                    //adapter.notifyDataSetChanged();
                                }

                                // Sort based on the number of total cases
                                Collections.sort(listItems, Collections.reverseOrder(new CasesSorter()));

                                // Update View
                                setAdapterAndUpdateView();
                                swipeRefreshLayout.setRefreshing(false);

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

        } else {

            StringRequest stringRequest = new StringRequest(StringRequest.Method.GET,
                    URL_DATA_INDIA,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray array = jsonObject.getJSONArray("statewise");

                                for (int i = 1; i < array.length(); i++) {
                                    JSONObject countryData = array.getJSONObject(i);
                                    totalCases += Integer.parseInt(countryData.getString("confirmed"));
                                    totalDeaths += Integer.parseInt(countryData.getString("deaths"));
                                    totalRecovered += Integer.parseInt(countryData.getString("recovered"));
                                    totalActive += Integer.parseInt(countryData.getString("active"));
                                    totalNewCases += Integer.parseInt(countryData.getString("deltaconfirmed"));
                                    totalNewDeaths += Integer.parseInt(countryData.getString("deltadeaths"));

                                    ListItem item = new ListItem(
                                            countryData.getString("state"),
                                            "Total Cases: " + getUSFormatForNumber(countryData.getString("confirmed")),
                                            "Total Death: " + getUSFormatForNumber(countryData.getString("deaths")),
                                            "Total Recovered: " + getUSFormatForNumber(countryData.getString("recovered")),
                                            "Total Active: " + getUSFormatForNumber(countryData.getString("active")),
                                            "New Cases: " + getUSFormatForNumber(countryData.getString("deltaconfirmed")),
                                            "New Deaths: " + getUSFormatForNumber(countryData.getString("deltadeaths"))
                                    );
                                    listItems.add(item);
                                    //adapter.notifyDataSetChanged();
                                }

                                // Update Data
                                setAdapterAndUpdateView();
//                                swipeRefreshLayout.setRefreshing(false);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAdapterAndUpdateView() {
        RecyclerView.Adapter adapter = new MyAdapter(listItems, getApplicationContext(), hmap, stateMap);
        recyclerView.setAdapter(adapter);

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        // Update overall data
        String totalCasesStr = "Total Cases: " + getUSFormatForNumber(String.valueOf(totalCases));
        String totalDeathsStr = "Total Deaths: " + getUSFormatForNumber(String.valueOf(totalDeaths));
        String totalRecoveredStr = "Total Recovered: " + getUSFormatForNumber(String.valueOf(totalRecovered));
        String totalActiveStr = "Total Active: " + getUSFormatForNumber(String.valueOf(totalActive));
        String totalNewCasesStr = "Total New Cases: " + getUSFormatForNumber(String.valueOf(totalNewCases));
        String totalNewDeathsStr = "Total New Deaths: " + getUSFormatForNumber(String.valueOf(totalNewDeaths));
        overallTotalCases.setText(totalCasesStr);
        overallTotalDeaths.setText(totalDeathsStr);
        overallTotalRecovered.setText(totalRecoveredStr);
        overallTotalActive.setText(totalActiveStr);
        overallNewCases.setText(totalNewCasesStr);
        overallNewDeaths.setText(totalNewDeathsStr);
    }

    public void OnRadioButtonClicked(View view) {
        // Get checked radio button
        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.worldYesterday) {
            // Load World Data
            loadRecyclerViewData(0);
        }
        else if (selectedId == R.id.india) {
            // Load India Data
            loadRecyclerViewData(1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getUSFormatForNumber(String numberStr) {
        return NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(numberStr));
    }

    // Create a HashMap for country name and code list
    public void makeCountryNameCountryCodeMap() {
        // Store key value pair for countries
        String json = null;
        /* This is how to declare HashMap */
        HashMap<String, String> hmap = new HashMap<String, String>();
        try {
            InputStream is = this.getResources().openRawResource(R.raw.countrycode);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONObject("countries").getJSONArray("country");
            for (int i = 0; i < array.length(); i++) {
                String jsonCountryName = array.getJSONObject(i).getString("countryName");
                String jsonCountryCode = array.getJSONObject(i).getString("countryCode");
                hmap.put(jsonCountryName, jsonCountryCode);
            }
            this.hmap = hmap;
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }
    }

    // Create a HashMap for country name and code list
    public void makeIndiaAndStateCodeMap() {
        // Store key value pair for countries
        String json = null;
        /* This is how to declare HashMap */
        HashMap<String, String> stateMap = new HashMap<String, String>();
        try {
            InputStream is = this.getResources().openRawResource(R.raw.indianstates);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray stateCodeStringArray = jsonObject.names();
            assert stateCodeStringArray != null;
            for (int i = 0; i < stateCodeStringArray.length(); i++) {
                String jsonStateCode = stateCodeStringArray.getString(i);
                String jsonStateName = jsonObject.getString(jsonStateCode);
                stateMap.put(jsonStateName, jsonStateCode.toLowerCase());
            }
            this.stateMap = stateMap;
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.about_menu) {
            Intent intent = new Intent(this, About.class);
            this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

