package com.covid;
import com.covid.database.DatabaseHelper;
import com.covid.utils.CodeManager;

import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import static com.covid.database.DatabaseHelper.ENCOUNTERS_TABLE;
import static com.covid.database.DatabaseHelper.checkDataBaseExists;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@Config(sdk = {Build.VERSION_CODES.O_MR1})
@RunWith(RobolectricTestRunner.class)
public class DatabaseTest {

    public DatabaseHelper myDB;
    public String personalID = String.valueOf(CodeManager.generateCode());

    @Before
    public void Setup() {
        myDB = new DatabaseHelper(ApplicationProvider.getApplicationContext());
    }



        /*=============================================================================
 |          Task: Build Database and define tables
 |   Description: Checking that data can be added to the databases
 *===========================================================================*/
  @Test
  //Checking valid ID can be added to Encounters table in database: Return true when ID is added successfully
    public void IDisAdded_returnsTrue() throws Exception{
        //Check that test passes when an ID that is text CAN be added to the database
        boolean IDisAdded = myDB.insertEncounterData("ID567891235673dd2aCt", "", "");
        assertTrue(IDisAdded);
    }

    @Test
    public void validDataisRetrieved() throws Exception{
        myDB.insertEncounterData("IDtestUniqueID", "13/02/1999", "4.00pm");
        String actualData = myDB.getEncounterData("IDtestUniqueID");
        String expectedData = "IDtestUniqueID, 13/02/1999, 4.00pm";
        assertEquals(expectedData, actualData);
    }

    @Test
    public void invalidDataisNotRetrieved() throws Exception{
        myDB.insertEncounterData("IDtestUniqueID", "13/02/1999", "4.00pm");
        String actualData = myDB.getEncounterData("IDtestUniqueID");
        String expectedData = "RANDOMIDtestUniqueID";
        assertNotEquals(expectedData, actualData);
    }

    @Test
    //Checking valid date can be added to Encounters table in database: Return true when date is added successfully
    public void encounterDate_isAdded() throws Exception{
        //Check that test passes when a date that is text CAN be added to the database
        boolean IDisAdded = myDB.insertEncounterData("", "2020-08-27", "");
        assertTrue(IDisAdded);
    }

    @Test
    //Checking valid time can be added to Encounters table in database: Return true when time is added successfully
    public void encounterTime_isAdded() throws Exception{
        //Check that test passes when a date that is text CAN be added to the database
        boolean IDisAdded = myDB.insertEncounterData("", "", "14:59");
        assertTrue(IDisAdded);
    }


    /*=============================================================================
|          Task: Write Personal ID to Database
|   Description: Checking that personalID can be added to database using generated unique ID
*===========================================================================*/
    @Test
    //Checking valid ID can be added to Personal Data table in database: Return true when ID is added successfully
    public void PersonalIDisAdded_returnsTrue() throws Exception{
        //Check that test passes when an ID that is text CAN be added to the database
        boolean IDisAdded = myDB.insertPersonalData("ID567891235673dd2tfLC");
        assertTrue(IDisAdded);
    }

    @Test
    //Checking invalid ID cannot be added to Personal Data table in database: Return false when ID is not unique
    public void PersonalIDisAdded_returnsFalse() throws Exception{
        //Check that test passes when an ID that is not unique CANNOT be added to the database
        myDB.insertPersonalData("ID567891235673dd2tfLC");
        boolean IDisAdded = myDB.insertPersonalData("ID567891235673dd2tfLC");
        assertFalse(IDisAdded);
    }

    @Test
    //Checking valid ID from codeManager can be added to Personal Data table in database: Return true when ID is added successfully
    public void PersonalIDisAddedCodeManager_returnsTrue() throws Exception{
        //Check that test passes when an ID that is text CAN be added to the database
        boolean IDisAdded = myDB.insertPersonalData(String.valueOf(CodeManager.generateCode()));
        assertTrue(IDisAdded);
    }

    @Test
    //Checking valid ID from codeManager can be added to Personal Data table in database: Return true when ID is added successfully
    public void PersonalIDisAddedGeneratedString_returnsTrue() throws Exception{
        //Check that test passes when an ID that is text CAN be added to the database
        boolean IDisAdded = myDB.insertPersonalData(personalID);
        assertTrue(IDisAdded);
    }

    @Test
    //Checking invalid ID cannot be added to Personal Data table in database: Return false when ID is not unique
    public void PersonalIDisAddedGeneratedString_returnsFalse() throws Exception{
        //Check that test passes when an ID that is not unique CANNOT be added to the database
        myDB.insertPersonalData(personalID);
        boolean IDisAdded = myDB.insertPersonalData(personalID);
        assertFalse(IDisAdded);
    }


    /*=============================================================================
|          Task:  Delete aged encounter IDs from the database
|   Description: Tests check if data older than 21 days is deleted, checks method deletedAgedEncounterData()
*===========================================================================*/
    @Test
    //Checking that dates older than 21 days are deleted: Return true when dates are deleted
    public void agedDateIsDeleted() throws Exception{
        myDB.insertEncounterData("IDOldDate>21days", "2020-01-01", "14:59");
        String unexpectedData = myDB.getEncounterData("IDOldDate>21days");
        myDB.deletedAgedEncounterData();
        String actualData = myDB.getEncounterData("IDOldDate>21days");
        assertNotEquals(unexpectedData, actualData);
    }

    private String yesterdayDate() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        cal.add(Calendar.DATE, -1);
        String yesterday = dateFormat.format(cal.getTime()).toString();
        return yesterday;
    }

    @Test
    //Checking that dates less than 21 days aren't deleted
    public void notAgedDateIsNotDeleted() throws Exception{
        String encounterDate = yesterdayDate();
        myDB.insertEncounterData("IDDate<21days", encounterDate, "4.00pm");
        String expectedData = myDB.getEncounterData("IDDate<21days");
        myDB.deletedAgedEncounterData();
        String actualData = myDB.getEncounterData("IDDate<21days");
        assertEquals(expectedData, actualData);
    }

    private String date21daysAgo() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        cal.add(Calendar.DATE, -21);
        String yesterday = dateFormat.format(cal.getTime()).toString();
        return yesterday;
    }

    @Test
    //Checking that dates exactly 21 days ago aren't deleted
    public void agedDateIsDeletedExactly21() throws Exception{
        String encounterDate = date21daysAgo();
        myDB.insertEncounterData("IDDate=21days", encounterDate, "4.00pm");
        String expectedData = myDB.getEncounterData("IDDate=21days");
        myDB.deletedAgedEncounterData();
        String actualData = myDB.getEncounterData("IDDate=21days");
        assertEquals(expectedData, actualData);
    }


        /*=============================================================================
|          Task:  Update date in database
|   Description: Checking that date is updated if ID already exists in database
*===========================================================================*/
        @Test
    //Checking that date is updated if ID exists
    public void EncounterDate_isUpdated() throws Exception{
            myDB.insertEncounterData("ID567891235673dd2aUp", "2020-08-31", "10:09");
            boolean DataIsUpdated = myDB.insertEncounterData("ID567891235673dd2aUp", "2020-09-01", "01:44");
            assertTrue(DataIsUpdated);
        }


    /*=============================================================================
|          Task:  Get date
|   Description: Testing to check Date() method
*===========================================================================*/
    private String Date() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(cal.getTime()).toString();
        return currentDate;
    }

    @Test
    //Checking that current date is added to database
    public void currentDate_isAdded() throws Exception{
        String encounterDate = Date();
        myDB.insertEncounterData("IDtestUniqueID", encounterDate, "4.00pm");
        String actualData = myDB.getEncounterData("IDtestUniqueID");
        String expectedData = "IDtestUniqueID, 2020-09-01, 4.00pm";
        assertEquals(expectedData, actualData);
    }


        /*=============================================================================
|          Task:  Get time
|   Description: Testing to check Time() method
*===========================================================================*/
        private String Time() {
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("HH-mm");
            String currentTime = dateFormat.format(cal.getTime()).toString();
            return currentTime;
        }

    @Test
    //Checking that current time is added to database
    public void currentTime_isAdded() throws Exception{
        String encounterTime = Time();
        myDB.insertEncounterData("IDtestUniqueID", "2020-09-01", encounterTime);
        String actualData = myDB.getEncounterData("IDtestUniqueID");
        String expectedData = "IDtestUniqueID, 2020-09-01, " + encounterTime;
        assertEquals(expectedData, actualData);
    }


}
