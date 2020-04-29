package com.example.covid19;

import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DistrictFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DistrictFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";
    private static final String ARG_PARAM7 = "param7";

    // TODO: Rename and change types of parameters
    private String stateName;
    private String totalCases;
    private String totalDeaths;
    private String totalRecovered;
    private String totalActive;
    private String totalNewCases;
    private String totalNewDeaths;

//    private static final String URL_DATA_INDIA_STATE_DISTRICT_WISE = "https://api.covid19india.org/v2/state_district_wise.json";
    private static final String URL_DATA_INDIA_STATE_DISTRICT_WISE = "https://api.covid19india.org/state_district_wise.json";
    private RecyclerView recyclerView;
    private List<ListItem> listItems;

    public DistrictFragment() {
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
     * @param totalNewCasesIn Parameter 6.
     * @param totalNewDeathsIn Parameter 7.
     * @return A new instance of fragment DistrictFragment.
     */
    // TODO: Rename and change types and number of parameters
    static DistrictFragment newInstance(String stateNameIn, String totalCasesIn, String totalDeathIn, String totalRecoveredIn,
                                        String totalActiveIn, String totalNewCasesIn, String totalNewDeathsIn) {

        DistrictFragment fragment = new DistrictFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, stateNameIn);
        args.putString(ARG_PARAM2, totalCasesIn);
        args.putString(ARG_PARAM3, totalDeathIn);
        args.putString(ARG_PARAM4, totalRecoveredIn);
        args.putString(ARG_PARAM5, totalActiveIn);
        args.putString(ARG_PARAM6, totalNewCasesIn);
        args.putString(ARG_PARAM7, totalNewDeathsIn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stateName = getArguments().getString(ARG_PARAM1);
            totalCases = getArguments().getString(ARG_PARAM2);
            totalDeaths = getArguments().getString(ARG_PARAM3);
            totalRecovered = getArguments().getString(ARG_PARAM4);
            totalActive = getArguments().getString(ARG_PARAM5);
            totalNewCases = getArguments().getString(ARG_PARAM6);
            totalNewDeaths = getArguments().getString(ARG_PARAM7);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_district, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = view.findViewById(R.id.district_recycler_view);
        recyclerView.setHasFixedSize(true);

        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.HORIZONTAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        // Set values to textView
        TextView overallTotalCases = view.findViewById(R.id.state_overall_cases);
        TextView overallTotalDeaths = view.findViewById(R.id.state_overall_deaths);
        TextView overallTotalRecovered = view.findViewById(R.id.state_overall_recovered);
        TextView overallTotalActive = view.findViewById(R.id.state_overall_active);
        TextView overallNewCases = view.findViewById(R.id.state_overall_new_cases);
        TextView overallNewDeaths = view.findViewById(R.id.state_overall_new_deaths);

        // Update overall data
        String totalCasesStr = totalCases;
        String totalDeathsStr = totalDeaths;
        String totalRecoveredStr = totalRecovered;
        String totalActiveStr = totalActive;
        String totalNewCasesStr = totalNewCases;
        String totalNewDeathsStr = totalNewDeaths;
        overallTotalCases.setText(totalCasesStr);
        overallTotalDeaths.setText(totalDeathsStr);
        overallTotalRecovered.setText(totalRecoveredStr);
        overallTotalActive.setText(totalActiveStr);
        overallNewCases.setText(totalNewCasesStr);
        overallNewDeaths.setText(totalNewDeathsStr);

        // Create Data List
        listItems = new ArrayList<>();

        // Populate data from URL
        loadRecyclerViewData();
    }

    private void loadRecyclerViewData() {

        // Reset list
        listItems.clear();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET,
                URL_DATA_INDIA_STATE_DISTRICT_WISE,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject stateObject = jsonObject.getJSONObject(stateName);
                            JSONObject allDistrictsData = stateObject.getJSONObject("districtData");
                            JSONArray districtArray = allDistrictsData.names();

                            for (int i = 0; i < districtArray.length(); i++) {
                                String districtName = districtArray.getString(i);
                                JSONObject aDistrictData = allDistrictsData.getJSONObject(districtName);

                                ListItem item = new ListItem(
                                        districtName,
                                        "Total Cases: " + getUSFormatForNumber(aDistrictData.getString("confirmed")),
                                        "Total Death: " + getUSFormatForNumber(aDistrictData.getString("deceased")),
                                        "Total Recovered: " + getUSFormatForNumber(aDistrictData.getString("recovered")),
                                        "Total Active: " + getUSFormatForNumber(aDistrictData.getString("active")),
                                        "New Cases: " + getUSFormatForNumber(aDistrictData.getJSONObject("delta").getString("confirmed")),
                                        "New Deaths: " + getUSFormatForNumber(aDistrictData.getJSONObject("delta").getString("deceased"))
                                );
                                listItems.add(item);
                                //adapter.notifyDataSetChanged();
                            }

                            // Sort based on the number of total cases
                            Collections.sort(listItems, Collections.reverseOrder(new CasesSorter()));

                            // Update Data
                            setAdapterAndUpdateView();

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

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        requestQueue.add(stringRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAdapterAndUpdateView() {
        RecyclerView.Adapter adapter = new MyDistrictAdapter(listItems, getContext());
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getUSFormatForNumber(String numberStr) {
        return NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(numberStr));
    }

}
