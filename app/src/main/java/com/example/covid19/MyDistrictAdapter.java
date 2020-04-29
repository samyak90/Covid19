package com.example.covid19;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyDistrictAdapter extends RecyclerView.Adapter<MyDistrictAdapter.ViewHolder>{

    private List<ListItem> listItems;
    private Context context;

    MyDistrictAdapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public MyDistrictAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item, parent, false );
        return new MyDistrictAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDistrictAdapter.ViewHolder holder, int position) {
        final ListItem listItem = listItems.get(position);

        holder.textViewDistrictName.setText(listItem.getCountryName());
        holder.textViewTotalCases.setText(listItem.getTotalCases());
        holder.textViewTotalDeath.setText(listItem.getTotalDeath());
        holder.textViewTotalRecovered.setText(listItem.getTotalRecovered());
        holder.textViewTotalActive.setText(listItem.getTotalActive());
        holder.textViewNewCases.setText(listItem.getNewCases());
        holder.textViewNewDeaths.setText(listItem.getNewDeaths());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout parentLayout;
        TextView textViewDistrictName;
        TextView textViewTotalCases;
        TextView textViewTotalDeath;
        TextView textViewTotalRecovered;
        TextView textViewTotalActive;
        TextView textViewNewCases;
        TextView textViewNewDeaths;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Get all the view from the xml file and assign them to the variables
            this.parentLayout = itemView.findViewById(R.id.parent_layout_list_item);
            this.textViewDistrictName = itemView.findViewById(R.id.country_state_name);
            this.textViewTotalCases = itemView.findViewById(R.id.total_cases_item_left);
            this.textViewTotalDeath = itemView.findViewById(R.id.total_deaths_item_left);
            this.textViewTotalRecovered = itemView.findViewById(R.id.total_recovered_item_left);
            this.textViewTotalActive = itemView.findViewById(R.id.total_active_item_left);
            this.textViewNewCases = itemView.findViewById(R.id.new_cases_item_left);
            this.textViewNewDeaths = itemView.findViewById(R.id.new_deaths_item_left);
        }
    }
}

