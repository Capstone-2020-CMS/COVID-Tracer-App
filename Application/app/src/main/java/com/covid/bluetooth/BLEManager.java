package com.covid.bluetooth;

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
import android.content.Intent;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static com.covid.utils.CodeManager.generateCode;
import static com.covid.utils.CodeManager.longToByteArray;

public class BLEManager {
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothLeScanner bluetoothLeScanner;
    private ScanSettings scanSettings;
    private UUID serviceUUID;
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
        this.serviceUUID = new UUID(1313,1313);
        this.scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(serviceUUID))
                .build();
        // Advertiser
        this.bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        this.advertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setConnectable(false)
                .build();
        //TODO replace with cuba's get code method
        long code = generateCode();
        byte[] byteCode = longToByteArray(code);
        this.advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(serviceUUID))
                .addServiceData(new ParcelUuid(serviceUUID), byteCode)
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
