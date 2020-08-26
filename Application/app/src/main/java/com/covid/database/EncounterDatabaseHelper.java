package com.covid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class EncounterDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyBubble.db";
    public static final String ENCOUNTERS_TABLE = "Encounters_Table";
    public static final String PERSONAL_INFO_TABLE = "Personal_Info_Table";
    public static final String ENCOUNTERS_COL1 = "ID";
    public static final String ENCOUNTERS_COL2 = "ENCOUNTER_DATE";
    public static final String PERSONAL_INFO_COL1 = "PERSONAL_ID";



    public EncounterDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static boolean checkDataBaseExists(String DB_FULL_PATH) {
        SQLiteDatabase checkDB = null;
        try{
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLException e) {
            // Database does not exist
        }
        finally {
            checkDB.close();
        }

        return checkDB != null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" create table " + ENCOUNTERS_TABLE + "(ID TEXT PRIMARY KEY CHECK(\n" +
                "        typeof(\"ID\") = \"text\" AND\n" +
                "        length(\"ID\") <= 20\n" +
                "    ), ENCOUNTER_DATE TEXT)");

        db.execSQL(" create table " + PERSONAL_INFO_TABLE + "(PERSONAL_ID TEXT PRIMARY KEY)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + ENCOUNTERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PERSONAL_INFO_TABLE);
        onCreate(db);
    }

    public boolean insertEncounterData(String ID, String EncounterDate) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues newValues = new ContentValues();
            newValues.put("ID", ID);
            newValues.put("ENCOUNTER_DATE", EncounterDate);
            long result = db.insert(ENCOUNTERS_TABLE, null, newValues);
            if (result == -1) {
                return false;
            }
            else {
                return true;
            }
    }

    public boolean insertPersonalData(String PERSONAL_ID) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("PERSONAL_ID", PERSONAL_ID);
        long result = db.insert(PERSONAL_INFO_TABLE, null, newValues);
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }
}
