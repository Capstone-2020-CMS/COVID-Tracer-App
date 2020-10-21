package com.covid.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.covid.R;
import com.covid.database.cloud.VolleyDELETE;
import com.covid.database.cloud.VolleyGET;
import com.covid.database.cloud.VolleyPOST;
import com.google.android.material.card.MaterialCardView;

import static com.covid.MainActivity.activeExpo;
import static com.covid.MainActivity.myDB;
import static com.covid.MainActivity.myID;


public class NotificationsFragment extends Fragment {

    private static String idAdd = "Your ID was added to the active infections database.";
    private static String idRemove = "Your ID was removed from the active infections database";

    private static MaterialCardView cardExposure;
    private static MaterialCardView cardStatus;
    private static int motorwayGreen;
    private static int red;
    private static LinearLayout layout;
    private NotificationsViewModel notificationsViewModel;



    private int infectionNumber;


    // New vars
    TextView txtStatus;
    TextView txtStatusDetails;

    TextView txtInfectedIDValue;
    TextView txtIDValue;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);


        motorwayGreen = getResources().getColor(R.color.motorwayGreen);
        red = getResources().getColor(R.color.red);


        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        final TextView txtExposure = root.findViewById(R.id.txtExposure);

        txtStatus = root.findViewById(R.id.txtViewStatus);
        txtStatusDetails = root.findViewById(R.id.txtViewStatusDetail);

        txtInfectedIDValue = root.findViewById(R.id.txtInfectedDBValue);


        cardExposure = root.findViewById(R.id.cardExposure);

        cardStatus = root.findViewById(R.id.cardStatus);


        layout = root.findViewById(R.id.linearlayoutH);

        txtIDValue = root.findViewById(R.id.txtIDValue);
        txtIDValue.setText(myID);


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
                VolleyGET.checkExposure(getContext());
            }
        });

        return root;
    }


    // Method to set card colours
    public void deployCardColours(){
        if (activeExpo == false){
            cardExposure.setCardBackgroundColor(red);
            cardStatus.setCardBackgroundColor(motorwayGreen);
        }
        else{
            cardExposure.setCardBackgroundColor(motorwayGreen);
            cardStatus.setCardBackgroundColor(red);
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

}