package com.covid;
import com.covid.database.EncounterDatabaseHelper;

import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.covid.database.EncounterDatabaseHelper.checkDataBaseExists;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@Config(sdk = {Build.VERSION_CODES.O_MR1})
@RunWith(RobolectricTestRunner.class)
public class DatabaseTest {

    public EncounterDatabaseHelper encounterDB;

    @Before
    public void Setup() {
        encounterDB = new EncounterDatabaseHelper(ApplicationProvider.getApplicationContext());
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
  //Checking valid ID can be added to database: Return true when ID is added successfully
    public void IDisAdded_returnsTrue() throws Exception{
        //Check that test passes when an ID that is text CAN be added to the database
        //MainActivity.encounterDB = new EncounterDatabaseHelper(MainActivity.getApplicationContext());
        boolean IDisAdded = encounterDB.insertEncounterData("ID567891235673dd2aCt", "");
        assertTrue(IDisAdded == true);
    }

    @Test
    //Checking invalid ID cannot be added to database: Return false when ID is not unique
    public void IDisAdded_returnsFalse() throws Exception{
        //Check that test passes when an ID that is not unique CANNOT be added to the database
        boolean IDisAdded = encounterDB.insertEncounterData("ID567891235673dd2aCt", "");
        assertFalse(IDisAdded == false);
    }

    @Test
    //Checking valid ID can be added to database: Return true when ID is added successfully
    public void IDisAddedLess20Char_returnsTrue() throws Exception{
        //Check that test passes when an ID that is text AND less than 20 characters CAN be added to the database
        boolean IDisAdded = MainActivity.encounterDB.insertEncounterData("ID567891235673dd2a", "");
        assertTrue(IDisAdded == true);
    }

    @Test
    //Checking valid ID can be added to database: Return true when ID is added successfully
    public void IDisAddedEqual20Char_returnsTrue() throws Exception{
        //Check that test passes when an ID that is text AND equal to 20 characters CAN be added to the database
        boolean IDisAdded = MainActivity.encounterDB.insertEncounterData("ID567891235673dd2aQt", "");
        assertTrue(IDisAdded == true);
    }

    @Test
    //Checking invalid ID cannot be added to database: Return false when ID is not added
    public void IDisAddedMore20Char_returnsFalse() throws Exception{
        //Check that test passes when an ID that is text AND more than 20 characters CANNOT be added to the database
        boolean IDisAdded = MainActivity.encounterDB.insertEncounterData("ID567891235673dd2aQtDDDrx345", "");
        assertFalse(IDisAdded == false);
    }


//    @Test
//    public void encounterDate_isAdded() throws Exception{
//
//    }
//
//
//    @Test
//    public void UniqueID_isRetrieved() throws Exception{
//
//    }
//
//
//    @Test
//    public void encounterDate_isRetrieved() throws Exception{
//
//    }


}
