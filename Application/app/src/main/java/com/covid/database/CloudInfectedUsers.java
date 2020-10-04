package com.covid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
                String sql  = "UPDATE INFECTED_ENCOUNTERS_TABLE SET ENCOUNTERED_STATUS = 'true' WHERE INFECTED_USER_ID='" + id + "'";
                db.execSQL(sql);
            }
        }
    }
}
