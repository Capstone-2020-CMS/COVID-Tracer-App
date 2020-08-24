package com.covid;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(RobolectricTestRunner.class)


public class DatabaseTest {




    @Test
    public void databaseExists_returnsTrue() throws Exception{
        boolean databaseExists = checkDataBaseExists("ENCOUNTER DATABASE");
        assertTrue(databaseExists);
    }

    public void databaseExists_returnsFalse() throws Exception{
        boolean databaseExists = checkDataBaseExists("NONEXISTENT DATABASE");
        assertFalse(databaseExists);
    }


    @Test
    public void UniqueID_isAdded() throws Exception{

    }

    @Test
    public void encounterDate_isAdded() throws Exception{

    }

    @Test
    public void UniqueID_isRetrieved() throws Exception{

    }

    @Test
    public void encounterDate_isRetrieved() throws Exception{

    }



}
