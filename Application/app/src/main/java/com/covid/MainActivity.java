package com.covid;

import android.content.SharedPreferences;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;

import android.preference.PreferenceManager;
import android.util.Log;

import com.covid.database.DatabaseHelper;
import com.covid.database.EncountersData;
import com.covid.database.PersonalData;
import com.covid.utils.CodeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.covid.bluetooth.BLEService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.UUID;

import static com.covid.utils.CodeManager.longToByteArray;
import static com.covid.utils.CodeManager.generateCode;
import static com.covid.utils.CodeManager.getLongFromByteArray;
import static com.covid.utils.utilNotification.createNotificationChannel;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    public static final String NOTIFICATION_CHANNEL = "0";
    public static BluetoothManager bluetoothManager;
    public static BluetoothAdapter bluetoothAdapter;

    // Scanner
    public static BluetoothLeScanner bleScanner;
    public static ScanSettings scanSettings;
    public static ScanFilter scanFilter;

    // Advertiser
    public static BluetoothLeAdvertiser bleAdvertiser;
    public static AdvertiseSettings advertiseSettings;
    public static AdvertiseData advertiseData;
    public static UUID serviceUUID;

    public static String logPath;
    public static NotificationManagerCompat notificationManager;
    public static DatabaseHelper encounterDB;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        encounterDB = new DatabaseHelper(this);

        // Set the path to the logs folder
        logPath = String.valueOf(getExternalFilesDir("Logs"));

        // Notification setup
        createNotificationChannel(getApplicationContext());
        notificationManager = NotificationManagerCompat.from(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Check for permissions for android users of sdk 23 or higher
        checkPermissions();

        // Initialises Bluetooth adapter
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Sets the bluetooth le scanner
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();

        // Setup le scan settings
        scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .build();

        // Setup le scan filter
        serviceUUID = new UUID(1313,1313);

        scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(serviceUUID))
                .build();

        // Sets the bluetooth le advertiser
        bleAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();

        // Setup le advertiser settings
        advertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                // TODO check whether this should be true or not
                .setConnectable(false)
                .build();

        long code = generateCode();

        byte[] byteCode = longToByteArray(code);

        long backFromTheDead = getLongFromByteArray(byteCode);

        // Set advertise data
        advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(serviceUUID))
                .addServiceData(new ParcelUuid(serviceUUID), byteCode)
                .build();

        // Start the bluetooth le service on a new thread
        Thread bleThread = new Thread() {
            @Override
            public void run() {
                // Do something martin
                Intent intent = new Intent(getApplicationContext(), BLEService.class);
                startService(intent);
            }
        };

        bleThread.start();


        //Access and modify preference data
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // Runs initial one time on install code here
            // Adding personal user information to database on installation
            PersonalData.addOnInstallData();
            // marks the first time the code has run.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
    }

    // Checks necessary permissions have been enabled
    private void checkPermissions() {
        // Fine Location
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        // Bluetooth
        if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH},1);
        }
        //Bluetooth Admin
        if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_ADMIN") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN},1);
        }
        //External Storage
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

}