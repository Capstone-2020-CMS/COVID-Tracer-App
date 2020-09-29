package com.covid.database.cloud;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static com.covid.MainActivity.myDB;

public class VolleyGET {

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


                    for(int i = 0; i < response.length(); i++) {
                        JSONObject userData;
                        try {
                            userData = (JSONObject) response.get(i);
                            String infectedUserID = (String) userData.get("InfectedUserID");
                            boolean encounteredInfectedUser = myDB.CheckIsDataInDB(infectedUserID);
                            long date = (long) userData.get("date");
                            int j = 7;

                            if(encounteredInfectedUser == true) {
                                myDB.insertInfectedEncounterData(infectedUserID);
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

}
