package com.covid.ui.notifications;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.covid.R;
import com.google.android.material.card.MaterialCardView;

public class NotificationsFragment extends Fragment {

    private static MaterialCardView cardExposure;
    private static int motorwayGreen;
    private static int babyBlue;
    private static LinearLayout layout;

    private Boolean expoState;



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


        txtExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expoHandler();
            }
        });


        return root;
    }


    public void expoHandler(){
        if (expoState == null || expoState == false){
            expoState = true;
            cardExposure.setCardBackgroundColor(motorwayGreen);
        }
        else{
            expoState = false;
            cardExposure.setCardBackgroundColor(babyBlue);
        }


    }




}