package com.example.se_lab_23b02;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class DataSavingHelperTest {
    DataSavingHelper dsHelper;
    Context context;
    @Before
    public void initialize() {
        dsHelper = DataSavingHelper.getInstance();
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void getAllSettingsFromSharedPref() {
        checkGetAllSettingsFromSharedPref("hello", "world");
        checkGetAllSettingsFromSharedPref("1", "1");
        checkGetAllSettingsFromSharedPref("test", "");
    }

    @Test
    public void testInternalReadWrite() {
        String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        final String testingFilename = "UnitTest";

        dsHelper.writeDataToInternalStorage(testingFilename, lorem, context);
        String readData = dsHelper.readSavedDataFromInternalStorage(testingFilename, context).trim();

        assertEquals(lorem, readData);
    }

    public void checkGetAllSettingsFromSharedPref(String key, String value) {
        dsHelper.saveSettingAsSharedPref(key, value, context);
        Map<String, String> returnValue;
        returnValue = (HashMap<String, String>) dsHelper.getAllSettingsFromSharedPref(context);

        assertEquals(value, returnValue.get(key));
    }
}