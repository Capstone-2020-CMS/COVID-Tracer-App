package com.covid.database;

import com.covid.MainActivity;
import com.covid.utils.CodeManager;

public class EncountersData {

    public static void recordEncountersData() {

        //Placeholder insertEncounterData method
        //String btEncounter =  "test ID: 567891235673dd2aQt";
        //String date = "test Date: 27-08-2020 12:10:18pm";
        //MainActivity.encounterDB.insertEncounterData(btEncounter, date);

        //Recording encounters
        String btEncounter =  "567891235673dd2aQt";
        String date = "test Date: 27-08-2020 12:10:18pm";
        MainActivity.encounterDB.insertEncounterData(btEncounter, date);
    }
}
