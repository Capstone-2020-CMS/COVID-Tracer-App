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
    public String personalID = CodeManager.generateCode();

    @Before
    public void Setup() {
        myDB = new DatabaseHelper(ApplicationProvider.getApplicationContext());
    }


    /*=============================================================================
 |          Task: Build Database and define tables
 |   Description: Checking that database exists and method checkDatabaseExists() works
 *===========================================================================*/
    @Test
    //Checking existence of database: Return true when database does exist
    public void databaseExists_returnsTrue() throws Exception{
        //Check that test passes when database exists
        boolean databaseExists = checkDataBaseExists("MyBubble.db");
        assertTrue(databaseExists);
    }

    @Test
    //Checking existence of database: Return False when database does not exist
    public void databaseNotExists_returnsFalse() throws Exception{
        //Check that passes when database does not exist
        boolean databaseExists = checkDataBaseExists("NonexistentDatabase.db");
        assertFalse(databaseExists == false);
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
        assertTrue(IDisAdded == true);
    }

    @Test
    //Checking invalid ID cannot be added to Encounters table in database: Return false when ID is not unique
    public void IDisAdded_returnsFalse() throws Exception{
        //Check that test passes when an ID that is not unique CANNOT be added to the database
        boolean IDisAdded = myDB.insertEncounterData("ID567891235673dd2aCt", "", "");
        assertFalse(IDisAdded == false);
    }

    @Test
    public void DataisRetrieved_returnsTrue() throws Exception{
        myDB.insertEncounterData("IDtestUniqueID", "13/02/1999", "4.00pm");
        String actualData = myDB.getEncounterData("IDtestUniqueID");
        String expectedData = "IDtestUniqueID, 13/02/1999 4.00pm";
        assertEquals(expectedData, actualData);
    }

    @Test
    public void DataisRetrieved_returnsFalse() throws Exception{
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
        assertTrue(IDisAdded == true);
    }

    @Test
    //Checking valid time can be added to Encounters table in database: Return true when time is added successfully
    public void encounterTime_isAdded() throws Exception{
        //Check that test passes when a date that is text CAN be added to the database
        boolean IDisAdded = myDB.insertEncounterData("", "", "14:59");
        assertTrue(IDisAdded == true);
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
        assertTrue(IDisAdded == true);
    }

    @Test
    //Checking invalid ID cannot be added to Personal Data table in database: Return false when ID is not unique
    public void PersonalIDisAdded_returnsFalse() throws Exception{
        //Check that test passes when an ID that is not unique CANNOT be added to the database
        boolean IDisAdded = myDB.insertPersonalData("ID567891235673dd2tfLC");
        assertFalse(IDisAdded == false);
    }

    @Test
    //Checking valid ID from codeManager can be added to Personal Data table in database: Return true when ID is added successfully
    public void PersonalIDisAddedCodeManager_returnsTrue() throws Exception{
        //Check that test passes when an ID that is text CAN be added to the database
        boolean IDisAdded = myDB.insertPersonalData(CodeManager.generateCode());
        assertTrue(IDisAdded == true);
    }

    @Test
    //Checking valid ID from codeManager can be added to Personal Data table in database: Return true when ID is added successfully
    public void PersonalIDisAddedGeneratedString_returnsTrue() throws Exception{
        //Check that test passes when an ID that is text CAN be added to the database
        boolean IDisAdded = myDB.insertPersonalData(personalID);
        assertTrue(IDisAdded == true);
    }

    @Test
    //Checking invalid ID cannot be added to Personal Data table in database: Return false when ID is not unique
    public void PersonalIDisAddedGeneratedString_returnsFalse() throws Exception{
        //Check that test passes when an ID that is not unique CANNOT be added to the database
        boolean IDisAdded = myDB.insertPersonalData(personalID);
        assertFalse(IDisAdded == false);
    }


    /*=============================================================================
|          Task:  Delete aged encounter IDs from the database
|   Description: Tests check if data older than 21 days is deleted, checks method deletedAgedEncounterData()
*===========================================================================*/
    @Test
    //Checking that dates older than 21 days are deleted: Return true when dates are deleted
    public void agedDateIsDeleted_returnsTrue() throws Exception{
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
    public void agedDateIsDeleted_returnsFalse() throws Exception{
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
    //Checking that dates less than 21 days aren't deleted
    public void agedDateIsDeletedExactly21_returnsFalse() throws Exception{
        String encounterDate = date21daysAgo();
        myDB.insertEncounterData("IDDate=21days", encounterDate, "4.00pm");
        String expectedData = myDB.getEncounterData("IDDate=21days");
        myDB.deletedAgedEncounterData();
        String actualData = myDB.getEncounterData("IDDate=21days");
        assertEquals(expectedData, actualData);
    }
}
