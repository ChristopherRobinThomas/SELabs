package com.example.se_lab_23b02;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class MyLocationListener implements LocationListener {
    private Location lastLocationKnown;
    private double walkedDistance;

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(lastLocationKnown == null)
            lastLocationKnown = location;

        double distanceMeters = location.distanceTo(lastLocationKnown);
        walkedDistance += distanceMeters;
        System.out.println("update" + walkedDistance);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}


    public double getWalkedDistance() {
        return walkedDistance;
    }
}
