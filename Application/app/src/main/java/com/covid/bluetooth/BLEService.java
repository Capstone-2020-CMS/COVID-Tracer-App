package com.covid.bluetooth;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelUuid;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.covid.R;
import com.covid.database.EncountersData;
import com.covid.utils.txtFile;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import java.util.Random;

import static com.covid.MainActivity.NOTIFICATION_CHANNEL;
import static com.covid.MainActivity.advertiseData;
import static com.covid.MainActivity.advertiseSettings;
import static com.covid.MainActivity.bleAdvertiser;
import static com.covid.MainActivity.bleScanner;
import static com.covid.MainActivity.scanFilter;
import static com.covid.MainActivity.scanSettings;
import static com.covid.MainActivity.serviceUUID;
import static com.covid.database.EncountersData.recordEncountersData;
import static com.covid.utils.CodeManager.getLongFromByteArray;

public class BLEService extends Service {
    private ScanCallback scanCallback;
    private AdvertiseCallback advertiseCallback;
    private List<ScanFilter> scanFilters = new ArrayList<>();
    public String bleEncounterDate;
    public String bleEncounterTime;
    public String bleEncounterID;

    @Override
    public void onCreate() {
        super.onCreate();
        createCallback();
        scanFilters.add(scanFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bleAdvertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
        bleScanner.startScan(scanFilters,scanSettings, scanCallback);
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

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(cal.getTime());
        return currentDate;
    }

    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HH-mm");
        String currentTime = dateFormat.format(cal.getTime());
        return currentTime;
    }

    private void createCallback() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                Map<ParcelUuid, byte[]> raw = result.getScanRecord().getServiceData();

                Object[] uuidAGAIN;

                Set<ParcelUuid> set = raw.keySet();
                uuidAGAIN = set.toArray();

                UUID uuid = result.getScanRecord().getServiceUuids().get(0).getUuid();

                long bigBrain = getLongFromByteArray(raw.get(uuidAGAIN[0]));

                try {
                    //txtFile.writeToFile(String.valueOf(bigBrain));
                    bleEncounterID = String.valueOf(bigBrain);
                }
                catch (NullPointerException ex) {
                    txtFile.writeToFile(ex.toString());
                }

                bleEncounterDate = getCurrentDate();
                bleEncounterTime = getCurrentTime();
                boolean dbResult = recordEncountersData(bleEncounterID, bleEncounterDate, bleEncounterTime);

                String message = "";
                if (dbResult) {
                    message = "Successful db stuff";
                } else {
                    message = "Failed db stuff";
                }
                txtFile.writeToFile(message);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                txtFile.writeToFile("Error Code: " + Integer.toString(errorCode) + "\n" + getErrorDescription(errorCode));
            }
        };

        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                txtFile.writeToFile("Successfully started advertising");
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                txtFile.writeToFile("Failed to start advertising");
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
