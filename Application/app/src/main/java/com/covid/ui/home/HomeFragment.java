package com.covid.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.covid.MainActivity;
import com.covid.R;
import com.google.android.material.card.MaterialCardView;

import static android.app.Activity.RESULT_OK;
import static com.covid.MainActivity.adapter;
import static com.covid.MainActivity.bubbleSize;

public class HomeFragment extends Fragment {

    private final int REQUEST_ENABLE_BT = 1;
    private static MaterialCardView cardBluetoothStatus;
    private int motorwayGreen;
    private int red;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        // Get the text view for bubble size and set it
        TextView txtBubbleSize = root.findViewById(R.id.txtBubbleSize);
        txtBubbleSize.setText(Integer.toString(bubbleSize));
        cardBluetoothStatus = root.findViewById(R.id.cardBluetoothStatus);
        motorwayGreen = getResources().getColor(R.color.motorwayGreen);
        red = getResources().getColor(R.color.red);

        if (adapter.isEnabled()) {
            toggleCardColour(true);
        } else {
            toggleCardColour(false);
        }

        cardBluetoothStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!adapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    Toast.makeText(getContext(), "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Bluetooth
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                toggleCardColour(true);
            } else {
                Toast.makeText(this.getContext(), "Bluetooth was not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleCardColour(boolean green) {
        if (green) {
            cardBluetoothStatus.setCardBackgroundColor(motorwayGreen);
        } else {
            cardBluetoothStatus.setCardBackgroundColor(red);
        }
    }
}