package com.covid;

import com.covid.utils.CodeManager;

import org.junit.Test;

import java.security.MessageDigest;
import java.security.MessageDigestSpi;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void code_isCorrectLength(){
        String input;
        input = CodeManager.generateCode();

        assertEquals(16, input.length());
    }


    @Test
    public void code_isGenerated(){

        //33 - 125


        String salt = "SALT";
        String bubbleID = "";
        boolean hasRun = false;

        int min = 65;
        int max = 90;

        for (int i = 0; i < 16; i++){
            Random r = new Random();
            int rNum = r.nextInt(max - min + 1) + min;

            char newChar = (char) rNum;
            bubbleID += newChar;
        }

        bubbleID += salt;

        if(bubbleID.length() == 20){
            hasRun = true;
        }

        assertEquals(true, hasRun);

    }

    @Test
    public void date_stringLength(){
        Date date = Calendar.getInstance().getTime();
        String dateString = date.toString();

        long longDate = date.getTime();
        String longString = String.valueOf(longDate);


        dateString = dateString.replaceAll("\\s", "");

        assertEquals("4", longString);
    }


/*    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }*/



}
