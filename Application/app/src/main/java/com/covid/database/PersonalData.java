package com.covid.database;

import com.covid.MainActivity;
import com.covid.utils.CodeManager;

public class PersonalData {

    public static void addOnInstallData() {

        //App Unique ID
        String personalID =  String.valueOf(CodeManager.generateCode());
        MainActivity.encounterDB.insertPersonalData(personalID);
    }
}
