package com.covid.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class BLEReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context.getApplicationContext(), BLEService.class);
        ContextCompat.startForegroundService(context.getApplicationContext(), serviceIntent);
        Toast.makeText(context.getApplicationContext(), "Service has started", Toast.LENGTH_LONG).show();
    }
}
