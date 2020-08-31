package com.covid.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.ArrayList;





//Class
//Manages reading and writing of user saved data to the device in a specific save file
//Save file SHOULD be encrypted, MAY not be
//Stores:
// - app install code
// - user phone number, if supplied
// - user settings and preference data
public class FileManager {

    String saveFileName = "tracerFile.txt";

        public FileManager(){

    }


    public int writeSave(String input){
        File saveFile = new File(saveFileName);
        int responseCode = 0;

        try{
            FileWriter fw = new FileWriter(saveFile);
            fw.write(input);
            fw.close();

            responseCode = 1;

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return responseCode;
    }


    public ArrayList<String> readSave(){
        ArrayList<String>readData = new ArrayList<>();

        try{
            FileReader fr = new FileReader(saveFileName);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while((line = br.readLine())!= null){
                readData.add(line);
            }

            br.close();
            fr.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        //TODO
        //Consider creating a class attribute of read data, and assigning the data there so that it
        //is accessible and comparable with newer reads/updates
        return readData;
    }

}
