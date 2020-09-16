package com.covid.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
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
import static com.covid.MainActivity.locationManager;
import static com.covid.MainActivity.providerName;
import static com.covid.MainActivity.wifiManager;

public class HomeFragment extends Fragment {

    private final int REQUEST_ENABLE_BT = 1;
    private final int REQUEST_ENABLE_GPS = 2;
    private final int REQUEST_ENABLE_INTERNET = 3;
    private int motorwayGreen;
    private int red;

    private MaterialCardView cardBluetoothStatus;
    private MaterialCardView cardLocationStatus;
    private MaterialCardView cardInternetStatus;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        // Get the text view for bubble size and set it
        TextView txtBubbleSize = root.findViewById(R.id.txtBubbleSize);
        txtBubbleSize.setText(Integer.toString(bubbleSize));

        cardBluetoothStatus = root.findViewById(R.id.cardBluetoothStatus);
        cardLocationStatus = root.findViewById(R.id.cardLocationStatus);
        cardInternetStatus = root.findViewById(R.id.cardInternetStatus);

        motorwayGreen = getResources().getColor(R.color.motorwayGreen);
        red = getResources().getColor(R.color.red);

        // Check the status of vital services
        checkStatus();

        // Bluetooth card on click
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

        // Location card on click
        cardLocationStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (providerName == null || !locationManager.isProviderEnabled(providerName)) {
                    Intent enableGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(enableGPSIntent);
                } else {
                    Toast.makeText(getContext(), "Location services are already enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Internet card on click
        cardInternetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wifiManager.isWifiEnabled()) {
                    Intent enableWifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(enableWifiIntent);
                } else {
                    Toast.makeText(getContext(), "WIFI is already enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        checkStatus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Bluetooth
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                toggleCardColour(true, cardBluetoothStatus);
            } else {
                Toast.makeText(this.getContext(), "Bluetooth was not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Function that checks the status of services and toggles colour as required
    private void checkStatus() {
        // Bluetooth
        if (adapter.isEnabled()) {
            toggleCardColour(true, cardBluetoothStatus);
        } else {
            toggleCardColour(false, cardBluetoothStatus);
        }

        // Location
        if (locationManager.isProviderEnabled(providerName)) {
            toggleCardColour(true, cardLocationStatus);
        } else {
            toggleCardColour(false, cardLocationStatus);
        }

        // Wifi
        if (wifiManager.isWifiEnabled()) {
            toggleCardColour(true, cardInternetStatus);
        } else {
            toggleCardColour(false, cardInternetStatus);
        }
    }

    private void toggleCardColour(boolean green, MaterialCardView cardView) {
        if (green) {
            cardView.setCardBackgroundColor(motorwayGreen);
        } else {
            cardView.setCardBackgroundColor(red);
        }
    }
}