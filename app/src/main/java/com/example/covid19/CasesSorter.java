package com.example.covid19;

import android.icu.text.NumberFormat;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Locale;

public class CasesSorter implements Comparator<ListItem>
{
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int compare(ListItem area1, ListItem area2) {
        Integer cases1 = null;
        try {
            cases1 = NumberFormat.getNumberInstance(Locale.US).parse(area1.getTotalCases().split(" ")[2]).intValue();
        } catch (ParseException e) {
            e.printStackTrace();
            cases1 = 0;
        }
        Integer cases2 = null;
        try {
            cases2 = NumberFormat.getNumberInstance(Locale.US).parse(area2.getTotalCases().split(" ")[2]).intValue();
        } catch (ParseException e) {
            e.printStackTrace();
            cases2 = 1;
        }

        return cases1.compareTo(cases2);
    }
}

