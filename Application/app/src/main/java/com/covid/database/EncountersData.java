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
        String btEncounterID =  "567891235673dd2aQt";
        String btEncounterDate = "2020-02-15";
        String btEncounterTime = "10:00";
        MainActivity.encounterDB.insertEncounterData(btEncounterID, btEncounterDate, btEncounterTime);
        String btEncounterID2 =  "4567891235673dd2aQt";
        String btEncounterDate2 = "2020-09-01";
        String btEncounterTime2 = "12:00";
        MainActivity.encounterDB.insertEncounterData(btEncounterID2, btEncounterDate2, btEncounterTime2);
        String btEncounterID3 =  "UpdatedID";
        String btEncounterDate3 = "2020-08-30";
        String btEncounterTime3 = "13:00";
        MainActivity.encounterDB.insertEncounterData(btEncounterID3, btEncounterDate3, btEncounterTime3);
        String btEncounterID4 =  "UpdatedID";
        String btEncounterDate4 = "2020-09-01";
        String btEncounterTime4 = "14:00";
        MainActivity.encounterDB.insertEncounterData(btEncounterID4, btEncounterDate4, btEncounterTime4);

        //Deleting aged encounter data
        MainActivity.encounterDB.deletedAgedEncounterData();
    }
}
