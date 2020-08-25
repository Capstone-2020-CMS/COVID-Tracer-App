package com.covid.bluetooth;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.covid.utils.txtFile;
import com.google.common.util.concurrent.ListenableFuture;

import static com.covid.MainActivity.leScanner;
import static com.covid.MainActivity.scanSettings;

public class leWorker extends ListenableWorker {

    public leWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {

        return CallbackToFutureAdapter.getFuture(completer -> {
            ScanCallback callback = new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    // TODO remove the temp list usage
                    //MainActivity.list.add(result.getDevice());
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

            leScanner.startScan(null,scanSettings,callback);
            return callback;
        });
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
