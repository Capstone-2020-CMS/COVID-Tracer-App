package com.covid.utils;

public class TableData {
    private String title;
    private String total;
    private String change24hrs;

    public TableData(String title, String total, String change24hrs) {
        this.title = title;
        this.total = total;
        this.change24hrs = change24hrs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getChange24hrs() {
        return change24hrs;
    }

    public void setChange24hrs(String change24hrs) {
        this.change24hrs = change24hrs;
    }
}
