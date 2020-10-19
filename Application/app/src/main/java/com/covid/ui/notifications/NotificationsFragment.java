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
    private static int motorwayGreen;
    private static int red;
    private static LinearLayout layout;
    private TextView txtIDValue;
    private NotificationsViewModel notificationsViewModel;

    TextView txtInfectedIDValue;

    private int infectionNumber;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);


        motorwayGreen = getResources().getColor(R.color.motorwayGreen);
        red = getResources().getColor(R.color.red);


        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        final TextView txtExposure = root.findViewById(R.id.txtExposure);

        cardExposure = root.findViewById(R.id.cardExposure);
        layout = root.findViewById(R.id.linearlayoutH);

        txtIDValue = root.findViewById(R.id.txtIDValue);
        txtIDValue.setText(myID);

        txtExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expoHandler();
                VolleyPOST.setInfectedUsers(getContext());
            }
        });

        txtInfectedIDValue = root.findViewById(R.id.txtInfectedDBValue);

/*        if(myDB.getNumOfInfectedEncounters() > -1){
            int size = myDB.getNumOfInfectedEncounters();
            String newString = "Infected count = " + String.valueOf(size);
            txtInfectedIDValue.setText(newString);
        }*/

        updateNumber();

        txtInfectedIDValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtInfectedIDValue.setText("Updating Database");
                VolleyGET.checkExposure(getContext());
            }
        });


        return root;
    }

    public void expoHandler(){
        if (activeExpo == false){
            activeExpo = true;
            cardExposure.setCardBackgroundColor(motorwayGreen);
            Toast.makeText(getContext(), idAdd, Toast.LENGTH_SHORT).show();

        }
        else{
            activeExpo = false;
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