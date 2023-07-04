package com.example.se_lab_23b02;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class SensorManager {

    public static void toggleService(Context context) {
        boolean running = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SensorForegroundService.class.getName().equals(service.service.getClassName())) {
                running = true;
            }
        }
        if(running)
            stopService(context);
        else
            startService(context);

    }

    public static boolean checkRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SensorForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private static void startService(Context context) {
        Toast.makeText(context, "recording started", Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(context, SensorForegroundService.class);
        serviceIntent.putExtra("extras", "start");
        ContextCompat.startForegroundService(context.getApplicationContext(), serviceIntent);
    }

    private static void stopService(Context context) {
        Toast.makeText(context, "recording ended", Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(context, SensorForegroundService.class);
        serviceIntent.putExtra("extras", "stop");
        ContextCompat.startForegroundService(context.getApplicationContext(), serviceIntent);
    }
}
