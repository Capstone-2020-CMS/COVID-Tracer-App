package com.covid;

import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.covid.database.DatabaseHelper;
import com.covid.gps.GPSRecord;
import com.covid.utils.CodeManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Config(sdk = {Build.VERSION_CODES.O_MR1})
@RunWith(RobolectricTestRunner.class)
public class TimelineTest {

    private DatabaseHelper myDB;
    private String personalID = String.valueOf(CodeManager.generateCode());

    @Before
    public void Setup() {
        myDB = new DatabaseHelper(ApplicationProvider.getApplicationContext());
        myDB.insertPersonalData(personalID);
    }

    @Test
    public void getAllGPSDataAsList_IsWorking() {
        // Insert 5 GPS records for one day
        myDB.insertGPSData(1.1, 1.1, "2020-01-01", "14:59");
        myDB.insertGPSData(1.1, 1.1, "2020-01-01", "14:59");
        myDB.insertGPSData(1.1, 1.1, "2020-01-01", "14:59");
        myDB.insertGPSData(1.1, 1.1, "2020-01-01", "14:59");
        myDB.insertGPSData(1.1, 1.1, "2020-01-01", "14:59");

        ArrayList<GPSRecord> gpsRecordArrayList = myDB.getAllGPSData();

        assertEquals(5, gpsRecordArrayList.size());
    }

    @Test
    public void getGPSDataForOneDay_IsWorking() {
        // Insert 5 GPS records for one day
        myDB.insertGPSData(1.1, 1.1, "2020-01-01", "14:59");
        myDB.insertGPSData(1.1, 1.1, "2020-01-01", "14:59");
        myDB.insertGPSData(1.1, 1.1, "2020-01-01", "14:59");
        myDB.insertGPSData(1.1, 1.1, "2020-01-01", "14:59");
        myDB.insertGPSData(1.1, 1.1, "2020-01-01", "14:59");
        // Insert 5 GPS records for the next day
        myDB.insertGPSData(1.1, 1.1, "2020-01-02", "14:59");
        myDB.insertGPSData(1.1, 1.1, "2020-01-02", "14:12");
        myDB.insertGPSData(1.1, 1.1, "2020-01-02", "14:46");
        myDB.insertGPSData(1.1, 1.1, "2020-01-02", "14:32");
        myDB.insertGPSData(1.1, 1.1, "2020-01-02", "14:14");

        ArrayList<GPSRecord> gpsRecordArrayList = myDB.getGPSDataForDay("2020-01-02");

        assertEquals(5, gpsRecordArrayList.size());
        // Check that the list is ordered
        assertTrue(gpsRecordArrayList.get(0).getTime().charAt(3) < gpsRecordArrayList.get(4).getTime().charAt(3));
    }
}
