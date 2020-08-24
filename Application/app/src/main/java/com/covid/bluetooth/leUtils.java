package com.covid.bluetooth;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import com.covid.MainActivity;

public class leUtils {

    // Scancallback function that handles the results of the BLE scans
    // E.g. on scan result add the scan contents to a list
    // TODO Add results to database
    public static ScanCallback leScanCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            MainActivity.list.add(result.getDevice());
        }
    };

}
