package com.myBubble.database;

import static com.myBubble.MainActivity.myDB;

public class EncountersData {

    public static boolean recordEncountersData(String EncounterID,String EncounterDate, String EncounterTime) {

        // Inserts the encounterID into the encountersDB with a time stamp returns a boolean
        // which indicates whether it succeeded of failed
        boolean result = myDB.insertEncounterData(EncounterID, EncounterDate, EncounterTime);

        //Deleting aged encounter data
        myDB.deletedAgedEncounterData();

        return result;
    }
}
