package com.example.se_lab_23b02;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import android.app.ActivityManager;
import android.content.Context;

import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class SensorManagerTest {

    Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        User u = new User("2,6b8cd66f-2794-11ee-bd9f-0adab99dbc12,test,5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8");
        DataSavingHelper.getInstance().saveSettingAsSharedPref("ActiveUser",u.toString(), context);
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
    public void testCheckToggling() throws InterruptedException {
        boolean result = !checkRunning();
        SensorManager.toggleService(context, "");
        TimeUnit.SECONDS.sleep(1);
        assertEquals(result, checkRunning());
    }

    @Test
    public void testCheckRunning() {
        boolean running = checkRunning();
        assertEquals(running, SensorManager.checkRunning(context));

        running = !running;
        SensorManager.toggleService(context, "");
        assertEquals(running, SensorManager.checkRunning(context));

    }
}
