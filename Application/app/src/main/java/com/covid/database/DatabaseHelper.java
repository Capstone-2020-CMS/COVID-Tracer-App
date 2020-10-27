package com.covid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;



import com.covid.gps.GPSRecord;


import java.util.ArrayList;

import static com.covid.MainActivity.myDB;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyBubble.db";
    public static final String ENCOUNTERS_TABLE = "Encounters_Table";
    public static final String PERSONAL_INFO_TABLE = "Personal_Info_Table";
    public static final String GPS_TABLE = "GPS_Table";
    public static final String INFECTED_ENCOUNTERS_TABLE = "Infected_Encounters_Table";

    public static final String ENCOUNTERS_COL1 = "ID";
    public static final String ENCOUNTERS_COL2 = "ENCOUNTER_DATE";
    public static final String ENCOUNTERS_COL3 = "ENCOUNTER_TIME";
    public static final String ENCOUNTERS_COL4 = "IS_INFECTED";
    public static final String PERSONAL_INFO_COL1 = "PERSONAL_ID";
    public static final String INFECTED_ENCOUNTERS_COL1 = "INFECTED_USER_ID";
    public static final String INFECTED_ENCOUNTERS_COL2 = "DATE_REPORTED";






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
                "    ), ENCOUNTER_DATE TEXT, ENCOUNTER_TIME TEXT, IS_INFECTED DEFAULT 'false', " +
                "NOTIFICATION_SENT DEFAULT 'false')");

        db.execSQL(" create table " + PERSONAL_INFO_TABLE + "(PERSONAL_ID TEXT PRIMARY KEY)");

        db.execSQL(" create table " + GPS_TABLE + "(" +
                "LATITUDE INTEGER," +
                "LONGITUDE INTEGER," +
                "DATE TEXT," +
                "TIME TEXT" +
                ")");


        db.execSQL(" create table " + INFECTED_ENCOUNTERS_TABLE + "(" +
                "INFECTED_USER_ID TEXT PRIMARY KEY, " +
                "DATE_REPORTED TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + ENCOUNTERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PERSONAL_INFO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GPS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + INFECTED_ENCOUNTERS_TABLE);
        onCreate(db);
    }

    public boolean oldinsertEncounterData(String ID, String ENCOUNTER_DATE, String ENCOUNTER_TIME) {
        SQLiteDatabase db = getWritableDatabase();
        long result;

        // Insert new encounter into db
        ContentValues newValues = new ContentValues();
        newValues.put("ID", ID);
        newValues.put("ENCOUNTER_DATE", ENCOUNTER_DATE);
        newValues.put("ENCOUNTER_TIME", ENCOUNTER_TIME);
        result = db.insert(ENCOUNTERS_TABLE, null, newValues);

        return result != -1;
    }

    public boolean insertEncounterData(String ID, String ENCOUNTER_DATE, String ENCOUNTER_TIME) {
        SQLiteDatabase db = getWritableDatabase();
        long result;

        if (!getEncounterData(ID).equals("Data Not Found")) {
            String sql = "UPDATE ENCOUNTERS_TABLE SET ENCOUNTER_DATE = '" + ENCOUNTER_DATE +"', ENCOUNTER_TIME = '" + ENCOUNTER_TIME + "' WHERE ID='" + ID + "'";
            db.execSQL(sql);
            result = 1;
        } else {
            // Insert new encounter into db
            ContentValues newValues = new ContentValues();
            newValues.put("ID", ID);
            newValues.put("ENCOUNTER_DATE", ENCOUNTER_DATE);
            newValues.put("ENCOUNTER_TIME", ENCOUNTER_TIME);
            result = db.insert(ENCOUNTERS_TABLE, null, newValues);
        }

        return result != -1;
    }

    public boolean insertGPSData(double lat, double lon, String date, String time) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("LATITUDE", lat);
        newValues.put("LONGITUDE", lon);
        newValues.put("DATE", date);
        newValues.put("TIME", time);
        long result = db.replace(GPS_TABLE, null, newValues);
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

    public boolean insertInfectedEncounterData(String InfectedUserID, String dateReported) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("INFECTED_USER_ID", InfectedUserID);
        newValues.put("DATE_REPORTED", dateReported);
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

    public String getInfectedData(String ID) {
        String result = "Data Not Found";
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "INFECTED_USER_ID=?";
        String[] whereArgs = new String[]{String.valueOf(ID)};
        Cursor csr = db.query(INFECTED_ENCOUNTERS_TABLE, null, whereClause, whereArgs, null, null, null);
        if (csr.moveToFirst()) {
            result = csr.getString(csr.getColumnIndex(INFECTED_ENCOUNTERS_COL1));
        }
        return result;
    }

    public ArrayList<String> getListOfInfectedIDs() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> listOfInfectedIDs = new ArrayList<>();

        String sql = "SELECT " + INFECTED_ENCOUNTERS_COL1 + " FROM " + INFECTED_ENCOUNTERS_TABLE;

        try (Cursor cursor = db.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                listOfInfectedIDs.add(cursor.getString(0));
            }
        }

        return listOfInfectedIDs;
    }

    public void deletedAgedEncounterData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM ENCOUNTERS_TABLE WHERE ENCOUNTER_DATE < date('now','-21 day')";
        db.execSQL(sql);
    }

    public void deleteAgedGPSData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM GPS_TABLE WHERE DATE < date('now','-21 day')";
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


    public int getNumOfGPSData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM GPS_TABLE";
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

    public boolean checkIDInEncntrTbl(String id) {
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


    // Method to check if Data is in DB and has boolean attached
    public boolean newCheckIsDataInDB(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns = {"ID"};
        String selection = "ID=? AND IS_INFECTED=?";
        String[] selectionArgs = {id, "true"};
        String limit = "1";

        Cursor cursor = db.query("Encounters_Table", columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public void deleteAllInfectedData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM " + INFECTED_ENCOUNTERS_TABLE;
        db.execSQL(sql);
    }

    public void deleteSingleInfectedData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM " + INFECTED_ENCOUNTERS_TABLE + " WHERE INFECTED_USER_ID = " + id;
        db.execSQL(sql);
    }

    public ArrayList<GPSRecord> getAllGPSData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM GPS_TABLE ORDER BY date(DATE)";

        ArrayList<GPSRecord> arrayList = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        try {
            while (cursor.moveToNext()) {
                double lat = cursor.getDouble(0);
                double lon = cursor.getDouble(1);
                String date = cursor.getString(2);
                String time = cursor.getString(3);

                arrayList.add(new GPSRecord(lat, lon, date, time));
            }
        } finally {
            cursor.close();
        }

        return arrayList;
    }

    public ArrayList<GPSRecord> getGPSDataForDay(String dateCondition) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM GPS_TABLE WHERE strftime('%Y-%m-%d', DATE) = '"+ dateCondition +"' ORDER BY time(TIME)";

        ArrayList<GPSRecord> arrayList = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        try {
            while (cursor.moveToNext()) {
                double lat = cursor.getDouble(0);
                double lon = cursor.getDouble(1);
                String date = cursor.getString(2);
                String time = cursor.getString(3);

                arrayList.add(new GPSRecord(lat, lon, date, time));
            }
        } finally {
            cursor.close();
        }

        return arrayList;

    }
}
