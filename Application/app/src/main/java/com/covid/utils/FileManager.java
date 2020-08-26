package com.covid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;




//Class
//Manages reading and writing of user saved data to the device in a specific save file
//Save file SHOULD be encrypted, MAY not be
//Stores:
// - app install code
// - user phone number, if supplied
// - user settings and preference data
public class FileManager {
    FileOutputStream fOS;
    FileInputStream fIS;

        public FileManager(){

    }





/*    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss'Z'");

    Date date = Calendar.getInstance().getTime();
    String fileName = dateformat.format(date) + ".txt";

    File test = new File(logPath, fileName);

        try {
        FileWriter fileWriter = new FileWriter(test);
        fileWriter.write(input);
        fileWriter.close();
    } catch (java.io.IOException ex) {
        ex.printStackTrace();
    }*/


}
