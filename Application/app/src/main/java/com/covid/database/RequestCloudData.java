package com.covid.database;

public class RequestCloudData {
    String infectedUserID;


    public String getInfectedUsers() {
        return infectedUserID;
    }

    public void setFirstName(String firstName) {
        this.infectedUserID = firstName;
    }


    public RequestCloudData(String infectedUserID) {
        this.infectedUserID = infectedUserID;
    }

    public RequestCloudData() {
    }
}
