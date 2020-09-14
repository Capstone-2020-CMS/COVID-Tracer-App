package com.covid.database;

public class ResponseCloudData {
    String infectedUserEncounters;

    public String getinfectedUserEncounters() {
        return infectedUserEncounters;
    }

    public void setInfectedUsers(String greetings) {
        this.infectedUserEncounters = infectedUserEncounters;
    }

    public ResponseCloudData(String greetings) {
        this.infectedUserEncounters = greetings;
    }

    public ResponseCloudData() {
    }
}
