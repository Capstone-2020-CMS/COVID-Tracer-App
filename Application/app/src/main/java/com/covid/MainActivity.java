package com.covid;

import android.app.NotificationManager;
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
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.JsonRequest;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.covid.bluetooth.BLEService;

import java.util.ArrayList;
import java.util.UUID;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static com.covid.ui.home.HomeFragment.toggleCardColour;
import static com.covid.utils.CodeManager.longToByteArray;
import static com.covid.utils.CodeManager.generateCode;
import static com.covid.utils.CodeManager.getLongFromByteArray;
import static com.covid.utils.utilNotification.createNotificationChannel;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    public static final String NOTIFICATION_CHANNEL = "0";

    public static String logPath;
    public static NotificationManagerCompat notificationManager;
    public static DatabaseHelper myDB;
    public static int bubbleSize = 0;
    public static String myID;

    public static boolean activeExpo;

    private String responseJSON;





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

        // Check for permissions for android users of sdk 23 or higher
        checkPermissions();



    }

    private void checkBluetoothService() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void start() {
        firstTimeSetup();
        checkBluetoothService();

        bubbleSize = myDB.getNumOfEncounters();
        myID =  myDB.getPersonalInfoData();


//--------------------------------------------------------------------------------------------------

        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());

        String requestURL = "https://s6bimnllqb.execute-api.ap-southeast-2.amazonaws.com/prod/data";


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseJSON = response;
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseJSON = "ERROR";

            }
        };


        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL, responseListener, errorListener);

        requestQueue.add(stringRequest);

//--------------------------------------------------------------------------------------------------

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
                // Start the bluetooth le service on a new thread
                Thread bleThread = new Thread() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), BLEService.class);
                        startService(intent);
                    }
                };

                bleThread.start();

                toggleCardColour();
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



/*    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }*/


/*    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, 1)
            .setSmallIcon(R.drawable.iconbubble)
            .setContentTitle("Hello")
            .setContentText("You're a winner")
            .setPriority(1);*/


}