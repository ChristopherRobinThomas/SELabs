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
        User u = new User("2,6b8cd66f-2794-11ee-bd9f-0adab99dbc12,test,5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8");
        DataSavingHelper.getInstance().saveSettingAsSharedPref("ActiveUser",u.toString(), context);
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

    @Test
    public void testGetSelectedLanguage() {

    }
}