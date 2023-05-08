package com.example.se_lab_23b02;
import android.app.Activity;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.app.Application;
import android.content.Context;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

// Woah Singleton
public class DataSavingHelper extends Activity {
    private static DataSavingHelper INSTANCE;
    private DataSavingHelper() {

    }
    public static DataSavingHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataSavingHelper();
        }
        return INSTANCE;
    }
    public void saveSettingAsSharedPref(String key, String value) {
        SharedPreferences settings = getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public Map<String, String> getAllSettingsFromSharedPref() {
        SharedPreferences settings = getSharedPreferences("Settings", 0);
        Map<String, String> settingsContents;
        settingsContents = new HashMap<String, String>(settings.getAll().size());
        return settingsContents;
    }

    public void saveFileInInternalStorage(String filename, String contents) {
        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(contents.toByteArray());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
