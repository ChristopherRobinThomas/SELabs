package com.example.se_lab_23b02;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private Switch switchGPS;
    private Context context;
    private DataSavingHelper dsHelper;

    private HashMap<String, String> settings;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();

        dsHelper = DataSavingHelper.getInstance();


        View view = inflater.inflate(R.layout.fragment_settings_2, container, false);

        Spinner spinnerLanguage = (Spinner) view.findViewById(R.id.spLanguageSelector);
        Spinner spinnerColortheme = (Spinner) view.findViewById(R.id.spinner_color);
        Spinner spinnerDarkMode = (Spinner) view.findViewById(R.id.spDarkMode);
        Switch switchStepCount = (Switch) view.findViewById(R.id.swStepCounter);
        Switch switchAccel = (Switch) view.findViewById(R.id.swAccelSensor);
        switchGPS = (Switch) view.findViewById(R.id.swGPS);
        Button btnNotifMorning = (Button) view.findViewById(R.id.btChangeNotificationMorning);
        Button btnNotifNoon = (Button) view.findViewById(R.id.btChangeNotificationNoon);
        Button btnNotifEvening = (Button) view.findViewById(R.id.btChangeNotificationEvening);
        Button btnDelAllData = (Button) view.findViewById(R.id.btDeleteData);

        saveDefaultValuesInSettings();

        settings = (HashMap<String, String>) dsHelper.getAllSettingsFromSharedPref(context);


        setupSpinnerValues(spinnerLanguage, spinnerColortheme, spinnerDarkMode, settings);
        setupCheckboxValues(switchAccel, switchStepCount, switchGPS, settings);

        //language Spinner selection Listener
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String val = parent.getItemAtPosition(position).toString();

                dsHelper.saveSettingAsSharedPref("language", val, context);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerColortheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String val = parent.getItemAtPosition(position).toString();
                dsHelper.saveSettingAsSharedPref("color", val, context);
                System.out.println(val);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDarkMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String val = parent.getItemAtPosition(position).toString();
                dsHelper.saveSettingAsSharedPref("dmValue", val, context);
                System.out.println(val);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switchAccel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dsHelper.saveSettingAsSharedPref("AccelAllowed", String.valueOf(b), context);
            }
        });

        switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    }

                }
                dsHelper.saveSettingAsSharedPref("GPSAllowed", String.valueOf(b), context);
            }
        });

        switchStepCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dsHelper.saveSettingAsSharedPref("StepCounterAllowed", String.valueOf(b), context);
            }
        });

        btnNotifMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle b = new Bundle();
                refreshSettings();
                System.out.println(settings);

                b.putInt("lowerBound", Integer.parseInt(settings.get("mornLB")));
                b.putInt("upperBound", Integer.parseInt(settings.get("mornUB")));
                b.putInt("currentHour", Integer.parseInt(settings.get("mornCurH")));
                b.putInt("currentMin", Integer.parseInt(settings.get("mornCurM")));
                b.putInt("id", 0);
                //System.out.println(b);

                Intent intent = new Intent(getActivity(), TimePickerActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        btnNotifNoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();

                refreshSettings();

                b.putInt("lowerBound", Integer.parseInt(settings.get("noonLB")));
                b.putInt("upperBound", Integer.parseInt(settings.get("noonUB")));
                b.putInt("currentHour", Integer.parseInt(settings.get("noonCurH")));
                b.putInt("currentMin", Integer.parseInt(settings.get("noonCurM")));
                b.putInt("id", 1);

                Intent intent = new Intent(getActivity(), TimePickerActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        btnNotifEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();

                refreshSettings();

                b.putInt("lowerBound", Integer.parseInt(settings.get("evnLB")));
                b.putInt("upperBound", Integer.parseInt(settings.get("evnUB")));
                b.putInt("currentHour", Integer.parseInt(settings.get("evnCurH")));
                b.putInt("currentMin", Integer.parseInt(settings.get("evnCurM")));
                b.putInt("id", 2);

                Intent intent = new Intent(getActivity(), TimePickerActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        btnDelAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("btnDelClick");
                System.out.println(dsHelper.getAllSettingsFromSharedPref(context));

            }
        });

        return view;
    }


    //this method takes the spinner and the settings as parameters to set the spinner's values and the default value
    private void setupSpinnerValues(Spinner spLang, Spinner spColor, Spinner spDMode, HashMap<String, String> settings) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.languages));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLang.setAdapter(arrayAdapter);

        if(settings.containsKey("language")) {
            int spinnerPosition = arrayAdapter.getPosition(settings.get("language"));
            spLang.setSelection(spinnerPosition);
        }


        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.colortheme));
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spColor.setAdapter(colorAdapter);

        if(settings.containsKey("color")) {
            int spinnerPosition = colorAdapter.getPosition(settings.get("color"));
            spColor.setSelection(spinnerPosition);
        }

        ArrayAdapter<String> darkModeAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.darkmode));
        darkModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDMode.setAdapter(darkModeAdapter);

        if(settings.containsKey("dmValue")) {
            int spinnerPosition = darkModeAdapter.getPosition(settings.get("dmValue"));
            spDMode.setSelection(spinnerPosition);
        }
    }

    //this method takes all switches and the settings map to set the switches to checked if their corresponding value is true/false
    private void setupCheckboxValues(Switch swAcc, Switch swStep, Switch swGPS, HashMap<String, String> settings) {
        if(settings.containsKey("StepCounterAllowed")) {
            swStep.setChecked(Boolean.parseBoolean(settings.get("StepCounterAllowed")));
        }
        if(settings.containsKey("AccelAllowed")) {
            swAcc.setChecked(Boolean.parseBoolean(settings.get("AccelAllowed")));
        }
        if(settings.containsKey("GPSAllowed")) {
            swGPS.setChecked(Boolean.parseBoolean(settings.get("GPSAllowed")));
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:

                switchGPS.setChecked(grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;

        }
    }

    private void saveDefaultValuesInSettings() {
        Map<String, String> tempSettings = dsHelper.getAllSettingsFromSharedPref(context);
        if(!tempSettings.containsKey("mornCurH"))
            dsHelper.saveSettingAsSharedPref("mornCurH", "8", context);
        if(!tempSettings.containsKey("mornCurM"))
            dsHelper.saveSettingAsSharedPref("mornCurM", "15", context);
        if(!tempSettings.containsKey("mornLB"))
            dsHelper.saveSettingAsSharedPref("mornLB", "7", context);
        if(!tempSettings.containsKey("mornUB"))
            dsHelper.saveSettingAsSharedPref("mornUB", "11", context);
        if(!tempSettings.containsKey("noonCurH"))
            dsHelper.saveSettingAsSharedPref("noonCurH", "13", context);
        if(!tempSettings.containsKey("noonCurM"))
            dsHelper.saveSettingAsSharedPref("noonCurM", "0", context);
        if(!tempSettings.containsKey("noonLB"))
            dsHelper.saveSettingAsSharedPref("noonLB", "12", context);
        if(!tempSettings.containsKey("noonUB"))
            dsHelper.saveSettingAsSharedPref("noonUB", "14", context);
        if(!tempSettings.containsKey("evnCurH"))
            dsHelper.saveSettingAsSharedPref("evnCurH", "18", context);
        if(!tempSettings.containsKey("evnCurM"))
            dsHelper.saveSettingAsSharedPref("evnCurM", "0", context);
        if(!tempSettings.containsKey("evnLB"))
            dsHelper.saveSettingAsSharedPref("evnLB", "17", context);
        if(!tempSettings.containsKey("evnUB"))
            dsHelper.saveSettingAsSharedPref("evnUB", "19", context);
    }

    private void refreshSettings() {
        settings = (HashMap<String, String>) dsHelper.getAllSettingsFromSharedPref(context);
    }

}

