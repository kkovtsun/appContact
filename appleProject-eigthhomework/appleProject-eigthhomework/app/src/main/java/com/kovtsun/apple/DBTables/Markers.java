package com.kovtsun.apple.DBTables;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Markers implements Serializable {
    @DatabaseField(generatedId = true, columnName = "markers_id")
    public int markersId;

    @DatabaseField(columnName = "markers_title")
    public String markersTitle = "";

    @DatabaseField(columnName = "markers_lat")
    public double markersLat;

    @DatabaseField(columnName = "markers_lng")
    public double markersLng;

    @Override
    public String toString() {
        return "Markers{" + "id=" + markersId + ", title=" + markersTitle + ", lat=" + markersLat + ", lng"+ markersLng + '}';
    }

    public Markers() {}

    public int getMarkersId() {
        return markersId;
    }

    public void setMarkersId(int markersId) {
        this.markersId = markersId;
    }

    public double getMarkersLat() {
        return markersLat;
    }

    public void setMarkersLat(double markersLat) {
        this.markersLat = markersLat;
    }

    public double getMarkersLng() {
        return markersLng;
    }

    public void setMarkersLng(double markersLng) {
        this.markersLng = markersLng;
    }

    public String getMarkersTitle() {
        return markersTitle;
    }

    public void setMarkersTitle(String markersTitle) {
        this.markersTitle = markersTitle;
    }

    public Markers(double markersLat, double markersLng, String markersTitle) {
        this.markersLat = markersLat;
        this.markersLng = markersLng;
        this.markersTitle = markersTitle;
    }


}
