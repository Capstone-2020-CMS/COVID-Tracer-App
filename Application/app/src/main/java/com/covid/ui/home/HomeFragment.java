package com.covid.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.covid.R;
import com.google.android.material.card.MaterialCardView;

import static com.covid.MainActivity.bubbleSize;

public class HomeFragment extends Fragment {

    private static MaterialCardView cardBluetoothStatus;
    private static int motorwayGreen;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        // Get the text view for bubble size and set it
        TextView txtBubbleSize = root.findViewById(R.id.txtBubbleSize);
        txtBubbleSize.setText(Integer.toString(bubbleSize));
        cardBluetoothStatus = root.findViewById(R.id.cardBluetoothStatus);
        motorwayGreen = getResources().getColor(R.color.motorwayGreen);
        return root;
    }

    public static void toggleCardColour() {
        cardBluetoothStatus.setCardBackgroundColor(motorwayGreen);
    }
}