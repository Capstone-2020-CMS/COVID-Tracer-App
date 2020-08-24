package com.covid;

import com.covid.utils.CodeManager;

import org.junit.Test;

import java.security.MessageDigest;
import java.security.MessageDigestSpi;
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

}
