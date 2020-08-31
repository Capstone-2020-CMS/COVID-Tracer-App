package com.covid;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FileManagerTest{

    @Test
    public void file_Write(){

        String fileName = "testFile.txt";


        File testFile = new File(fileName);

        try{
            FileWriter fileWriter = new FileWriter(testFile);
            fileWriter.write("TEST");
            fileWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void file_Read(){

        String fileName = "testFile.txt";

        List<String>stringList = new ArrayList<String>();

        try{

            FileReader fileReader = new FileReader(fileName);

            BufferedReader br = new BufferedReader(fileReader);

            String line;

            while ((line = br.readLine()) != null){
                stringList.add(line);
            }

            br.close();
            fileReader.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        assertEquals("TEST", stringList.get(0));


    }



}




