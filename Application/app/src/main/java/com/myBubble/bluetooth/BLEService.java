package com.myBubble.bluetooth;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import androidx.core.app.TaskStackBuilder;


import com.myBubble.MainActivity;
import com.myBubble.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.myBubble.MainActivity.mFusedLocationProviderClient;
import static com.myBubble.MainActivity.myDB;
import static com.myBubble.database.EncountersData.recordEncountersData;
import static com.myBubble.utils.CodeManager.getLongFromByteArray;

public class BLEService extends Service {
    private ScanCallback scanCallback;
    private AdvertiseCallback advertiseCallback;
    private LocationCallback locationCallback;
    private BLEManager bleManager;
    private String NOTIFICATION_CHANNEL = "0";
    public String bleEncounterDate;
    public String bleEncounterTime;
    public String bleEncounterID;
    private String logTag = "COVID";




    @Override
    public void onCreate() {
        super.onCreate();
        createCallback();
        bleManager = new BLEManager(getApplicationContext());
        recordGPSCoordinates();
    }

    @SuppressLint("MissingPermission")
    private void recordGPSCoordinates() {
        if (mFusedLocationProviderClient == null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(BLEService.this);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);

        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bleManager.startAdvertising(advertiseCallback);
        bleManager.startScanning(scanCallback);
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

        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(goToMainActivityIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setContentTitle("Contact Scanning")
                .setContentText("myBubble is scanning for close contacts using Bluetooth low energy.")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_icon_small_01)
                .setContentIntent(pendingIntent)
                .build();

        return notification;
    }

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(cal.getTime());
    }

    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(cal.getTime());
    }

    private void createCallback() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                byte[] byteData = result.getScanRecord().getManufacturerSpecificData(1313);

                if (byteData != null) {
                    long bigBrain = getLongFromByteArray(byteData);

                    try {
                        bleEncounterID = String.valueOf(bigBrain);
                    }
                    catch (NullPointerException ex) {
                        Log.e(logTag, ex.toString());
                    }

                    Log.i(logTag, bleEncounterID);

                    bleEncounterDate = getCurrentDate();
                    bleEncounterTime = getCurrentTime();
                    boolean dbResult = recordEncountersData(bleEncounterID, bleEncounterDate, bleEncounterTime);

                    String message = "";
                    if (dbResult) {
                        message = "Successful db stuff";
                    } else {
                        message = "Failed db stuff";
                    }
                    Log.i(logTag, message);
                } else {
                    Log.e(logTag, "No service data was found");
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.e(logTag, getScanErrorDescription(errorCode));
            }
        };

        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.i(logTag, "Successfully started advertising");
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                Log.e(logTag, getAdvertiseErrorDescription(errorCode));
            }
        };

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult.getLastLocation() != null) {
                    Location mLastKnownLocation = locationResult.getLastLocation();
                    myDB.insertGPSData(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), getCurrentDate(), getCurrentTime());
                    Log.i(logTag, String.format("%s,%s", String.valueOf(mLastKnownLocation.getLatitude()), mLastKnownLocation.getLongitude()));
                }
            }
        };
    }

    private String getScanErrorDescription(int errorCode) {
        switch (errorCode) {
            case 1: return "Failed to start scan as BLE scan with the same settings is already started by the app.";
            case 2: return "Failed to start scan as app cannot be registered.";
            case 3: return "Failed to start power optimized scan as this feature is not supported.";
            case 4: return "Failed to start scan due an internal error.";
            default: return "Unknown error code.";
        }
    }

    private String getAdvertiseErrorDescription(int errorCode) {
        switch (errorCode) {
            case 1: return "Failed to start advertising as the advertise data to be broadcast is larger than 31 bytes.";
            case 2: return "Failed to start advertising because no advertising instance is available.";
            case 3: return "Failed to start advertising as the advertising is already started.";
            case 4: return "Operation failed due to an internal error.";
            case 5: return "This feature is not supported on this platform.";
            default: return "Unknown error code.";
        }
    }
}
