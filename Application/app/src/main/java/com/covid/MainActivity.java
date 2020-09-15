package com.covid;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
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
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.ParcelUuid;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.covid.bluetooth.BLEReceiver;
import com.covid.database.DatabaseHelper;
import com.covid.database.PersonalData;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.covid.bluetooth.BLEService;

import java.util.ArrayList;

import static com.covid.utils.utilNotification.createNotificationChannel;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_ENABLE_BT = 1;
    public static final String NOTIFICATION_CHANNEL = "0";

    public static String logPath;
    public static NotificationManagerCompat notificationManager;
    public static DatabaseHelper myDB;
    public static int bubbleSize = 0;
    public static BluetoothAdapter adapter;
    public static LocationManager locationManager;
    public static String providerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHelper(this);

        // Set the path to the logs folder
        logPath = String.valueOf(getExternalFilesDir("Logs"));

        // Notification setup
        createNotificationChannel(getApplicationContext());
        notificationManager = NotificationManagerCompat.from(this);

        // Template code from the start of the project
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        // Get the bluetooth adapter
        adapter = BluetoothAdapter.getDefaultAdapter();

        // Get the location manager
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        providerName = locationManager.getBestProvider(new Criteria(), true);

        // Check for permissions for android users of sdk 23 or higher
        checkPermissions();
    }

    private void checkBluetoothService() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            startBluetoothService();
        }
    }

    private void start() {
        firstTimeSetup();
        checkBluetoothService();

        bubbleSize = myDB.getNumOfEncounters();
    }

    // Checks necessary permissions have been enabled
    private void checkPermissions() {
        ArrayList<String> arrayList = new ArrayList<>();
        String[] permissions;

        // Fine Location
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            arrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        // External Storage
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            arrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (arrayList.size() != 0) {
            permissions = new String[arrayList.size()];

            for (int i = 0; i < arrayList.size(); i++) {
                permissions[i] = arrayList.get(i);
            }

            ActivityCompat.requestPermissions(this, permissions,1);
        } else {
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == -1) {
                Toast.makeText(MainActivity.this, "Please enable permissions or the application won't work as intended", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Bluetooth
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                startBluetoothService();
            } else {
                Toast.makeText(MainActivity.this, "Please enable bluetooth so the app works as intended", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firstTimeSetup() {
        // Access and modify preference data
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

    private void startBluetoothService() {
        // Start the bluetooth le service on a new thread
        Thread bleThread = new Thread() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), BLEService.class);
                startService(intent);
            }
        };

        bleThread.start();
    }
}