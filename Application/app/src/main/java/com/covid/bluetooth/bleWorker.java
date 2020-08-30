package com.covid.bluetooth;

import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.ListenableWorker;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;

import com.covid.R;
import com.covid.utils.txtFile;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.LogRecord;

import static com.covid.MainActivity.NOTIFICATION_CHANNEL;
import static com.covid.MainActivity.bleScanner;
import static com.covid.MainActivity.scanSettings;

public class bleWorker extends ListenableWorker {

    private static final long SCAN_PERIOD = 900000;
    private Handler handler;
    private boolean isScanning = false;

    public bleWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {

        Log.d("Run attempt count", String.valueOf(getRunAttemptCount()));

        return CallbackToFutureAdapter.getFuture(completer -> {
            handler = new Handler();

            ScanCallback callback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    txtFile.writeToFile(result.getDevice().getAddress());
                    completer.set(Result.success());
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    txtFile.writeToFile("Error Code: " + Integer.toString(errorCode) + "\n" + getErrorDescription(errorCode));
                    completer.set(Result.failure());
                }
            };

            setForegroundAsync(createForegroundInfo());

            startLeScan(callback);

            return callback;
        });
    }

    private void startLeScan(ScanCallback callback) {
        if(!isScanning) {
            // stops the scan after a predefined period
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    bleScanner.stopScan(callback);
                }
            }, SCAN_PERIOD);

            isScanning = true;
            bleScanner.startScan(null,scanSettings,callback);

        } else {
            isScanning = false;
            bleScanner.stopScan(callback);
        }
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

    private ForegroundInfo createForegroundInfo() {
        // Build a notification using bytesRead and contentLength

        Context context = getApplicationContext();

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss'Z'");

        Date date = Calendar.getInstance().getTime();

        String title = dateformat.format(date);
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent = WorkManager.getInstance(context)
                .createCancelPendingIntent(getId());

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setContentTitle(title)
                .setContentText("Hello World")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .build();

        return new ForegroundInfo(1, notification);
    }
}
