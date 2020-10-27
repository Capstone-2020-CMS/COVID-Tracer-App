package com.covid.utils;

public class InfectedUserData {
    private String ID;
    private String dateEncountered;
    private String dateReported;


    public InfectedUserData(String ID, String dateEncountered, String dateReported) {
        this.ID = ID;
        this.dateEncountered = dateEncountered;
        this.dateReported = dateReported;
    }

    public String getID() {
        return ID;
    }

    public String getDateEncountered() {
        return dateEncountered;
    }

    public String getDateReported() {
        return dateReported;
    }
}
