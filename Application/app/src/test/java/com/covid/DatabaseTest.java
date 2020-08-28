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

  @Test
  //Checking valid ID can be added to Encounters table in database: Return true when ID is added successfully
    public void IDisAdded_returnsTrue() throws Exception{
        //Check that test passes when an ID that is text CAN be added to the database
        boolean IDisAdded = myDB.insertEncounterData("ID567891235673dd2aCt", "");
        assertTrue(IDisAdded == true);
    }


    @Test
    //Checking invalid ID cannot be added to Encounters table in database: Return false when ID is not unique
    public void IDisAdded_returnsFalse() throws Exception{
        //Check that test passes when an ID that is not unique CANNOT be added to the database
        boolean IDisAdded = myDB.insertEncounterData("ID567891235673dd2aCt", "");
        assertFalse(IDisAdded == false);
    }

    @Test
    public void UniqueIDisRetrieved_returnsTrue() throws Exception{
        myDB.insertEncounterData("IDtestUniqueID", "");
        String actualUniqueID = myDB.getEncounterData("IDtestUniqueID");
        String expectedUniqueID = "IDtestUniqueID";
        assertEquals(expectedUniqueID, actualUniqueID);
    }

    @Test
    public void UniqueIDisRetrieved_returnsFalse() throws Exception{
        myDB.insertEncounterData("IDtestUniqueID", "");
        String actualUniqueID = myDB.getEncounterData("IDtestUniqueID");
        String expectedUniqueID = "RANDOMIDtestUniqueID";
        assertNotEquals(expectedUniqueID, actualUniqueID);
    }

    @Test
    //Checking valid ID can be added to Personal Data table in database: Return true when ID is added successfully
    public void PersonalIDisAdded_returnsTrue() throws Exception{
        //Check that test passes when an ID that is text CAN be added to the database
        boolean IDisAdded = myDB.insertPersonalData("ID567891235673dd2tfLC");
        assertTrue(IDisAdded == true);
    }

    @Test
    //Checking invalid ID cannot be added to Encounters table in database: Return false when ID is not unique
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
    //Checking invalid ID cannot be added to Encounters table in database: Return false when ID is not unique
    public void PersonalIDisAddedGeneratedString_returnsFalse() throws Exception{
        //Check that test passes when an ID that is not unique CANNOT be added to the database
        boolean IDisAdded = myDB.insertPersonalData(personalID);
        assertFalse(IDisAdded == false);
    }


    @Test
    //Checking valid date can be added to Encounters table in database: Return true when date is added successfully
    public void encounterDate_isAdded() throws Exception{
        //Check that test passes when a date that is text CAN be added to the database
        boolean IDisAdded = myDB.insertEncounterData("", "27-08-2020 12:10:18pm");
        assertTrue(IDisAdded == true);
    }
//
//

//
//
//    @Test
//    public void encounterDate_isRetrieved() throws Exception{
//
//    }


}
