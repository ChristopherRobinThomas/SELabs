/*
 * Copyright (c) 2023.
 * Christopher Thomas, Miriam Fee, Linus Brandt, Maximilian Frese, Frederik Hüttemann
 * All rights. reserved.
 */

package com.example.se_lab_23b02;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class SensorManager {

    public static void toggleService(Context context, String timestamp) {
        boolean running = checkRunning(context);
        if(running)
            stopService(context);
        else
            startService(context, timestamp);

    }

    private static Resources getLanguageResources(Context pContext) {
        String language = DataSavingHelper.getInstance().getSpecificSettingsFromSharedPref("language", pContext);
        String loc = "en";
        if(language.equals("francais"))
            loc = "fr";
        if(language.equals("deutsch"))
            loc = "de";

        Context context = LocaleHelper.setLocale(pContext, loc);
        return context.getResources();
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
    private static void startService(Context context, String timestamp) {
        try {
            Resources res = getLanguageResources(context);
            Toast.makeText(context, res.getString(R.string.rec_start), Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Intent serviceIntent = new Intent(context, SensorForegroundService.class);
        serviceIntent.putExtra("extras", "start");
        serviceIntent.putExtra("timestamp", timestamp);
        ContextCompat.startForegroundService(context.getApplicationContext(), serviceIntent);
    }

    private static void stopService(Context context) {
        try {
            Resources res = getLanguageResources(context);
            Toast.makeText(context, res.getString(R.string.rec_stopped), Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Intent serviceIntent = new Intent(context, SensorForegroundService.class);
        serviceIntent.putExtra("extras", "stop");
        ContextCompat.startForegroundService(context.getApplicationContext(), serviceIntent);
    }
}
