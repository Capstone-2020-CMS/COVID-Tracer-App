package com.myBubble.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.util.Log;

import com.myBubble.database.DatabaseHelper;
import com.myBubble.database.PersonalData;

import java.util.ArrayList;
import java.util.List;

import static com.myBubble.MainActivity.myDB;
import static com.myBubble.utils.CodeManager.longToByteArray;

public class BLEManager {
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothLeScanner bluetoothLeScanner;
    private ScanSettings scanSettings;
    private ScanFilter scanFilter;

    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private AdvertiseSettings advertiseSettings;
    private AdvertiseData advertiseData;

    public BLEManager(Context context) {
        // General
        this.bluetoothManager = (BluetoothManager) context.getSystemService(context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = this.bluetoothManager.getAdapter();
        // Scanner
        this.bluetoothLeScanner = this.bluetoothAdapter.getBluetoothLeScanner();
        this.scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .build();
        // Scan filter
        int manufacturerID = 1313;
        byte[] manufacturerByte = new byte[8];
        for (byte b : manufacturerByte) {
            b = 0;
        }
        this.scanFilter = new ScanFilter.Builder()
                .setManufacturerData(manufacturerID, manufacturerByte, manufacturerByte)
                .build();
        // Advertiser
        this.bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        this.advertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setConnectable(false)
                .build();
        // Advertise data
        if (myDB == null) {
            myDB = new DatabaseHelper(context);
        }
        String strCode = myDB.getPersonalInfoData();
        if (strCode.equals("Data Not Found")) {
            PersonalData.addOnInstallData();
            strCode = myDB.getPersonalInfoData();
        }
        Log.i("COVID", "Personal Code from DB " + strCode);
        long code = Long.parseLong(strCode);
        byte[] byteCode = longToByteArray(code);
        this.advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .addManufacturerData(manufacturerID, byteCode)
                .build();
    }

    public void startScanning(ScanCallback scanCallback) {
        List<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(this.scanFilter);
        this.bluetoothLeScanner.startScan(scanFilters,scanSettings, scanCallback);
    }

    public void startAdvertising(AdvertiseCallback advertiseCallback) {
        this.bluetoothLeAdvertiser.startAdvertising(this.advertiseSettings, this.advertiseData, advertiseCallback);
    }
}
