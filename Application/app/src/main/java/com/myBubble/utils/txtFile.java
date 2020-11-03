package com.myBubble.utils;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.myBubble.MainActivity.logPath;

public class txtFile {

    public static void writeToFile(String input) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss'Z'");

        Date date = Calendar.getInstance().getTime();
        String fileName = dateformat.format(date) + ".txt";

        File test = new File(logPath, fileName);

        try {
            FileWriter fileWriter = new FileWriter(test);
            fileWriter.write(input);
            fileWriter.close();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }
}
