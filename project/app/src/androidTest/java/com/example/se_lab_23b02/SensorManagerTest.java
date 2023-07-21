package com.example.se_lab_23b02;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import android.app.ActivityManager;
import android.content.Context;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SensorManagerTest {

    Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }
    private boolean checkRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean running = false;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SensorForegroundService.class.getName().equals(service.service.getClassName())) {
                running = true;
            }
        }
        return running;
    }
    @Test
    public void checkToggling() {
        boolean runningBefore = checkRunning();
        SensorManager.toggleService(context);
        assertEquals(runningBefore, !checkRunning());
    }

    @Test
    public void testCheckRunning() {
        boolean running = checkRunning();
        assertEquals(running, SensorManager.checkRunning(context));

        running = !running;
        SensorManager.toggleService(context);
        assertEquals(running, SensorManager.checkRunning(context));

    }
}
