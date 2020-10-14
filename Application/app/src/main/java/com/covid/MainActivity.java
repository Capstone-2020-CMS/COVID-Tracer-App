package com.covid;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.covid.bluetooth.BLEService;
import com.covid.database.CloudInfectedUsers;
import com.covid.database.DatabaseHelper;
import com.covid.database.PersonalData;

import com.covid.database.cloud.VolleyGET;

import com.covid.utils.DBUpdateWorker;
import com.covid.utils.GetDataWorker;
import com.covid.utils.TableData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;


import static com.covid.utils.NoteManager.CHANNEL_1_ID;
import static com.covid.utils.NoteManager.CHANNEL_2_ID;
import static com.covid.utils.NoteManager.createNotificationChannels;
import static com.covid.utils.utilNotification.createNotificationChannel;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ENABLE_BT = 1;
    public static final String NOTIFICATION_CHANNEL = "0";

    // Class vars
    public static String logPath;
    public static NotificationManagerCompat notificationManager;
    public static DatabaseHelper myDB;
    public static int bubbleSize = 0;

    public static BluetoothAdapter adapter;
    public static LocationManager locationManager;
    public static String providerName = "gps";
    public static WifiManager wifiManager;
    public static FusedLocationProviderClient mFusedLocationProviderClient;
    public static boolean mainSetupDone = false;
    public static WorkRequest getDataWorkRequest;
    public static WorkManager workManager;

    public static ArrayList<TableData> tableDataArrayList = new ArrayList<>();
    public static String dateUpdated;

    public static String myID;

    public static boolean activeExpo;
    public static boolean hasExpo;

    private String responseJSON;

    // Notification Manager (use compat as it supports backwards compatibility with earlier notifications)
    private static NotificationManagerCompat noteManagerCompat;





    private boolean readyToStart = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (myDB == null) {
            myDB = new DatabaseHelper(this);
        }



        //TODO - schedule updateEncounterStatus method
        //currently begins before BLE advertising so nothing is updated
        CloudInfectedUsers.updateEncounterStatus();

        // Set the path to the logs folder
        logPath = String.valueOf(getExternalFilesDir("Logs"));

        // Notification setup
        createNotificationChannel(getApplicationContext());
        notificationManager = NotificationManagerCompat.from(this);


        // Note Manager
        createNotificationChannels(getApplicationContext());
        noteManagerCompat = NotificationManagerCompat.from(this);

        // Template code from the start of the project
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_gps, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        // Get the bluetooth adapter
        adapter = BluetoothAdapter.getDefaultAdapter();

        // Get the location manager
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        //List<String> allProviders = locationManager.getAllProviders();
        //providerName = locationManager.getBestProvider(new Criteria(), true);

        // Get the WIFI manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        // Get the fused location provider client
        if (mFusedLocationProviderClient == null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }

        // Check for permissions for android users of sdk 23 or higher
        checkPermissions();

        mainSetupDone = true;

        // Retrieve data from ministry of health
        getDataWorkRequest = new OneTimeWorkRequest.Builder(GetDataWorker.class).build();
        workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueue(getDataWorkRequest);

        // Schedule updates to the local DB from Cloud
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

/*        PeriodicWorkRequest updateDBRequest =
                new PeriodicWorkRequest.Builder(DBUpdateWorker.class, 1, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();*/

        PeriodicWorkRequest updateDBRequest =
                new PeriodicWorkRequest.Builder(DBUpdateWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();

        // Enqueues a unique periodic request to prevent duplicate requests each time onCreate() runs
        workManager.enqueueUniquePeriodicWork("UpdateDB", ExistingPeriodicWorkPolicy.KEEP, updateDBRequest);





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

        // TODO evaluate position of this call
        myDB.deleteAgedGPSData();

        myID =  myDB.getPersonalInfoData();
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
        // WIFI
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_WIFI_STATE") != PackageManager.PERMISSION_GRANTED) {
            arrayList.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        // Internet
        if (ContextCompat.checkSelfPermission(this, "android.permission.INTERNET") != PackageManager.PERMISSION_GRANTED) {
            arrayList.add(Manifest.permission.INTERNET);
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


        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //VolleyGET.getInfectedUsers(getApplicationContext());

        VolleyGET.checkExposure(getApplicationContext());




        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

            activeExpo = false;

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

    //----------------------------------------------------------------------------------------------
    // Notifications in MAIN ACTIVITY
    //----------------------------------------------------------------------------------------------
    public void sendHighPriorityNote(String contents){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                0
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                //.setSmallIcon(R.drawable.ic_emptybubble)
                .setSmallIcon(R.drawable.ic_icon_small_01)
                .setContentTitle("ALART!")
                .setContentText("Big Alart Please CODE: Expono")
                // Set priority is used for API lower than 26/Oreo works like Channel system
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set category to be used to control behaviour https://developer.android.com/reference/android/app/Notification.html
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                // Set the intent to be called when the notification is tapped
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        // TODO figure out how not to conflict with main scanning notification
        //noteManagerCompat.notify(1, notification);
    }
    public void sendLowPriorityNote(String contents){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                0
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_emptybubble)
                .setContentTitle("alart?!")
                .setContentText("Updated Database?")
                // Set priority is used for API lower than 26/Oreo works like Channel system
                .setPriority(NotificationCompat.PRIORITY_LOW)
                // Set category to be used to control behaviour https://developer.android.com/reference/android/app/Notification.html
                //.setCategory(NotificationCompat.CATEGORY_MESSAGE)
                // Set the intent to be called when the notification is tapped
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        // TODO figure out how not to conflict with main scanning notification
        //noteManagerCompat.notify(2, notification);
   }

    public static void sendHighPriorityNoteAlpha(String contents, Context context){

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                0
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_emptybubble)
                .setContentTitle("ALART!")
                .setContentText("Big Alart Please CODE: Expono")
                // Set priority is used for API lower than 26/Oreo works like Channel system
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set category to be used to control behaviour https://developer.android.com/reference/android/app/Notification.html
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                // Set the intent to be called when the notification is tapped
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        // TODO figure out how not to conflict with main scanning notification
        //noteManagerCompat.notify(1, notification);
    }

   public static void setHasExpo(boolean value){
        hasExpo = value;
   }
}