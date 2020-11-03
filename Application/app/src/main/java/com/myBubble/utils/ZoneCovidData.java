package com.myBubble.utils;

public class ZoneCovidData {
    private String DHB;
    private String active;
    private String recovered;
    private String deceased;
    private String total;
    private String change24hr;

    public ZoneCovidData(String DHB, String active, String recovered, String deceased, String total, String change24hr) {
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

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getRecovered() {
        return recovered;
    }

    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }

    public String getDeceased() {
        return deceased;
    }

    public void setDeceased(String deceased) {
        this.deceased = deceased;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getChange24hr() {
        return change24hr;
    }

    public void setChange24hr(String change24hr) {
        this.change24hr = change24hr;
    }
}
