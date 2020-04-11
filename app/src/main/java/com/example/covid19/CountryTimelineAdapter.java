package com.example.covid19;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CountryTimelineAdapter extends RecyclerView.Adapter<CountryTimelineAdapter.ViewHolderCustom> {

    private List<CountryTimelineListItem> listItems;
    private Context context;

    CountryTimelineAdapter(List<CountryTimelineListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CountryTimelineAdapter.ViewHolderCustom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_country_timeline_recycler, parent, false );
        return new CountryTimelineAdapter.ViewHolderCustom(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryTimelineAdapter.ViewHolderCustom holder, int position) {
        final CountryTimelineListItem listItem = listItems.get(position);

        // Set values to each view
        String date = listItem.getDate();
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            DateFormat dateFormat = new SimpleDateFormat("dd MMM");
            date = dateFormat.format(date1);
        } catch (ParseException e) {
            date = listItem.getDate();
            e.printStackTrace();
        }
        holder.countryTimelineDate.setText(date);
        holder.countryTimelineConfirmed.setText(listItem.getConfirmedCases());
        holder.countryTimelineDeaths.setText(listItem.getDeaths());
        holder.countryTimelineRecovered.setText(listItem.getRecovered());

        if(position == 0) {
            // Initial day value for new cases and new deaths is assumed to be zero
            holder.countryTimelineNewCases.setText("0");
            holder.countryTimelineNewDeaths.setText("0");
        }
        else {
            CountryTimelineListItem listItemPreviousDay = listItems.get(position-1);
            int previousDayConfirmed = Integer.parseInt(listItemPreviousDay.getConfirmedCases());
            int previousDayDeaths = Integer.parseInt(listItemPreviousDay.getDeaths());
            int currentConfirmed = Integer.parseInt(listItem.getConfirmedCases());
            int currentDeaths = Integer.parseInt(listItem.getDeaths());
            holder.countryTimelineNewCases.setText(String.valueOf(currentConfirmed - previousDayConfirmed));
            holder.countryTimelineNewDeaths.setText(String.valueOf(currentDeaths - previousDayDeaths));
        }

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    static class ViewHolderCustom extends RecyclerView.ViewHolder{

        TextView countryTimelineDate;
        TextView countryTimelineConfirmed;
        TextView countryTimelineDeaths;
        TextView countryTimelineRecovered;
        TextView countryTimelineNewCases;
        TextView countryTimelineNewDeaths;

        ViewHolderCustom(@NonNull View itemView) {
            super(itemView);

            // Get all the view from the xml file
            countryTimelineDate = itemView.findViewById(R.id.country_timeline_date_id);
            countryTimelineConfirmed = itemView.findViewById(R.id.country_timeline_confirmed_id);
            countryTimelineDeaths = itemView.findViewById(R.id.country_timeline_deaths_id);
            countryTimelineRecovered = itemView.findViewById(R.id.country_timeline_recovered_id);
            countryTimelineNewCases = itemView.findViewById(R.id.country_timeline_new_cases_id);
            countryTimelineNewDeaths = itemView.findViewById(R.id.country_timeline_new_deaths_id);

        }
    }
}
