package com.covid.utils;

import java.security.SecureRandom;
import java.util.Calendar;

//Class
//Called to generate a random string with prefix NZ, 3 random chars, and the timestamp in milliseconds (c.13 digits long)
// "NZ-XXX-0000000000000"
//This class should be created and run once on install.
//Can be modified to generate more codes to intensify privacy preservation
public class CodeManager {

    public CodeManager(){

    }


    public static String generateCode(){

        //Get time from calendar in milliseconds
        long longTime = Calendar.getInstance().getTimeInMillis();
        //Convert time to string
        String timeString = String.valueOf(longTime);
        //Create empty prefix string
        String prefixString = "";
        //randomly generate three characters and add to the prefix string as an adhoc salt
        for (int i = 0; i < 3; i++){
            SecureRandom r = new SecureRandom();
            int rNum = r.nextInt(125 - 33 + 1) + 33;
            char rChar = (char) rNum;
            prefixString += rChar;
        }

        //Create the codeString
        String codeString = "NZ-" + prefixString + "-" + timeString;
        //return the codeString
        return codeString;
    }
}
