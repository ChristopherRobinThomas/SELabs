package com.example.se_lab_23b02;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.lang.Math;

public class MyLocationListener implements LocationListener {
    private Location lastLocationKnown;
    private long timestamp_start;
    private double walkedDistance;

    private double heightGained = 0;

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (lastLocationKnown == null)
            lastLocationKnown = location;

        if (Objects.isNull(timestamp_start))
            timestamp_start = (long) System.currentTimeMillis() / 1000;


        double distanceMeters = location.distanceTo(lastLocationKnown);
        walkedDistance += distanceMeters;

        double plusHeight = Math.max(0, location.getAltitude() - lastLocationKnown.getAltitude());

        heightGained += plusHeight;
        lastLocationKnown = location;

    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public double getWalkingSpeed() {
        return walkedDistance / ((System.currentTimeMillis()/1000) - timestamp_start);
    }
    public double getHeightGained() {
        return heightGained;
    }
    public double getWalkedDistance() {
        return walkedDistance;
    }
}
