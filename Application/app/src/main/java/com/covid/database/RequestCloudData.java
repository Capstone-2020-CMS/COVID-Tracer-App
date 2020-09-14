package com.covid.database;

public class RequestCloudData {
    String infectedUserID;


    public String getInfectedUsers() {
        return infectedUserID;
    }

    public void setInfectedUsers(String infectedUserID) {
        this.infectedUserID = infectedUserID;
    }


    public RequestCloudData(String infectedUserID) {
        this.infectedUserID = infectedUserID;
    }

    public RequestCloudData() {
    }
}
