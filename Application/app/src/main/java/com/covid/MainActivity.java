package com.covid;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.PeriodicWorkRequest;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import static com.covid.utils.utilNotification.createNotificationChannel;
import static com.covid.utils.utilNotification.displayNotification;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    public static final String NOTIFICATION_CHANNEL = "0";
    public static BluetoothAdapter bluetoothAdapter;
    public static BluetoothLeScanner leScanner;
    public static ScanSettings scanSettings;
    static PeriodicWorkRequest workRequest;
    public static String logPath;
    public static NotificationManagerCompat notificationManager;

    //TODO remove temp list
    public static ArrayList list = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Sets the bluetooth le scanner
        leScanner = bluetoothAdapter.getBluetoothLeScanner();

        // Setup le scan settings
        scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();

        // Periodic work request
//        workRequest = new PeriodicWorkRequest.Builder(leWorker.class, 90, TimeUnit.SECONDS).build();

        // Queues the task to be executed
//        WorkManager
//                .getInstance(getApplicationContext())
//                .enqueue(workRequest);

        // Example notification
        displayNotification(getApplicationContext(), "Title", "Hello World");
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