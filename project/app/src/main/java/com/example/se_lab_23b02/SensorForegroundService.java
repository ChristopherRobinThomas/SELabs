package com.example.se_lab_23b02;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

public class SensorForegroundService extends Service implements SensorEventListener {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private SensorManager sensorManager;
    private Sensor[] sensors = new Sensor[3];
    private boolean[] bools = new boolean[3];
    private ArrayList<ArrayList<String>> values;
    private boolean active = false;
    private int startSteps = -1;
    private String timestamp;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("extras");
        if (input.equals("start")) {
            timestamp = TimeStamp.getCurrentTimeStamp();
            createNotificationChannel();
            Intent notificationIntent = new Intent(this, HomeScreenActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Recording Data")
                    .setContentText("You are currently recording the data")
                    .setSmallIcon(R.drawable.red_round_button)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);

            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            bools = getActivatedSensors();
            values = new ArrayList<>();
            for (int i = 0; i < 3; i ++) {
                ArrayList<String> valueList = new ArrayList<>();
                values.add(valueList);
            }
            if(bools[0]) {
                sensors[0] = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, sensors[0], SensorManager.SENSOR_DELAY_NORMAL);
            }
            if(bools[1]) {
                sensors[1] = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                sensorManager.registerListener(this, sensors[1], SensorManager.SENSOR_DELAY_NORMAL);
            }
            if(bools[2]) {
                // GPS
            }
            active = true;


        } else {
            if(active) {
                for(int i = 0; i < sensors.length; i++) {
                    if(bools[i])
                        sensorManager.unregisterListener(this, sensors[i]);

                }
                DataSavingHelper dsHelper = DataSavingHelper.getInstance();
                String sensorValues = "{\"timestamp\": \"" + timestamp + "\", \"acceleration\": \"" + values.get(0).toString() + "\", \"stepcounter\": \"" + values.get(1).toString() + "\"}\n";
                dsHelper.writeDataToInternalStorage("sensorrecordings.txt", sensorValues, getApplicationContext(), true);
                Debug.logStack("values", values.get(1).toString(), 1);
                active = false;
                stopForeground(true);
                stopSelf();

            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == sensors[0]) {
            String vals = "{\"timestamp\": \""+ event.timestamp + "\", \"x\": \"" + String.valueOf(event.values[0]) + "\", \"y\": \"" + String.valueOf(event.values[1]) + "\", \"z\": \"" + String.valueOf(event.values[2]) + "\"}";
            values.get(0).add(vals);
        }
        if(event.sensor == sensors[1]) {
            if (startSteps < 0) {
                 startSteps = (int)event.values[0];
                 values.get(1).add(String.valueOf(startSteps));
            }

            values.get(1).remove(0);
            values.get(1).add(String.valueOf(event.values[0] - startSteps));
        }
        if(event.sensor == sensors[2]) {
            // GPS
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private boolean[] getActivatedSensors() {

        boolean[] bools = {false, false, false};
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();

        String accelStatusAsString = dsHelper.getSpecificSettingsFromSharedPref("AccelAllowed", getApplicationContext());
        if (accelStatusAsString.equals("true")) {
            bools[0] = true;
        }
        String stepStatusAsString = dsHelper.getSpecificSettingsFromSharedPref("StepCounterAllowed", getApplicationContext());
        if (stepStatusAsString.equals("true")) {
            bools[1] = true;
        }
        return bools;
    }
}
