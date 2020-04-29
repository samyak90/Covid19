package com.example.covid19;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IndiaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndiaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";

    // TODO: Rename and change types of parameters
    private String stateName;
    private String totalCases;
    private String totalDeath;
    private String totalRecovered;
    private String totalActive;
    private HashMap<String, String> stateMap;

    // Other needed params
    private static final String URL_DATA_INDIA_STATE_TIME_SERIES = "https://api.covid19india.org/states_daily.json";
    private List<CountryTimelineListItem> listItemsCountryTimeLine;
    private RecyclerView recyclerViewLinear;
    private LineChart lineChart1;
    private LineChart lineChart2;



    public IndiaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param stateNameIn Parameter 1.
     * @param totalCasesIn Parameter 2.
     * @param totalDeathIn Parameter 3.
     * @param totalRecoveredIn Parameter 4.
     * @param totalActiveIn Parameter 5.
     * @param stateMapIn Parameter 6.
     * @return A new instance of fragment IndiaFragment.
     */
    // TODO: Rename and change types and number of parameters
    static IndiaFragment newInstance(String stateNameIn, String totalCasesIn, String totalDeathIn, String totalRecoveredIn, String totalActiveIn, HashMap<String, String> stateMapIn) {
        IndiaFragment fragment = new IndiaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, stateNameIn);
        args.putString(ARG_PARAM2, totalCasesIn);
        args.putString(ARG_PARAM3, totalDeathIn);
        args.putString(ARG_PARAM4, totalRecoveredIn);
        args.putString(ARG_PARAM5, totalActiveIn);
        args.putSerializable(ARG_PARAM6, stateMapIn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stateName = getArguments().getString(ARG_PARAM1);
            totalCases = getArguments().getString(ARG_PARAM2);
            totalDeath = getArguments().getString(ARG_PARAM3);
            totalRecovered = getArguments().getString(ARG_PARAM4);
            totalActive = getArguments().getString(ARG_PARAM5);
            stateMap = (HashMap<String, String>) getArguments().getSerializable(ARG_PARAM6);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_india, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set data to views
        setDataToViews(view, totalCases, totalDeath, totalRecovered, totalActive);
    }

    private void setDataToViews(View view, String totalCases, String totalDeath, String totalRecovered, String totalActive) {
        // Get ImageView and set image to the view
        ImageView imageViewFlag = view.findViewById(R.id.india_image_view_flag);
        String stateCode = getStateCode(stateName, stateMap);
        imageViewFlag.setImageResource(World.getFlagOf("IN"));

        // Get all the view from the xml file
        TextView textViewCountryName = view.findViewById(R.id.india_country_state_name2);
        TextView textViewTotalCases = view.findViewById(R.id.india_total_cases_item_left2);
        TextView textViewTotalDeath = view.findViewById(R.id.india_total_deaths_item_left2);
        TextView textViewTotalRecovered = view.findViewById(R.id.india_total_recovered_item_left2);
        TextView textViewTotalActive = view.findViewById(R.id.india_total_active_item_left2);

        // Set data to text views
        textViewCountryName.setText(stateName);
        textViewTotalCases.setText(totalCases);
        textViewTotalDeath.setText(totalDeath);
        textViewTotalRecovered.setText(totalRecovered);
        textViewTotalActive.setText(totalActive);

        // Get LineChart view and set data for the chart
        lineChart1 = view.findViewById(R.id.india_lineChart1);
        lineChart2 = view.findViewById(R.id.india_lineChart2);
        lineChart1.setDragEnabled(true);
        lineChart2.setDragEnabled(true);
        lineChart1.setScaleEnabled(false);
        lineChart2.setScaleEnabled(false);

        // Create Data List Item variable
        listItemsCountryTimeLine = new ArrayList<>();

        // Create recycler view item
        recyclerViewLinear = view.findViewById(R.id.india_recycler_view_2);
        recyclerViewLinear.setHasFixedSize(true);
        recyclerViewLinear.setLayoutManager(new LinearLayoutManager(getContext()));

        // Populate data from URL
        loadRecyclerViewCountryTimelineData();

    }

    // Load Country timeline data from URL for countryName
    private void loadRecyclerViewCountryTimelineData() {

        String stateCode = getStateCode(stateName, stateMap);
        StringRequest stringRequest;

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

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        requestQueue.add(stringRequest);

    }

    private void setDataToChartAndRecyclerView() {
        RecyclerView.Adapter adapter = new CountryTimelineAdapter(listItemsCountryTimeLine, getContext());
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

    private String getStateCode(String aStateName, HashMap<String, String> aMap) {
        // Get value from aMap and use it to get url
        if (aMap.containsKey(aStateName)) {
            String stateCode = aMap.get(aStateName);
            assert stateCode != null;
            return stateCode;
        }
        return "";
    }
}
