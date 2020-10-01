package com.covid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyBubble.db";
    public static final String ENCOUNTERS_TABLE = "Encounters_Table";
    public static final String PERSONAL_INFO_TABLE = "Personal_Info_Table";
    public static final String INFECTED_ENCOUNTERS_TABLE = "Infected_Encounters_Table";
    public static final String ENCOUNTERS_COL1 = "ID";
    public static final String ENCOUNTERS_COL2 = "ENCOUNTER_DATE";
    public static final String ENCOUNTERS_COL3 = "ENCOUNTER_TIME";
    public static final String PERSONAL_INFO_COL1 = "PERSONAL_ID";
    public static final String INFECTED_ENCOUNTERS_COL1 = "INFECTED_USER_ID";
    public static final String INFECTED_ENCOUNTERS_COL2 = "DATE_REPORTED";
    public static final String INFECTED_ENCOUNTERS_COL3 = "ENCOUNTERED_STATUS";
    public static final String INFECTED_ENCOUNTERS_COL4 = "DATE_ENCOUNTERED";
    public static final String INFECTED_ENCOUNTERS_COL5 = "NOTIFICATION_SENT";





    public DatabaseHelper(@Nullable Context context) {
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
                "    ), ENCOUNTER_DATE TEXT, ENCOUNTER_TIME TEXT)");

        db.execSQL(" create table " + PERSONAL_INFO_TABLE + "(PERSONAL_ID TEXT PRIMARY KEY)");

        db.execSQL(" create table " + INFECTED_ENCOUNTERS_TABLE + "(INFECTED_USER_ID TEXT PRIMARY KEY, DATE_REPORTED, ENCOUNTERED_STATUS, DATE_ENCOUNTERED, NOTIFICATION_SENT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + ENCOUNTERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PERSONAL_INFO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + INFECTED_ENCOUNTERS_TABLE);
        onCreate(db);
    }




    public boolean insertEncounterData(String ID, String EncounterDate, String EncounterTime) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues newValues = new ContentValues();
            newValues.put("ID", ID);
            newValues.put("ENCOUNTER_DATE", EncounterDate);
            newValues.put("ENCOUNTER_TIME", EncounterTime);
            long result = db.replace(ENCOUNTERS_TABLE, null, newValues);
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

    public boolean insertInfectedEncounterData(String InfectedUserID, String dateReported, String encounterStatus, String notificationSent) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("INFECTED_USER_ID", InfectedUserID);
        newValues.put("DATE_REPORTED", dateReported);
        newValues.put("ENCOUNTERED_STATUS",encounterStatus);
        newValues.put("NOTIFICATION_SENT",notificationSent);
        long result = db.replace(INFECTED_ENCOUNTERS_TABLE, null, newValues);
        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    public String getEncounterData(String ID) {
        String result = "Data Not Found";
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "ID=?";
        String[] whereArgs = new String[]{String.valueOf(ID)};
        Cursor csr = db.query(ENCOUNTERS_TABLE, null, whereClause, whereArgs, null, null, null);
        if (csr.moveToFirst()) {
            result = csr.getString(csr.getColumnIndex(ENCOUNTERS_COL1)) + ", " + csr.getString(csr.getColumnIndex(ENCOUNTERS_COL2)) + ", " + csr.getString(csr.getColumnIndex(ENCOUNTERS_COL3));
        }
        return result;
    }

    public void deletedAgedEncounterData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM ENCOUNTERS_TABLE WHERE ENCOUNTER_DATE < date('now','-21 day')";
        db.execSQL(sql);
    }

    String[] tableColumns = new String[] {
            "PERSONAL_ID",
            "(SELECT PERSONAL_ID FROM Personal_Info_Table)"
    };

    public String getPersonalInfoData() {
        String result = "Data Not Found";
        SQLiteDatabase db = this.getWritableDatabase();
        //String whereClause = "ID=?";
        //String[] whereArgs = new String[]{String.valueOf(ID)};
        Cursor csr = db.query(PERSONAL_INFO_TABLE, tableColumns, null, null, null, null, null);
        if (csr.moveToFirst()) {
            result = csr.getString(csr.getColumnIndex(PERSONAL_INFO_COL1));
        }
        return result;
    }

    public int getNumOfEncounters() {
        SQLiteDatabase db = this.getWritableDatabase();
        //String query = "SELECT count(*) FROM ENCOUNTERS_TABLE WHERE ENCOUNTER_DATE < date('now','-" + days + " week')";
        String query = "SELECT * FROM ENCOUNTERS_TABLE";
        Cursor cursor = db.rawQuery(query, null);
        int result = cursor.getCount();
        return result;
    }

    public int getNumOfInfectedEncounters() {
        SQLiteDatabase db = this.getWritableDatabase();
        //String query = "SELECT count(*) FROM INFECTED_ENCOUNTERS_TABLE WHERE ENCOUNTER_DATE < date('now','-" + days + " week')";
        String query = "SELECT * FROM INFECTED_ENCOUNTERS_TABLE";
        Cursor cursor = db.rawQuery(query, null);
        int result = cursor.getCount();
        return result;
    }

    public boolean CheckIsDataInDB(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String query = "SELECT (*) FROM ENCOUNTERS_TABLE WHERE ENCOUNTER_ID exists;
        String[] columns = {"ID"};
        String selection = "ID" + " =?";
        String[] selectionArgs = {id};
        String limit = "1";

        Cursor cursor = db.query("Encounters_Table", columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}
