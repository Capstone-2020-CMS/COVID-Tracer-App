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
            };

            leScanner.startScan(null,scanSettings,callback);
            return callback;
        });
    }

}
