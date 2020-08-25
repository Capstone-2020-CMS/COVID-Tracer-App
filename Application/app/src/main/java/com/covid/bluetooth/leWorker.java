package com.covid.bluetooth;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

import static com.covid.MainActivity.leScanner;
import static com.covid.MainActivity.scanSettings;
import static com.covid.bluetooth.leUtils.leScanCallBack;

public class leWorker extends ListenableWorker {

    public leWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        // Temporary code to start a scan
        leScanner.startScan(null,scanSettings,leScanCallBack);

        return (ListenableFuture<Result>) Result.success();
    }

}
