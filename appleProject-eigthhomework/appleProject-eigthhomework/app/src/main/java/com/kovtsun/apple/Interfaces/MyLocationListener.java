package com.kovtsun.apple.Interfaces;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

    private double lat = 0.0;

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    private double lng = 0.0;

    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
