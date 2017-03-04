package com.kovtsun.apple.DBTables;


import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Markers implements Serializable {
    @DatabaseField(generatedId = true, columnName = "markers_id")
    public int markersId;

    @DatabaseField(columnName = "markers_title")
    public String markersTitle = "";

    @DatabaseField(columnName = "markers_lat")
    public String markersLat = "";

    @DatabaseField(columnName = "markers_lng")
    public String markersLng = "";

    @Override
    public String toString() {
        return "Markers{" + "id=" + markersId + ", title=" + markersTitle + ", lat=" + markersLat + ", lng"+ markersLng + '}';
    }

    public Markers() {}

    public String getMarkersLat() {
        return markersLat;
    }

    public void setMarkersLat(String markersLat) {
        this.markersLat = markersLat;
    }

    public String getMarkersLng() {
        return markersLng;
    }

    public void setMarkersLng(String markersLng) {
        this.markersLng = markersLng;
    }

    public String getMarkersTitle() {
        return markersTitle;
    }

    public void setMarkersTitle(String markersTitle) {
        this.markersTitle = markersTitle;
    }

    public Markers(String markersLat, String markersLng, String markersTitle) {
        this.markersLat = markersLat;
        this.markersLng = markersLng;
        this.markersTitle = markersTitle;
    }
}
