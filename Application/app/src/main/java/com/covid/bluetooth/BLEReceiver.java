package com.covid.bluetooth;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.covid.MainActivity;
import com.covid.R;
import com.covid.utils.txtFile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.covid.MainActivity.NOTIFICATION_CHANNEL;

public class BLEReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context.getApplicationContext(), BLEService.class);
        //context.getApplicationContext().startService(serviceIntent);
        ContextCompat.startForegroundService(context.getApplicationContext(), serviceIntent);
        Toast.makeText(context.getApplicationContext(), "Service has started", Toast.LENGTH_LONG).show();
    }
}
