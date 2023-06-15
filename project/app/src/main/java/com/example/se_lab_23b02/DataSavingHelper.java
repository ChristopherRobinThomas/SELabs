package com.example.se_lab_23b02;
import android.app.Activity;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.FileInputStream;

// Woah Singleton
public class DataSavingHelper {
    private static DataSavingHelper INSTANCE;
    private DataSavingHelper() {

    }
    public static DataSavingHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataSavingHelper();
        }
        return INSTANCE;
    }
    public void saveSettingAsSharedPref(String key, String value, Context context) {
        SharedPreferences settings = context.getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public Map<String, String> getAllSettingsFromSharedPref(Context context) {
        SharedPreferences settings = context.getSharedPreferences("Settings", 0);
        Map<String, String> settingsContents;
        settingsContents = (Map<String, String>) settings.getAll();
        return settingsContents;
    }

    public String getSpecificSettingsFromSharedPref(String keyValue, Context context){
        SharedPreferences settings = context.getSharedPreferences("Settings", 0);
        return settings.getString(keyValue, "");
    }

    public void deleteAllSettingsFromSharedPref(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Settings", 0);
        preferences.edit().clear().apply();
    }
    // Default usage
    public void writeDataToInternalStorage (String filename, String data, Context context) {
        accessInternalStorage(filename, data, context, Context.MODE_PRIVATE);
    }
    // Overload: Access Mode
    public void writeDataToInternalStorage (String filename, String data, Context context, boolean append) {
        if (append) {
            System.out.println("appending");
            accessInternalStorage(filename, data, context, Context.MODE_APPEND);
        } else {
            accessInternalStorage(filename, data, context, Context.MODE_PRIVATE);
        }
    }
    private void accessInternalStorage(String filename, String data, Context context, int accessMode) {
        try {
            // If file does not yet exist, create it
            File toWrite = new File(context.getFilesDir(), filename);
            if(!toWrite.exists()){
                toWrite.createNewFile();
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, accessMode));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            //Log.e("dsHelper", "To file: "+filename+", wrote: "+data);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public String readSavedDataFromInternalStorage(String filename, Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(filename);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("dsHelper", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("dsHelper", "Can not read file: " + e.toString());
        }
        //Log.e("dsHelper", "From file: "+filename+", read: "+ret);
        return ret;
    }
}
