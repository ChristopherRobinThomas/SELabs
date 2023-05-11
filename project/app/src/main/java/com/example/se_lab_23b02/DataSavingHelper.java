package com.example.se_lab_23b02;
import android.app.Activity;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.app.Application;
import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.FileInputStream;

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
    public void writeDataToInternalStorage (String filename, String data) {
        try {
            FileOutputStream fOut = openFileOutput(filename,1) ;
            OutputStreamWriter osw = new OutputStreamWriter(fOut) ;
            osw.write(data);
            osw.flush();
            osw.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public String readSavedDataFromInternalStorage(String filename) {
        StringBuilder datax = new StringBuilder("");
        try {
            FileInputStream fIn = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader buffreader = new BufferedReader(isr);
            String readString = buffreader.readLine();
            while (readString != null) {
                datax.append(readString);
                readString = buffreader.readLine();
            }
            isr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return datax.toString();
    }
}
