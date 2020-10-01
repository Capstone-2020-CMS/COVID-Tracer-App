package com.covid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.covid.MainActivity;

import java.sql.Connection;

import static com.covid.MainActivity.myDB;
import static com.covid.database.DatabaseHelper.INFECTED_ENCOUNTERS_TABLE;

public class CloudInfectedUsers {

    //Check if id in INFECTED_ENCOUNTERS_TABLE exists in ENCOUNTERS_TABLE
    public static void updateEncounterStatus() {
        SQLiteDatabase db = myDB.getWritableDatabase();
        //ContentValues newValues = new ContentValues();
        String sql1 = "SELECT * FROM INFECTED_ENCOUNTERS_TABLE IE INNER JOIN ENCOUNTERS_TABLE E ON IE.INFECTED_USER_ID=E.ID ";
        //db.rawQuery(sql, null);
        //String sql2  = "UPDATE INFECTED_ENCOUNTERS_TABLE SET ENCOUNTERED_STATUS = 'true' WHERE INFECTED_USER_ID=(SELECT ENCOUNTERS_TABLE.ID FROM ENCOUNTERS_TABLE)";

        //String sql3  = "UPDATE INFECTED_ENCOUNTERS_TABLE SET ENCOUNTERED_STATUS = 'true' WHERE ENCOUNTERS_TABLE.ID=INFECTED_USER_ID ";
        //db.addBatch();

        String sql2  = "REPLACE INTO INFECTED_ENCOUNTERS_TABLE(INFECTED_USER_ID, ENCOUNTERED_STATUS) VALUES ('testID123','true')";

        db.execSQL(sql2);
        //Cursor cursor = db.rawQuery(sql2, null);
    }
    //if id exists set ENCOUNTERED_STATUS to true






}
