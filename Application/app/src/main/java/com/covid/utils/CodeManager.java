package com.covid.utils;

import java.security.SecureRandom;
import java.util.Calendar;


public class CodeManager {

    private static int codeLength = 12;

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
