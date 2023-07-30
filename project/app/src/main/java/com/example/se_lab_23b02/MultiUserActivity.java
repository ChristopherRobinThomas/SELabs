/*
 * Copyright (c) 2023.
 * Christopher Thomas, Miriam Fee, Linus Brandt, Maximilian Frese, Frederik Hüttemann
 * All rights. reserved.
 */

package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class MultiUserActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_user);




        requestPushNotification();


        if(checkForActiveUser()){
            ServerHelper sHelper = ServerHelper.getInstance();
            sHelper.setActivity(this);
            try {
                sHelper.synchronize(getApplicationContext());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            Intent i = new Intent(this, HomeScreenActivity.class);
            startActivity(i);
            finish();
        }else{
            //Show the normal Multiuser Fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.multi_user_frame_layout, new MultiUserMainFragment());
            fragmentTransaction.commit();

        }
    }



    private void requestPushNotification() {
        try{
            if(Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.POST_NOTIFICATIONS}, 0);
            }
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    private boolean checkForActiveUser(){
        DataSavingHelper dsh = DataSavingHelper.getInstance();
        if(dsh.getAllSettingsFromSharedPref(this).containsKey("ActiveUser")){
            String current = dsh.getAllSettingsFromSharedPref(this).get("ActiveUser");

            if(current.equals(dsh.getAllSettingsFromSharedPref(this).get("UserOne")))
                return true;
            if(current.equals(dsh.getAllSettingsFromSharedPref(this).get("UserTwo")))
                return true;
            if(current.equals(dsh.getAllSettingsFromSharedPref(this).get("UserThree")))
                return true;
            if(current.equals(dsh.getAllSettingsFromSharedPref(this).get("UserFour")))
                return true;
        }
        return false;
    }
}