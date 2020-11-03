package com.myBubble.database;

import com.myBubble.MainActivity;
import com.myBubble.utils.CodeManager;

public class PersonalData {

    public static void addOnInstallData() {
        //App Unique ID
        String personalID =  String.valueOf(CodeManager.generateCode());
        MainActivity.myDB.insertPersonalData(personalID);
    }
}
