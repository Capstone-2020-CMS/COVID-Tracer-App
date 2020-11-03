package com.myBubble;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.myBubble.database.DatabaseHelper;
import com.myBubble.utils.CodeManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

@Config(sdk = {Build.VERSION_CODES.O_MR1})
@RunWith(RobolectricTestRunner.class)
public class InfectedDBStuff {

    public DatabaseHelper myDB;
    public String personalID = String.valueOf(CodeManager.generateCode());

    @Before
    public void Setup() {
        myDB = new DatabaseHelper(ApplicationProvider.getApplicationContext());

        myDB.insertInfectedEncounterData("1234", "2020-09-29");
        myDB.insertInfectedEncounterData("1235", "2020-09-29");
        myDB.insertInfectedEncounterData("1236", "2020-09-29");
        myDB.insertInfectedEncounterData("1237", "2020-09-29");

        myDB.insertEncounterData("12356", "2020-09-26", "10:00");
        myDB.insertEncounterData("1234", "2020-09-26", "10:00");
        myDB.insertEncounterData("12376", "2020-09-26", "10:00");
        myDB.insertEncounterData("1235", "2020-09-26", "10:00");
    }

    @Test
    public void changingInfectedStatus_IsWorking() {
        ArrayList<String> infectedEncounterData = new ArrayList<>();
        SQLiteDatabase db = myDB.getWritableDatabase();

        infectedEncounterData = myDB.getListOfInfectedIDs();

        for (String id : infectedEncounterData) {
            if (!myDB.getEncounterData(id).equals("Data Not Found")) {
                String sql  = "UPDATE INFECTED_ENCOUNTERS_TABLE SET ENCOUNTERED_STATUS = 'true' WHERE INFECTED_USER_ID='" + id + "'";
                db.execSQL(sql);
            }
        }

        String result = myDB.getInfectedData("1234");
        String result1 = myDB.getInfectedData("1235");
        String result2 = myDB.getInfectedData("1237");

        int test = 1+1;
    }

    @Test
    public void deleteInfectedData_IsWorking() {
        SQLiteDatabase db = myDB.getWritableDatabase();

        myDB.deleteAllInfectedData();

        ArrayList<String> infectedEncounterData = myDB.getListOfInfectedIDs();

        assert (infectedEncounterData.size() == 0);
    }
}
