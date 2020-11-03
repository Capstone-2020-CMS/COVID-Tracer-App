package com.myBubble.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import static com.myBubble.MainActivity.myDB;

public class CloudInfectedUsers {

    //Check if id in INFECTED_ENCOUNTERS_TABLE exists in ENCOUNTERS_TABLE
    public static void updateEncounterStatus() {
        ArrayList<String> infectedEncounterData = new ArrayList<>();
        SQLiteDatabase db = myDB.getWritableDatabase();

        infectedEncounterData = myDB.getListOfInfectedIDs();

        for (String id : infectedEncounterData) {
            if (!myDB.getEncounterData(id).equals("Data Not Found")) {
                String sql  = "UPDATE ENCOUNTERS_TABLE SET IS_INFECTED = 'true' WHERE ID='" + id + "'";
                db.execSQL(sql);
                Log.v("UPDATED INFECTION", "true");
            }
        }
    }
}
