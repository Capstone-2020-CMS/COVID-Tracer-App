package com.covid.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.covid.R;
import com.covid.database.cloud.VolleyPost;
import com.google.android.material.card.MaterialCardView;

import static com.covid.MainActivity.activeExpo;
import static com.covid.MainActivity.myID;


public class NotificationsFragment extends Fragment {

    private static MaterialCardView cardExposure;
    private static int motorwayGreen;
    private static int babyBlue;
    private static LinearLayout layout;

    private TextView txtIDValue;






    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);


        motorwayGreen = getResources().getColor(R.color.motorwayGreen);
        babyBlue = getResources().getColor(R.color.primaryLightColor);




        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        final TextView txtExposure = root.findViewById(R.id.txtExposure);

        cardExposure = root.findViewById(R.id.cardExposure);
        layout = root.findViewById(R.id.linearlayoutH);

        txtIDValue = root.findViewById(R.id.txtIDValue);
        txtIDValue.setText(myID);






        Intent i = new Intent(getContext(), VolleyPost.class);

        txtExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expoHandler();
                VolleyPost.setInfectedUsers(getContext());
            }
        });


        return root;
    }




    public void expoHandler(){
        if (activeExpo == false){
            activeExpo = true;
            cardExposure.setCardBackgroundColor(motorwayGreen);

        }
        else{
            activeExpo = false;
            cardExposure.setCardBackgroundColor(babyBlue);
        }
    }




}