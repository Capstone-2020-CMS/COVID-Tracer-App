package com.covid.gps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class TimeMarker implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;

    public TimeMarker(double lat, double lng, String title) {
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = null;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
