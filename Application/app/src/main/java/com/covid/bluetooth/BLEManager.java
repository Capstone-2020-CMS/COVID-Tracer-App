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
    private ParcelUuid serviceUUID;
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

        this.serviceUUID = new ParcelUuid(UUID.fromString("00000000-0000-0521-0000-000000000521"));
        int manufacturerID = 1313;
        byte[] manufacturerByte = new byte[8];
        for (byte b : manufacturerByte) {
            b = 0;
        }
        this.scanFilter = new ScanFilter.Builder()
                // TODO return back to normal if it doesn't work
                //.setServiceUuid(serviceUUID)
                //.setServiceData(serviceUUID, null)
                .setManufacturerData(manufacturerID, manufacturerByte, manufacturerByte)
                .build();
        // Advertiser
        this.bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        this.advertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setConnectable(false)
                .build();
        // TODO replace with cuba's get code method
        long code = generateCode();
        byte[] byteCode = longToByteArray(code);
        this.advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                // TODO if filter does not work look at this
                .addServiceUuid(serviceUUID)
                .addManufacturerData(manufacturerID, byteCode)
                .build();
    }

    public void startScanning(ScanCallback scanCallback) {
        List<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(this.scanFilter);
        this.bluetoothLeScanner.startScan(scanFilters,scanSettings, scanCallback);
        //this.bluetoothLeScanner.startScan(null,scanSettings, scanCallback);
    }

    public void startAdvertising(AdvertiseCallback advertiseCallback) {
        this.bluetoothLeAdvertiser.startAdvertising(this.advertiseSettings, this.advertiseData, advertiseCallback);
    }
}
