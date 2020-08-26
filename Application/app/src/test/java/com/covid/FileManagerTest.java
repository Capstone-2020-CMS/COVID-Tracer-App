package com.covid;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

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

        try{

            FileReader fileReader = new FileReader(fileName);

            FileWriter fileWriter = new FileWriter(testFile);
            fileWriter.write("TEST");
            fileWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }


    }



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


/*        EditText textmsg;
        static final int READ_BLOCK_SIZE = 100;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            textmsg=(EditText)findViewById(R.id.editText1);
        }

        // write text to file
        public void WriteBtn(View v) {
            // add-write text into file
            try {
                FileOutputStream fileout=openFileOutput("mytextfile.txt", MODE_PRIVATE);
                OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
                outputWriter.write(textmsg.getText().toString());
                outputWriter.close();

                //display file saved message
                Toast.makeText(getBaseContext(), "File saved successfully!",
                        Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Read text from file
        public void ReadBtn(View v) {
            //reading text from file
            try {
                FileInputStream fileIn=openFileInput("mytextfile.txt");
                InputStreamReader InputRead= new InputStreamReader(fileIn);

                char[] inputBuffer= new char[READ_BLOCK_SIZE];
                String s="";
                int charRead;

                while ((charRead=InputRead.read(inputBuffer))>0) {
                    // char to string conversion
                    String readstring=String.copyValueOf(inputBuffer,0,charRead);
                    s +=readstring;
                }
                InputRead.close();
                textmsg.setText(s);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/


