package com.covid.gps;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;

import com.covid.R;
import com.covid.utils.ZoneCovidData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlMultiGeometry;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.covid.MainActivity.getZoneDataWorkRequest;
import static com.covid.MainActivity.mFusedLocationProviderClient;
import static com.covid.MainActivity.myDB;
import static com.covid.MainActivity.workManager;
import static com.covid.MainActivity.zoneCovidDataArray;

public class MapsFragment extends Fragment {

    private GoogleMap nMap;
    private static final int DEFAULT_ZOOM = 15;

    private TextView txtDay;
    private Button btnPrevDay;
    private Button btnNextDay;

    private ArrayList<DHBZones> dhbZonesArrayList = new ArrayList<DHBZones>();

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
            oneOffKmlStuff();
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

        // disable the next day button
        btnNextDay.setEnabled(false);

        // Date limitation variables
        String curDate = formatDateForTitle(getCurrentDate());
        String minDate = getDate(curDate, -21);

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
                if (!txtDay.getText().equals(minDate)) {
                    String prevDateString = getDate((String) txtDay.getText(), -1);
                    txtDay.setText(prevDateString);
                    String dateForDB = formatDateForDB(prevDateString);
                    ArrayList<GPSRecord> arrayList = myDB.getGPSDataForDay(dateForDB);

                    // disable the button if it matches the min date
                    if (prevDateString.equals(minDate)) {
                        btnPrevDay.setEnabled(false);
                        btnPrevDay.setBackgroundColor(getResources().getColor(R.color.red));
                    }
                    // re-enable the next day button if it is no longer
                    if (prevDateString.equals(getDate(curDate, -1))) {
                        btnNextDay.setEnabled(true);
                        btnNextDay.setBackgroundColor(getResources().getColor(R.color.motorwayGreen));
                    }

                    drawTimeline(arrayList);
                } else {
                    Toast.makeText(requireContext(), "We do not store data past 21 days", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtDay.getText().equals(curDate)) {
                    String nextDateString = getDate((String) txtDay.getText(), 1);
                    txtDay.setText(nextDateString);
                    String dateForDB = formatDateForDB(nextDateString);
                    ArrayList<GPSRecord> arrayList = myDB.getGPSDataForDay(dateForDB);

                    // disable the button if it matches the current date
                    if (nextDateString.equals(curDate)) {
                        btnNextDay.setEnabled(false);
                        btnNextDay.setBackgroundColor(getResources().getColor(R.color.red));
                    }
                    // re-enable the next day button if it is no longer
                    if (nextDateString.equals(getDate(minDate, 1))) {
                        btnPrevDay.setEnabled(true);
                        btnPrevDay.setBackgroundColor(getResources().getColor(R.color.motorwayGreen));
                    }

                    drawTimeline(arrayList);
                } else {
                    Toast.makeText(requireContext(), "You are at the current date", Toast.LENGTH_SHORT).show();
                }
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

    private void oneOffKmlStuff() {
        KmlLayer layer = null;

        try {
            layer = new KmlLayer(nMap, R.raw.layer, requireContext());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (layer != null) {
            layer.addLayerToMap();
        }

        for (KmlContainer container : layer.getContainers()) {
            container.hasContainers();
            for (KmlPlacemark placemark : container.getPlacemarks()) {
                placemark.getPolygonOptions();
                String type = placemark.getGeometry().getGeometryType();
                String dhbName = placemark.getProperty("name");
                if (type.equals("Polygon")) {
                    KmlPolygon polygon = (KmlPolygon) placemark.getGeometry();
                    List<LatLng> list = polygon.getOuterBoundaryCoordinates();
                    ArrayList<PolygonOptions> polygonOptionsArrayList = new ArrayList<PolygonOptions>();
                    polygonOptionsArrayList.add(new PolygonOptions().addAll(list));
                    dhbZonesArrayList.add(new DHBZones(dhbName, type, polygonOptionsArrayList));
                } else {
                    KmlMultiGeometry multiGeometry = (KmlMultiGeometry) placemark.getGeometry();
                    ArrayList<PolygonOptions> polygonOptionsArrayList = new ArrayList<PolygonOptions>();

                    for (Geometry geometry : multiGeometry.getGeometryObject()) {
                        KmlPolygon polygon = (KmlPolygon) geometry;
                        polygonOptionsArrayList.add(new PolygonOptions().addAll(polygon.getOuterBoundaryCoordinates()));
                    }

                    dhbZonesArrayList.add(new DHBZones(dhbName, type, polygonOptionsArrayList));
                }
            }
        }

        Collections.sort(dhbZonesArrayList);

        layer.removeLayerFromMap();
    }

    private void getZoneData() {
        final Observer<WorkInfo> workInfoObserver = new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    Toast.makeText(requireContext(), "Successfully retrieved zone data.", Toast.LENGTH_SHORT).show();
                    drawZones();
                    Toast.makeText(requireContext(), "Tap on coloured zones to get more information about them", Toast.LENGTH_SHORT).show();
                } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                    Toast.makeText(requireContext(), "Failed to retrieve zone data, please check your internet and restart the app.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        workManager.pruneWork();
        workManager.enqueue(getZoneDataWorkRequest);
        LiveData<WorkInfo> status = workManager.getWorkInfoByIdLiveData(getZoneDataWorkRequest.getId());
        status.observe(getViewLifecycleOwner(), workInfoObserver);
    }

    private void drawZones() {
        int lowRate = Color.argb(100,51,204,51);
        int lowMediumRate = Color.argb(100,255,255,0);;
        int highMediumRate = Color.argb(100,255,165,0);
        int highRate = Color.argb(100,255,51,0);

        int currentActive = Integer.parseInt(zoneCovidDataArray.get(21).getActive());

        int i = 0;
        for (DHBZones zone : dhbZonesArrayList) {
            double percentage = (Double.parseDouble(zoneCovidDataArray.get(i).getActive()) / currentActive) * 100;
            int color;

            if (percentage >= 50) {
                color = highRate;
            } else if (percentage >= 25) {
                color = highMediumRate;
            } else if (percentage >= 12.5) {
                color = lowMediumRate;
            } else {
                color = lowRate;
            }

            for (PolygonOptions options : zone.getPolygons()) {
                Polygon polyn = nMap.addPolygon(options);
                polyn.setFillColor(color);
                polyn.setClickable(true);
                polyn.setTag(zoneCovidDataArray.get(i));
            }
            i++;
        }

        nMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                ZoneCovidData data = (ZoneCovidData) polygon.getTag();
                //Toast.makeText(requireContext(), data.getDHB(), Toast.LENGTH_SHORT).show();

                View view = getView();
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View popupView = inflater.inflate(R.layout.popup_zone_data_window, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                TextView txtTitle = popupView.findViewById(R.id.txtDHBName);
                TextView txtActive = popupView.findViewById(R.id.txtZoneActive);
                TextView txtTotal = popupView.findViewById(R.id.txtZoneTotal);
                TextView txtChange24hr = popupView.findViewById(R.id.txtZoneChange24hr);

                txtTitle.setText(data.getDHB());
                txtActive.setText(data.getActive());
                txtTotal.setText(data.getTotal());
                txtChange24hr.setText(data.getChange24hr());

                popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
            }
        });
    }

    // Draws line and markers for specified list
    private void drawTimeline(ArrayList<GPSRecord> arrayList) {

        nMap.clear();

        if (zoneCovidDataArray.size() > 0) {
            drawZones();
        } else {
            getZoneData();
        }

        PolylineOptions options = new PolylineOptions().clickable(true);
        ClusterManager<TimeMarker> clusterManager = new ClusterManager<TimeMarker>(requireContext(), nMap);
        nMap.setOnCameraIdleListener(clusterManager);

        Location previousLocation = null;

        for (int i=0; i<arrayList.size(); i++) {
            // Get GPS data from array
            GPSRecord record = arrayList.get(i);

            // Create location object for distance calculations
            Location location = new Location("COVID_Tracer_App");
            location.setLatitude(record.getLatitude());
            location.setLongitude(record.getLongitude());

            // Initialise booleans for adding a marker and polyline point as false
            boolean addMarker = false;
            boolean addPolyLinePoint = true;

            // If it is the first location set booleans to true
            if (previousLocation == null) {
                addMarker = true;
                addPolyLinePoint = true;
            }
            // If the distance to the previous location is greater than 500 metres add a marker
            else if (location.distanceTo(previousLocation) > 500) {
                addMarker = true;
            }
            // If the distance to the previous location is greater than 100 metres add a point for the line
//            else if (location.distanceTo(previousLocation) > 100) {
//                addPolyLinePoint = true;
//            }

            // If add marker boolean is true add a marker to the app
            if (addMarker) {
                //nMap.addMarker(new MarkerOptions().position(new LatLng(record.getLatitude(), record.getLongitude())).title(record.getTime()));
                TimeMarker marker = new TimeMarker(record.getLatitude(),record.getLongitude(), record.getTime());
                clusterManager.addItem(marker);
            }
            // If add polyline point boolean is true add point to polyline options
            if (addPolyLinePoint) {
                options.add(new LatLng(record.getLatitude(), record.getLongitude()));
            }

            // Set previous location to current location
            previousLocation = location;
        }
        // Clusters the items added to the cluster manager
        clusterManager.cluster();
        // Add the polyline to map
        options.color(R.color.primaryDarkColor);
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