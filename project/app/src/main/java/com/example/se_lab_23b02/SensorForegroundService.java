package com.example.se_lab_23b02;


import android.annotation.SuppressLint;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SensorForegroundService extends Service implements SensorEventListener {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private Sensor[] sensors = new Sensor[3];
    private boolean[] bools = new boolean[3];

    private Location lastKnownLocation;
    private MyLocationListener myLocationListener;
    private ArrayList<ArrayList<String>> values;
    private boolean active = false;
    private int startSteps = -1;
    private String timestamp;
    private long curTS = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("MissingPermission")
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
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
                myLocationListener = new MyLocationListener();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);

            }
            active = true;


        } else {
            if(active) {

                String gpsValue;
                if(bools[2]) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    gpsValue =  df.format(myLocationListener.getWalkedDistance());
                    locationManager.removeUpdates(myLocationListener);
                } else
                    gpsValue = "";
                DataSavingHelper dsHelper = DataSavingHelper.getInstance();

                String sensorValues = "{\"timestamp\":\"" + timestamp + "\",\"acceleration\":" + values.get(0).toString() + ",\"stepcounter\":" + values.get(1).toString() + ",\"distance\":[" + gpsValue + "]" + ",\"user_id\":\"" + UserHelper.getInstance().getCurrentUser(getApplicationContext()).getGlobalId() + "\"}\n";
                dsHelper.writeDataToInternalStorage("sensorrecordings.txt", sensorValues, getApplicationContext(), true);
              
                Debug.logStack("SensorService", "Saving sensor data", 0);
                Debug.logStack("values", values.get(2 ).toString(), 1);
              
                ServerHelper sHelper = ServerHelper.getInstance();
                try {
                    sHelper.synchronize(getApplicationContext());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                //deregister listener
                sensorManager.unregisterListener(this, sensors[0]);
                sensorManager.unregisterListener(this, sensors[1]);


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
            long ts = System.currentTimeMillis();
            if(curTS == 0) {
                curTS = ts;
            }
            if(ts != curTS) {
                String vals = "{\"timestamp\": \"" + ts + "\", \"x\": \"" + String.valueOf(event.values[0]) + "\", \"y\": \"" + String.valueOf(event.values[1]) + "\", \"z\": \"" + String.valueOf(event.values[2]) + "\"}";
                values.get(0).add(vals);
                curTS = ts;
            }
        }
        if(event.sensor == sensors[1]) {
            if (startSteps < 0) {
                 startSteps = (int)event.values[0];
                 values.get(1).add(String.valueOf(startSteps));
            }

            values.get(1).remove(0);
            values.get(1).add(String.valueOf(event.values[0] - startSteps));
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
        String gpsAllowedAsString = dsHelper.getSpecificSettingsFromSharedPref("GPSAllowed", getApplicationContext());
        if(gpsAllowedAsString.equals("true")) {
            bools[2] = true;
        }
        return bools;
    }
}
