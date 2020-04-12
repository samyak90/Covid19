package com.example.covid19;

import java.util.Date;

class CountryTimelineListItem {

//    public Date date;
    private String date;
    private String confirmedCases;
    private String deaths;
    private String recovered;

    CountryTimelineListItem(String date, String confirmedCases, String deaths, String recovered) {
        this.date = date;
        this.confirmedCases = confirmedCases;
        this.deaths = deaths;
        this.recovered = recovered;
    }

    String getDate() {
        return date;
    }

    String getConfirmedCases() {
        return confirmedCases;
    }

    String getDeaths() {
        return deaths;
    }

    String getRecovered() {
        return recovered;
    }
}
