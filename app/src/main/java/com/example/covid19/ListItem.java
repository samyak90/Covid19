package com.example.covid19;

class ListItem {

    private String countryName;
    private String totalCases;
    private String totalDeath;
    private String totalRecovered;
    private String totalActive;
    private String newCases;
    private String newDeaths;

    ListItem(String countryName, String totalCases, String totalDeath, String totalRecovered, String totalActive, String newCases, String newDeaths) {
        this.countryName = countryName;
        this.totalCases = totalCases;
        this.totalDeath = totalDeath;
        this.totalRecovered = totalRecovered;
        this.totalActive = totalActive;
        this.newCases = newCases;
        this.newDeaths = newDeaths;
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

    String getNewCases() {
        return newCases;
    }

    String getNewDeaths() {
        return newDeaths;
    }

}
