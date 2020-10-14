package com.covid.utils;

public class ZoneCovidData {
    private String DHB;
    private int active;
    private int recovered;
    private int deceased;
    private int total;
    private int change24hr;

    public ZoneCovidData(String DHB, int active, int recovered, int deceased, int total, int change24hr) {
        this.DHB = DHB;
        this.active = active;
        this.recovered = recovered;
        this.deceased = deceased;
        this.total = total;
        this.change24hr = change24hr;
    }

    public String getDHB() {
        return DHB;
    }

    public void setDHB(String DHB) {
        this.DHB = DHB;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public int getDeceased() {
        return deceased;
    }

    public void setDeceased(int deceased) {
        this.deceased = deceased;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getChange24hr() {
        return change24hr;
    }

    public void setChange24hr(int change24hr) {
        this.change24hr = change24hr;
    }
}
