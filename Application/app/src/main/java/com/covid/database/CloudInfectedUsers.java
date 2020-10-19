package com.covid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.covid.MainActivity;

import java.sql.Connection;
import java.util.ArrayList;

import static com.covid.MainActivity.myDB;
import static com.covid.database.DatabaseHelper.INFECTED_ENCOUNTERS_TABLE;

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
