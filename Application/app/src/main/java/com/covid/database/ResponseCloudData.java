package com.covid.database;

public class ResponseCloudData {
    String infectedUserEncounters;

    public String getinfectedUserEncounters() {
        return infectedUserEncounters;
    }

    public void setInfectedUsers(String infectedUserEncounters) {
        this.infectedUserEncounters = infectedUserEncounters;
    }

    public ResponseCloudData(String infectedUserEncounters) {
        this.infectedUserEncounters = infectedUserEncounters;
    }

    public ResponseCloudData() {
    }
}
