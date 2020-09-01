package com.covid.database;

import com.covid.MainActivity;
import com.covid.utils.CodeManager;

public class EncountersData {

    public static void recordEncountersData() {

        //Placeholder insertEncounterData method
        //String btEncounter =  "test ID: 567891235673dd2aQt";
        //String date = "test Date: 27-08-2020 12:10:18pm";
        //MainActivity.encounterDB.insertEncounterData(btEncounter, date);

        //Recording encounters (keep only 4 lines below when bluetooth is working, changing only the Strings content)
        String btEncounterID =  "567891235673dd2aQt"; //placeholder ID
        String btEncounterDate = "2020-02-15"; // placeholder Date
        String btEncounterTime = "10:00"; //placeholder Time
        MainActivity.encounterDB.insertEncounterData(btEncounterID, btEncounterDate, btEncounterTime);


        //Deleting aged encounter data
        MainActivity.encounterDB.deletedAgedEncounterData();
    }
}
