package com.covid.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.covid.utils.txtFile;

public class BLEReceiver extends BroadcastReceiver {
    public BLEReceiver() {
        super();
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        txtFile.writeToFile("WORKING WORKING WORKING");
    }
}
