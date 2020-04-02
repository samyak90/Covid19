package com.example.covid19;

class ListItem {

    private String countryName;
    private String totalCases;
    private String totalDeath;
    private String totalRecovered;
    private String totalActive;

    ListItem(String countryName, String totalCases, String totalDeath, String totalRecovered, String totalActive) {
        this.countryName = countryName;
        this.totalCases = totalCases;
        this.totalDeath = totalDeath;
        this.totalRecovered = totalRecovered;
        this.totalActive = totalActive;
    }

    String getCountryName() {
        return countryName;
    }

    String getTotalCases() {
        return totalCases;
    }

    String getTotalDeath() {
        return totalDeath;
    }

    String getTotalRecovered() {
        return totalRecovered;
    }

    String getTotalActive() {
        return totalActive;
    }
}
