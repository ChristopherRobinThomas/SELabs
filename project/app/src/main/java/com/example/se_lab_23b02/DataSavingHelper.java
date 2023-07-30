/*
 * Copyright (c) 2023.
 * Christopher Thomas, Miriam Fee, Linus Brandt, Maximilian Frese, Frederik Hüttemann
 * All rights. reserved.
 */

package com.example.se_lab_23b02;
import android.app.Activity;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.Vector;

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

    public String getSelectedLanguage(Context context) {
        SharedPreferences settings = context.getSharedPreferences("Settings", 0);
        Map<String, String> settingsContents = (Map<String, String>) settings.getAll();
        if(settingsContents.containsKey("language"))
            return settingsContents.get("language");
        saveSettingAsSharedPref("language", "english", context);
        return "english";


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
        String ret = settings.getString(keyValue, "");
        if (Objects.isNull(ret))
            return "";
        return ret;
    }

    public void deleteAllSettingsFromSharedPref(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Settings", 0);
        preferences.edit().clear().apply();
    }
    // Default usage
    public void writeDataToInternalStorage (String filename, String data, Context context) {
        //Important for multiuser -> everyone gets a file plus it's local ID
        //filename += getCurrentUserId(context);
        filename = getCurrentUserId(context) + filename;
        accessInternalStorage(filename, data, context, Context.MODE_PRIVATE);
    }
    // Overload: Access Mode
    public void writeDataToInternalStorage (String filename, String data, Context context, boolean append) {
        //Important for multiuser -> everyone gets a file plus it's local ID
        filename = getCurrentUserId(context) + filename;

        if (append) {
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
            //Log.i("dsHelper", "To file: "+filename+", wrote: "+data);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public boolean deleteFileFromInternalStorage(String filename,Context context)
    {
        filename = getCurrentUserId(context) + filename;

        File toWrite = new File(context.getFilesDir(), filename);
        if(!toWrite.exists())
            return false;

        context.deleteFile(filename);
        return true;
    }
  
    public String readSavedDataFromInternalStorage(String filename, Context context) {
        //Important for multiuser -> everyone gets a file plus it's local ID
        filename = getCurrentUserId(context) + filename;
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
        //Log.i("dsHelper", "From file: "+filename+", read: "+ret);
        return ret;
    }

    private String getCurrentUserId(Context context)
    {
        UserHelper us = UserHelper.getInstance();
        User u = us.getCurrentUser(context);
        return String.valueOf(u.getLocalId());
    }
    public Vector<Vector<String>> getSampleInterventions() {
        Vector<Vector<String>> newInterventions = new Vector<Vector<String>>();

        Vector<String> sampleInterventionA = new Vector<String>();
        sampleInterventionA.add("0");
        sampleInterventionA.add("Push-Ups");
        sampleInterventionA.add("Perform 10 push-ups.");
        newInterventions.add(sampleInterventionA);

        Vector<String> sampleInterventionB = new Vector<String>();
        sampleInterventionB.add("1");
        sampleInterventionB.add("Sit-Ups");
        sampleInterventionB.add("Perform 10 sit-ups.");
        newInterventions.add(sampleInterventionB);

        Vector<String> sampleInterventionC = new Vector<String>();
        sampleInterventionC.add("2");
        sampleInterventionC.add("Light Run");
        sampleInterventionC.add("Run a comfortable distance at a slow pace. Our recommendation: 2 kilometers in 10 minutes");
        newInterventions.add(sampleInterventionC);

        Vector<String> sampleInterventionD = new Vector<String>();
        sampleInterventionC.add("3");
        sampleInterventionC.add("Cobra");
        sampleInterventionC.add("Lie down on your stomach, with your hands against the ground. Straighten your arms and push upwards, while leaving your legs on the ground. Your back should arch upwards. Hold for 10 seconds. ");
        newInterventions.add(sampleInterventionD);

        Vector<String> sampleInterventionE = new Vector<String>();
        sampleInterventionC.add("4");
        sampleInterventionC.add("Rueckenentspannung");
        sampleInterventionC.add("Kneel on the ground. Lean forward so your knees tuck under your body. Stretch your arms forward and lean down until your face is almost on the ground. Hold for 10 seconds.");
        newInterventions.add(sampleInterventionE);

        Vector<String> sampleInterventionF = new Vector<String>();
        sampleInterventionC.add("5");
        sampleInterventionC.add("Laterallinien 1");
        sampleInterventionC.add("Stand up straight and curve your right arm above your head while leaning to the right. Repeat for the left side. Repeat three times. ");
        newInterventions.add(sampleInterventionF);

        Vector<String> sampleInterventionG = new Vector<String>();
        sampleInterventionC.add("6");
        sampleInterventionC.add("Laterallinien 2");
        sampleInterventionC.add("Stand up and cross your legs. Cross your arms over your head. Lean to one side, then to the other. Repeat three times. ");
        newInterventions.add(sampleInterventionG);

        Vector<String> sampleInterventionH = new Vector<String>();
        sampleInterventionC.add("7");
        sampleInterventionC.add("Seitliche Beinrolle");
        sampleInterventionC.add("Lie on your back. Raise your knees and feet to make two 90-degree angles. Swing your knees to one side, then the other. Repeat three times. ");
        newInterventions.add(sampleInterventionH);

        Vector<String> sampleInterventionI = new Vector<String>();
        sampleInterventionC.add("7");
        sampleInterventionC.add("Windmuehle");
        sampleInterventionC.add("Stand up straight and lean over forwards. Alternate touching your left and right foot with the opposing arm.");
        newInterventions.add(sampleInterventionI);

        return newInterventions;
    }
    public Vector<Vector<String>> getSavedInterventions(Context context) throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String savedInterventionsString = dsHelper.readSavedDataFromInternalStorage("interventions.txt", context);
        JSONArray jsonArray = new JSONArray(savedInterventionsString);          // J of J of S
        Vector<JSONArray> intermediateList = new Vector<JSONArray>();
        for(int i=0; i < jsonArray.length(); i++) {
            intermediateList.add(jsonArray.getJSONArray(i));                    // V of J of S
        }
        Vector<Vector<String>> endList = new Vector<Vector<String>>();
        for(int j=0; j < intermediateList.size(); j++) {                        // Jedes JSONA im Vector
            Vector<String> temp = new Vector<String>();
            for(int k = 0; k < intermediateList.get(j).length(); k++) {         // Jeden Eintrag im JSONA im Vector
                temp.add((String) intermediateList.get(j).get(k));              // V of S
                // V of V of S
            }
            endList.add(temp);
        }
        return endList;
    }
    public void saveInterventions(Vector<Vector<String>> interventions, Context context) throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        JSONArray endArray = new JSONArray();
        // Start: V of V of S
        JSONArray intermediateArr = new JSONArray();
        for(int i=0; i < interventions.size(); i++) {
            intermediateArr.put(interventions.get(i));                          // J of V of S
        }
        for(int j=0; j < intermediateArr.length(); j++) {                        // Jeder Vector im JSONA
            JSONArray temp = new JSONArray();
            Vector<String> elem = (Vector<String>)intermediateArr.get(j);
            for(int k = 0; k < elem.size(); k++) {                              // Jeden Eintrag im Vector im JSONA
                temp.put((String) (elem.get(k)));                               // J of S
                // J of J of S
            }
            endArray.put(temp);
        }
        dsHelper.writeDataToInternalStorage("interventions.txt", endArray.toString(), context);
    }
    public void ensureInterventions(Context context) {
        Vector<Vector<String>> emptyV = new Vector<Vector<String>>();
        Vector<Vector<String>> tempV = new Vector<Vector<String>>();
        try {
            tempV = getSavedInterventions(context);
        } catch (JSONException ignored) {}
        if(tempV.equals(emptyV)) {
            try {
                saveInterventions(getSampleInterventions(), context);
            } catch (JSONException ignored) {}
        }
    }
}
