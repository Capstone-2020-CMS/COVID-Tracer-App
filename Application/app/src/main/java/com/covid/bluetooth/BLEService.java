package com.covid.bluetooth;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.covid.R;
import com.covid.database.DatabaseHelper;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.covid.MainActivity.mFusedLocationProviderClient;
import static com.covid.MainActivity.myDB;
import static com.covid.database.EncountersData.recordEncountersData;
import static com.covid.utils.CodeManager.getLongFromByteArray;

public class BLEService extends Service {
    private ScanCallback scanCallback;
    private AdvertiseCallback advertiseCallback;
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

    private void recordGPSCoordinates() {
        Handler handler = new Handler(Looper.getMainLooper());

        Thread thread = new Thread() {
            @Override
            public void run() {
                int delay = 10000;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mFusedLocationProviderClient == null) {
                            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(BLEService.this);
                        }

                        @SuppressLint("MissingPermission") Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();

                        locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful()) {
                                    Location mLastKnownLocation = task.getResult();
                                    if (mLastKnownLocation != null) {
                                        myDB.insertGPSData(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), getCurrentDate(), getCurrentTime());
                                        Log.i(logTag, String.format("%s,%s", String.valueOf(mLastKnownLocation.getLatitude()), mLastKnownLocation.getLongitude()));
                                    }
                                }
                            }
                        });

                        handler.postDelayed(this, delay);
                    }
                }, delay);
            }
        };

        thread.start();
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
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String currentTime = dateFormat.format(cal.getTime());
        return currentTime;
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
