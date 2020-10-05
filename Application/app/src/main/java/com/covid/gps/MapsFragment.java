package com.covid.gps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.covid.MainActivity;
import com.covid.R;
import com.covid.utils.TableData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;

import static com.covid.MainActivity.mFusedLocationProviderClient;
import static com.covid.MainActivity.myDB;

public class MapsFragment extends Fragment {

    private GoogleMap nMap;
    private static final int DEFAULT_ZOOM = 15;

    private TextView txtDay;
    private Button btnPrevDay;
    private Button btnNextDay;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            nMap = googleMap;

            if (ContextCompat.checkSelfPermission(getContext(), "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                getLocation();
            }

            // Draw the timeline for today
            ArrayList<GPSRecord> arrayList = myDB.getGPSDataForDay(getCurrentDate());
            drawTimeline(arrayList);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        txtDay = view.findViewById(R.id.txtDay);
        btnPrevDay = view.findViewById(R.id.btnPrevDay);
        btnNextDay = view.findViewById(R.id.btnNextDay);

        // Set the title to the current day
        txtDay.setText(formatDateForTitle(getCurrentDate()));

        txtDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDay.setText(formatDateForTitle(getCurrentDate()));
                String dateForDB = formatDateForDB((String) txtDay.getText());
                ArrayList<GPSRecord> arrayList = myDB.getGPSDataForDay(dateForDB);
                drawTimeline(arrayList);
            }
        });

        btnPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDay.setText(getDate((String) txtDay.getText(), -1));
                String dateForDB = formatDateForDB((String) txtDay.getText());
                ArrayList<GPSRecord> arrayList = myDB.getGPSDataForDay(dateForDB);
                drawTimeline(arrayList);
            }
        });

        btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDay.setText(getDate((String) txtDay.getText(), 1));
                String dateForDB = formatDateForDB((String) txtDay.getText());
                ArrayList<GPSRecord> arrayList = myDB.getGPSDataForDay(dateForDB);
                drawTimeline(arrayList);
            }
        });
    }

    private void getLocation() {
        @SuppressLint("MissingPermission") Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location mLastKnownLocation = task.getResult();
                    if (mLastKnownLocation != null) {
                        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    }
                }
            }
        });
    }

    // Draws line and markers for specified list
    private void drawTimeline(ArrayList<GPSRecord> arrayList) {
        nMap.clear();

        PolylineOptions options = new PolylineOptions().clickable(true);

        Location previousLocation = null;

        for (int i=0; i<arrayList.size(); i++) {
            GPSRecord record = arrayList.get(i);
            options.add(new LatLng(record.getLatitude(), record.getLongitude()));

            Location location = new Location("COVID_Tracer_App");
            location.setLatitude(record.getLatitude());
            location.setLongitude(record.getLongitude());

            boolean addMarker = false;

            if (previousLocation == null) {
                addMarker = true;
            } else if (location.distanceTo(previousLocation) > 500) {
                addMarker = true;
            }

            if (addMarker) {
                nMap.addMarker(new MarkerOptions().position(new LatLng(record.getLatitude(), record.getLongitude())).title(record.getTime()));
            }

            previousLocation = location;
        }

        Polyline polyline = nMap.addPolyline(options);
    }

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(cal.getTime());
    }

    private String getDate(String input, int difference) {
        DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");

        Date date = null;
        try {
            date = dateFormat.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, difference);

        Date previousDate = calendar.getTime();
        return dateFormat.format(previousDate);
    }

    private String formatDateForTitle(String input) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("d MMM yyyy").format(date);
    }

    private String formatDateForDB(String input) {
        Date date = null;
        try {
            date = new SimpleDateFormat("d MMM yyyy").parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(cal.getTime());
    }
}