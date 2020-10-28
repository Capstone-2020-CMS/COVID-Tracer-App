package com.covid.database.cloud;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.covid.utils.InfectedUserData;
import com.covid.utils.utilNotification;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.covid.MainActivity.myDB;
import static com.covid.MainActivity.hasExpo;
import static com.covid.MainActivity.setHasExpo;

public class VolleyGET {

    @Deprecated
    public static void getInfectedUsers(Context context) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final JSONArray[] jsonArray = new JSONArray[1];


        String url = "https://s6bimnllqb.execute-api.ap-southeast-2.amazonaws.com/prod/data";


        JsonArrayRequest request;

        {
            Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {


                    String userID = null;
                    try {
                        userID = response.getString(1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("response", response.toString());
                    Log.d("userID", userID);
                    
                    myDB.deleteAllInfectedData();

                    for(int i = 0; i < response.length(); i++) {
                        JSONObject userData;
                        try {
                            userData = (JSONObject) response.get(i);
                            String infectedUserID = (String) userData.get("InfectedUserID");
                            boolean encounteredInfectedUser = myDB.checkIDInEncntrTbl(infectedUserID); //Not used yet!
                            long epochDate = (long) userData.get("date");
                            String dateReported = convertEpochDate(epochDate);
                            // Add code to build the infectedUsersTable from the JSON array
                            // by just cleaning the table and then reinserting all the new values
                            myDB.insertInfectedEncounterData(infectedUserID, dateReported);


                            // Refactor this boolean to be a variable of the Main Activity class and import it into this class
                            // Set boolean if any ID in the infectedUsersTable matches an ID in the encountersTable
                            // Maybe we could use two booleans and check if BOTH are true, to run the IF statement below
                            // Using two booleans might help eliminate double-up notifications for the same encounter




                            int numberOfInfectedEncounters = myDB.getNumOfInfectedEncounters(); //Check database is working
                            Log.v("number", String.valueOf(numberOfInfectedEncounters));
                            int j = 7; //line for breakpoint - does nothing


                            // If the boolean == true, call notification
                            // This code can be left largely untouched - Martin will try to figure out how to implement the notification
                            // from this point onwards
                            if(encounteredInfectedUser == true) {
                                // This line "insertInfectedEncounterData to be refactored into the TRY clause above ^^^
                                //myDB.insertInfectedEncounterData(infectedUserID);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", error.getMessage());
                }
            };

            request = new JsonArrayRequest(Request.Method.GET, url, null, responseListener, errorListener);

            requestQueue.add(request);

        }

   }
   public static void checkExposure(Context context) {

       String requestURL = "https://s6bimnllqb.execute-api.ap-southeast-2.amazonaws.com/prod/data";

       RequestQueue rQueue = Volley.newRequestQueue(context);
       final JSONArray[] jsonArrays = new JSONArray[1];
       JsonArrayRequest request;

       Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
           @Override
           public void onResponse(JSONArray response) {
               ArrayList<String> newInfectedList = new ArrayList<>();
               ArrayList<String> oldInfectedList = new ArrayList<>();
               String getOldQuery = "SELECT INFECTED_USER_ID FROM INFECTED_ENCOUNTERS_TABLE";
               try (Cursor cursor = myDB.getWritableDatabase().rawQuery(getOldQuery, null)) {
                   if (cursor.moveToNext()) {
                       oldInfectedList.add(cursor.getString(0));
                   }
               }

               // Clear the current infected DB
               myDB.deleteAllInfectedData();

               for (int i = 0; i < response.length(); i++) {
                   JSONObject userData;
                   try {
                       userData = (JSONObject) response.get(i);
                       String infectedUserID = (String) userData.get("InfectedUserID");
                       newInfectedList.add(infectedUserID);
                       long epochDate = (long) userData.get("date");
                       String dateReported = convertEpochDate(epochDate);
                       myDB.insertInfectedEncounterData(infectedUserID, dateReported);


                       String encounterData = myDB.getEncounterData(infectedUserID);
                       String content = "You encountered: " + encounterData;


                       // NEW CODE------------------------------------------------------------------
                       if(!myDB.getEncounterData(infectedUserID).equals("Data Not Found")) {

                           if(hasExpo == false){
                               setHasExpo(true);
                           }

                           if(!myDB.newCheckIsDataInDB(infectedUserID)){
                               String sql  = "UPDATE ENCOUNTERS_TABLE SET IS_INFECTED = 'true' WHERE ID='" + infectedUserID + "'";
                               SQLiteDatabase db = myDB.getWritableDatabase();
                               db.execSQL(sql);

                               String encounterDate = "placeholder";
                               String query2 = "SELECT ENCOUNTER_DATE FROM ENCOUNTERS_TABLE WHERE ID='" + infectedUserID + "'";
                               try (Cursor cursor = db.rawQuery(query2, null)) {
                                   if (cursor.moveToFirst()) {
                                       encounterDate = cursor.getString(0);
                                   }
                               }

                               InfectedUserData data = new InfectedUserData(infectedUserID, encounterDate, dateReported);

                               utilNotification.displayEXPONO(context, content, data);
                           }
                       }

                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }

               for (String s : oldInfectedList) {
                   if (!newInfectedList.contains(s)) {
                       myDB.getWritableDatabase().execSQL("UPDATE ENCOUNTERS_TABLE SET IS_INFECTED = 'false' WHERE ID='" + s + "'");
                   }
               }
           }
       };

       Response.ErrorListener errorListener = new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               Log.e("error", error.getMessage());
           }
       };
       request = new JsonArrayRequest(Request.Method.GET, requestURL, null, responseListener, errorListener);

       rQueue.add(request);

   }

    public static String convertEpochDate(long epochDate) {
        Date date = new Date(epochDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        //Long dateLong = Long.parseLong(sdf.format(epochDate));
        //String date = dateLong.toString();
        String infectedDate = sdf.format(date);
        return infectedDate;
    }
}
