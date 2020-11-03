package com.myBubble.gps;

import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class DHBZones implements Comparable<DHBZones>{
    private String name;
    private String type;
    private ArrayList<PolygonOptions> polygons;

    public DHBZones(String name, String type, ArrayList<PolygonOptions> polygons) {
        this.name = name;
        this.type = type;
        this.polygons = polygons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<PolygonOptions> getPolygons() {
        return polygons;
    }

    public void setPolygons(ArrayList<PolygonOptions> polygons) {
        this.polygons = polygons;
    }

    @Override
    public int compareTo(DHBZones o) {
        return this.name.compareTo(o.name);
    }
}
