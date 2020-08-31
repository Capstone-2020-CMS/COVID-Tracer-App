package com.covid.bluetooth;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.covid.R;
import com.covid.utils.txtFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.covid.MainActivity.NOTIFICATION_CHANNEL;
import static com.covid.MainActivity.bleScanner;
import static com.covid.MainActivity.scanSettings;

public class BLEService extends Service {
    private ScanCallback callback;
    private List<ScanFilter> scanFilters = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        createCallback();
        scanFilters.add(new ScanFilter.Builder().build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //bleScanner.startScan(scanFilters,scanSettings,callback);
        bleScanner.startScan(null,scanSettings,callback);
        startForeground(1, createForegroundNotification());
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createForegroundNotification() {
        // Build a notification using bytesRead and contentLength

        Context context = getApplicationContext();

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss'Z'");

        Date date = Calendar.getInstance().getTime();

        String title = dateformat.format(date);

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setContentTitle(title)
                .setContentText("BLE scanning is active")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .build();

        return notification;
    }

    private void createCallback() {
        callback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                txtFile.writeToFile(result.getDevice().getAddress());
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                txtFile.writeToFile("Error Code: " + Integer.toString(errorCode) + "\n" + getErrorDescription(errorCode));
            }
        };
    }

    private String getErrorDescription(int errorCode) {
        switch (errorCode) {
            case 1: return "Fails to start scan as BLE scan with the same settings is already started by the app.";
            case 2: return "Fails to start scan as app cannot be registered.";
            case 3: return "Fails to start power optimized scan as this feature is not supported.";
            case 4: return "Fails to start scan due an internal error";
            default: return "Unknown error code";
        }
    }
}
