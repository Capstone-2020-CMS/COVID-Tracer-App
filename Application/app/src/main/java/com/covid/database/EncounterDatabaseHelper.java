package com.covid.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class EncounterDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyBubble.db";
    public static final String TABLE_NAME = "Encounters_Table";
    public static final String COL1 = "ID";
    public static final String COL2 = "ENCOUNTERDATE";

    public EncounterDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = getWritableDatabase();
    }

    public static boolean checkDataBaseExists(String DB_FULL_PATH) {
        SQLiteDatabase checkDB = null;
        try{
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        }
        catch (SQLException e) {
            // Database does not exist
        }
        return checkDB != null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY, ENCOUNTERDATE DATE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
