package com.covid.database;

import android.content.Context;

import com.covid.MainActivity;

import static com.covid.bluetooth.BLEService.myDB;

public class EncountersData {

    public static boolean recordEncountersData(String EncounterID,String EncounterDate, String EncounterTime) {

        //Placeholder insertEncounterData method
        //String EncounterDate =  "test ID: 567891235673dd2aQt";
        //String date = "test Date: 27-08-2020 12:10:18pm";
        //MainActivity.encounterDB.insertEncounterData(btEncounter, date);



        //...


        //Recording encounters (keep only 4 lines below when bluetooth is working, changing only the Strings content)
        //String EncounterID =  "567891235673dd2aQt"; //placeholder ID - insert ID method taken from bluetooth encounter here
        //String EncounterDate = "2020-02-15"; // placeholder Date - insert  method taken from bluetooth encounter here
        //String EncounterTime = "10:00"; //placeholder Time - insert Time method taken from bluetooth encounter here
        boolean result = myDB.insertEncounterData(EncounterID, EncounterDate, EncounterTime);


        //Deleting aged encounter data
        myDB.deletedAgedEncounterData();

        return result;
    }
}
