package com.covid.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.covid.R;
import com.covid.database.cloud.VolleyDELETE;
import com.covid.database.cloud.VolleyGET;
import com.covid.database.cloud.VolleyPOST;
import com.covid.utils.utilNotification;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.covid.MainActivity.activeExpo;
import static com.covid.MainActivity.hasExpo;
import static com.covid.MainActivity.myDB;
import static com.covid.MainActivity.myID;
import static com.covid.MainActivity.setHasExpo;


public class NotificationsFragment extends Fragment {

    private static String idAdd = "Your ID was added to the active infections database.";
    private static String idRemove = "Your ID was removed from the active infections database";

    private NotificationsViewModel notificationsViewModel;

    // TextView vars
    TextView txtStatus;
    TextView txtStatusDetails;
    TextView txtInfectedIDValue;
    TextView txtIDValue;
    TextView txtExposure;

    // MaterialCard vars
    private static MaterialCardView cardExposure;
    private static MaterialCardView cardStatus;

    // Colour vars
    private static int motorwayGreen;
    private static int red;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);



        // Initialise colours
        motorwayGreen = getResources().getColor(R.color.motorwayGreen);
        red = getResources().getColor(R.color.red);

        // Initialise TextViews
        txtStatus = root.findViewById(R.id.txtViewStatus);
        txtStatusDetails = root.findViewById(R.id.txtViewStatusDetail);
        txtExposure = root.findViewById(R.id.txtExposure);
        txtInfectedIDValue = root.findViewById(R.id.txtInfectedDBValue);

        txtIDValue = root.findViewById(R.id.txtIDValue);
        txtIDValue.setText(myID);

        // Initialise MaterialCards
        cardExposure = root.findViewById(R.id.cardExposure);
        cardStatus = root.findViewById(R.id.cardStatus);

        // Set the initial card colours
        deployCardColours();

        // Call method to set the initial display of the infected DB size
        updateNumber();



        // Create onClick methods for the buttons
        txtExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volleyHandler();
                //expoHandler();
                //VolleyPOST.setInfectedUsers(getContext());
                //VolleyDELETE.deleteInfectedUser(getContext());
            }
        });

        txtInfectedIDValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtInfectedIDValue.setText("Updating Database");
                //VolleyGET.checkExposure(getContext());
                callVolley(getContext());
            }
        });

        return root;
    }


    // Method to set card colours and text contents
    public void deployCardColours(){
        if (activeExpo == false){
            cardExposure.setCardBackgroundColor(red);
            cardStatus.setCardBackgroundColor(motorwayGreen);

            txtStatus.setText(R.string.notefragment_status_healthy);
            txtStatusDetails.setText(R.string.notefragment_status_healthy_detail);

            txtExposure.setText(R.string.note_fragment_confirm_expo);
        }
        else{
            cardExposure.setCardBackgroundColor(motorwayGreen);
            cardStatus.setCardBackgroundColor(red);

            txtStatus.setText(R.string.notefragment_status_infectious);
            txtStatusDetails.setText(R.string.notefragment_status_infectious_detail);

            txtExposure.setText(R.string.note_fragment_revert_expo);
        }
    }

    // Method to handle Volleys
    public void volleyHandler(){
        if (activeExpo == false){
            activeExpo = true;
            VolleyPOST.setInfectedUsers(getContext());
            Toast.makeText(getContext(), idAdd, Toast.LENGTH_SHORT).show();
        }
        else {
            activeExpo = false;
            VolleyDELETE.deleteInfectedUser(getContext());
            Toast.makeText(getContext(), idRemove, Toast.LENGTH_SHORT).show();
        }
        deployCardColours();
    }




    public void expoHandler(){
        if (activeExpo == false){
            activeExpo = true;
            VolleyPOST.setInfectedUsers(getContext());
            cardExposure.setCardBackgroundColor(motorwayGreen);
            Toast.makeText(getContext(), idAdd, Toast.LENGTH_SHORT).show();

        }
        else{
            activeExpo = false;
            VolleyDELETE.deleteInfectedUser(getContext());
            cardExposure.setCardBackgroundColor(red);
            Toast.makeText(getContext(), idRemove, Toast.LENGTH_SHORT).show();
        }
    }


    public void updateNumber(){
        if(myDB.getNumOfInfectedEncounters() > -1){
            int size = myDB.getNumOfInfectedEncounters();
            String newString = "Infected count = " + String.valueOf(size);
            txtInfectedIDValue.setText(newString);
        }
    }




    // ----------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------
    // NEW VOLLEY CODE
    // ----------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------
    public interface VolleyCallBack{
        void onSuccess();
    }


    public void callVolley(Context context){

        volleyRequest(new VolleyGET.VolleyCallBack(){
            @Override
            public void onSuccess() {
                updateNumber();

            }
        }, context);
    }


    public void volleyRequest(final VolleyGET.VolleyCallBack callBack, Context context){

        RequestQueue queue = Volley.newRequestQueue(context);
        String requestURL = "https://s6bimnllqb.execute-api.ap-southeast-2.amazonaws.com/prod/data";


        final JSONArray[] jsonArrays = new JSONArray[1];
        JsonArrayRequest request;

        Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                String userID = null;
                try {
                    userID = response.getString(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Clear the current infected DB
                myDB.deleteAllInfectedData();

                for (int i = 0; i < response.length(); i++) {
                    JSONObject userData;
                    try {
                        userData = (JSONObject) response.get(i);
                        String infectedUserID = (String) userData.get("InfectedUserID");
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

                                utilNotification.displayEXPONO(context, content);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                callBack.onSuccess();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.getMessage());
            }
        };
        request = new JsonArrayRequest(Request.Method.GET, requestURL, null, responseListener, errorListener);

        queue.add(request);
    }


    public static String convertEpochDate(long epochDate) {
        Date date = new Date(epochDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        //Long dateLong = Long.parseLong(sdf.format(epochDate));
        //String date = dateLong.toString();
        String infectedDate = sdf.format(date);
        return infectedDate;
    }

    // ----------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------
    // END BLOCK
    // ----------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------

}