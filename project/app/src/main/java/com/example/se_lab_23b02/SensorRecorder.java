package com.example.se_lab_23b02;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class SensorRecorder extends Activity implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor sensor_to_read;
    private Sensor[] sensors;
    private Context context;
    private DataSavingHelper dsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public SensorRecorder(Context context) {
        dsHelper = DataSavingHelper.getInstance();
        this.context = context;

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String writing = String.valueOf(event.values[0]) + "\n";
        dsHelper.writeDataToInternalStorage("sensorValues.txt", writing, context, true);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
