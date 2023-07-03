package com.example.se_lab_23b02;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
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
    private Context contextForSharedPreferences;
    private Resources resources;
    private DataSavingHelper dsHelper;
    private HashMap<String, String> settings;
    private ArrayList<Object> textViews = new ArrayList<>();


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

        contextForSharedPreferences = getActivity().getApplicationContext();

        dsHelper = DataSavingHelper.getInstance();

        settings = (HashMap<String, String>) dsHelper.getAllSettingsFromSharedPref(contextForSharedPreferences);

        String language = settings.get("language");

        if(language.equals("deutsch")) {
            context = LocaleHelper.setLocale(getActivity().getApplicationContext(), "de");
        }
        if(language.equals("english")) {
            context = LocaleHelper.setLocale(getActivity().getApplicationContext(), "en");
        }
        if(language.equals("francais")) {
            context = LocaleHelper.setLocale(getActivity().getApplicationContext(), "fr");
        }

        View view = inflater.inflate(R.layout.fragment_settings_2, container, false);
        resources = context.getResources();

        // add textviews for language change
        textViews.add(view.findViewById(R.id.tvSettingsApperance));
        textViews.add(view.findViewById(R.id.tvSettingsLanguage));
        textViews.add(view.findViewById(R.id.tv_select_color));
        textViews.add(view.findViewById(R.id.tvSettingsSensor));
        textViews.add(view.findViewById(R.id.tvSettingsStepCounter));
        textViews.add(view.findViewById(R.id.tvSettingsAccel));
        textViews.add(view.findViewById(R.id.tvGeoTagging));
        textViews.add(view.findViewById(R.id.tvSettingsNotification));
        textViews.add(view.findViewById(R.id.tvMorningNotification));
        textViews.add(view.findViewById(R.id.tvSettingsNoon));
        textViews.add(view.findViewById(R.id.tvSettingsEvening));
        textViews.add(view.findViewById(R.id.btDeleteData));
        textViews.add(view.findViewById(R.id.btSettingsLogout));


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
        Button btLogout = (Button) view.findViewById(R.id.btSettingsLogout);

        saveDefaultValuesInSettings();
        setAllStrings();

        setupSpinnerValues(spinnerLanguage, spinnerColortheme, spinnerDarkMode, settings);
        setupCheckboxValues(switchAccel, switchStepCount, switchGPS, settings);

        //language Spinner selection Listener
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String val = parent.getItemAtPosition(position).toString();

                dsHelper.saveSettingAsSharedPref("language", val, contextForSharedPreferences);

                if(val.equals("english")) {
                    context = LocaleHelper.setLocale(getActivity().getApplicationContext(), "en");

                }
                if(val.equals("deutsch")) {
                    context = LocaleHelper.setLocale(getActivity().getApplicationContext(), "de");
                }
                if(val.equals("francais")) {
                    context = LocaleHelper.setLocale(getActivity().getApplicationContext(), "fr");
                }

                resources = context.getResources();
                setAllStrings();
                refreshSettings();
                setSpinnerWithoutLanguageSpinner(spinnerColortheme, spinnerDarkMode);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerColortheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String val = "blue";

                switch(position) {
                    case 1:
                        val = "red";
                        break;
                    case 2:
                        val = "green";
                        break;
                }

                dsHelper.saveSettingAsSharedPref("color", val, contextForSharedPreferences);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDarkMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String val = "white-mode";
                switch(position) {
                    case 1:
                        val = "dark-mode";
                        break;
                    case 2:
                        val = "match OS";
                        break;
                }
                dsHelper.saveSettingAsSharedPref("dmValue", val, contextForSharedPreferences);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switchAccel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dsHelper.saveSettingAsSharedPref("AccelAllowed", String.valueOf(b), contextForSharedPreferences);
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
                dsHelper.saveSettingAsSharedPref("GPSAllowed", String.valueOf(b), contextForSharedPreferences);
            }
        });

        switchStepCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, 0);
                    }

                }

                dsHelper.saveSettingAsSharedPref("StepCounterAllowed", String.valueOf(b), contextForSharedPreferences);
            }
        });

        btnNotifMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle b = new Bundle();
                refreshSettings();

                b.putInt("lowerBound", Integer.parseInt(settings.get("mornLB")));
                b.putInt("upperBound", Integer.parseInt(settings.get("mornUB")));
                b.putInt("currentHour", Integer.parseInt(settings.get("mornCurH")));
                b.putInt("currentMin", Integer.parseInt(settings.get("mornCurM")));
                b.putInt("id", 0);


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
            public void onClick(View v)  {
                cancelAlarms();

                //delete data here
                dsHelper.deleteAllSettingsFromSharedPref(contextForSharedPreferences);
                saveDefaultValuesInSettings();
                refreshSettings();
                setupCheckboxValues(switchAccel,switchStepCount, switchGPS, settings);
                setupSpinnerValues(spinnerLanguage, spinnerColortheme, spinnerDarkMode, settings);
                Toast.makeText(getContext(), "All data deleted successfully.", Toast.LENGTH_SHORT).show();

            }
        });

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarms();
                Intent myIntent = new Intent(getActivity(), MultiUserActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(myIntent);
            }
        });

        return view;
    }


    //this method takes the spinner and the settings as parameters to set the spinner's values and the default value
    private void setupSpinnerValues(Spinner spLang, Spinner spColor, Spinner spDMode, HashMap<String, String> settings) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.languages));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLang.setAdapter(arrayAdapter);

        String[] tempL = getResources().getStringArray(R.array.languages);
        ArrayList<String> languages = new ArrayList<>(Arrays.asList(tempL));



        if(settings.containsKey("language")) {
            int spinnerPosition = languages.indexOf(settings.get("language"));
            spLang.setSelection(spinnerPosition);
        }


        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.colortheme));
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spColor.setAdapter(colorAdapter);

        String[] tempC = getResources().getStringArray(R.array.colortheme);
        ArrayList<String> colors = new ArrayList<>(Arrays.asList(tempC));

        if(settings.containsKey("color")) {
            int spinnerPosition = colors.indexOf(settings.get("color"));
            spColor.setSelection(spinnerPosition);
        }

        ArrayAdapter<String> darkModeAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.darkmode));
        darkModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDMode.setAdapter(darkModeAdapter);

        String[] tempD = getResources().getStringArray(R.array.darkmode);
        ArrayList<String> darkmode = new ArrayList<>(Arrays.asList(tempD));

        if(settings.containsKey("dmValue")) {
            int spinnerPosition = darkmode.indexOf(settings.get("dmValue"));
            spDMode.setSelection(spinnerPosition);
        }
    }

    //this method takes all switches and the settings map to set the switches to checked if their corresponding value is true/false
    private void setupCheckboxValues(Switch swAcc, Switch swStep, Switch swGPS, HashMap<String, String> settings) {
        if(settings.containsKey("StepCounterAllowed")) {
            swStep.setChecked(Boolean.parseBoolean(settings.get("StepCounterAllowed")));
        } else
            swStep.setChecked(false);
        if(settings.containsKey("AccelAllowed")) {
            swAcc.setChecked(Boolean.parseBoolean(settings.get("AccelAllowed")));
        } else
            swAcc.setChecked(false);
        if(settings.containsKey("GPSAllowed")) {
            swGPS.setChecked(Boolean.parseBoolean(settings.get("GPSAllowed")));
        }
        swGPS.setChecked(false);
    }

    //this is for the case that all the settings are not set yet so we don't try to access data which isn't available
    private void saveDefaultValuesInSettings() {
        Map<String, String> tempSettings = dsHelper.getAllSettingsFromSharedPref(contextForSharedPreferences);
        if(!tempSettings.containsKey("mornCurH"))
            dsHelper.saveSettingAsSharedPref("mornCurH", "8", contextForSharedPreferences);
        if(!tempSettings.containsKey("mornCurM"))
            dsHelper.saveSettingAsSharedPref("mornCurM", "15", contextForSharedPreferences);
        if(!tempSettings.containsKey("mornLB"))
            dsHelper.saveSettingAsSharedPref("mornLB", "7", contextForSharedPreferences);
        if(!tempSettings.containsKey("mornUB"))
            dsHelper.saveSettingAsSharedPref("mornUB", "11", contextForSharedPreferences);
        if(!tempSettings.containsKey("noonCurH"))
            dsHelper.saveSettingAsSharedPref("noonCurH", "13", contextForSharedPreferences);
        if(!tempSettings.containsKey("noonCurM"))
            dsHelper.saveSettingAsSharedPref("noonCurM", "0", contextForSharedPreferences);
        if(!tempSettings.containsKey("noonLB"))
            dsHelper.saveSettingAsSharedPref("noonLB", "12", contextForSharedPreferences);
        if(!tempSettings.containsKey("noonUB"))
            dsHelper.saveSettingAsSharedPref("noonUB", "14", contextForSharedPreferences);
        if(!tempSettings.containsKey("evnCurH"))
            dsHelper.saveSettingAsSharedPref("evnCurH", "18", contextForSharedPreferences);
        if(!tempSettings.containsKey("evnCurM"))
            dsHelper.saveSettingAsSharedPref("evnCurM", "0", contextForSharedPreferences);
        if(!tempSettings.containsKey("evnLB"))
            dsHelper.saveSettingAsSharedPref("evnLB", "17", contextForSharedPreferences);
        if(!tempSettings.containsKey("evnUB"))
            dsHelper.saveSettingAsSharedPref("evnUB", "19", contextForSharedPreferences);
    }

    private void refreshSettings() {
        settings = (HashMap<String, String>) dsHelper.getAllSettingsFromSharedPref(contextForSharedPreferences);
    }

    private void setAllStrings() {
        getActivity().setTitle(resources.getString(R.string.settings_heading));

        TextView tv = (TextView) textViews.get(0);
        tv.setText(resources.getString(R.string.settings_title));
        tv = (TextView) textViews.get(1);
        tv.setText(resources.getString(R.string.tv_settings_language_title));
        tv = (TextView) textViews.get(2);
        tv.setText(resources.getString(R.string.tv_settings_select_color));
        tv = (TextView) textViews.get(3);
        tv.setText(resources.getString(R.string.tv_settings_settings_sensor));
        tv = (TextView) textViews.get(4);
        tv.setText(resources.getString(R.string.tv_settings_step_counter));
        tv = (TextView) textViews.get(5);
        tv.setText(resources.getString(R.string.tv_settings_accel));
        tv = (TextView) textViews.get(6);
        tv.setText(resources.getString(R.string.tv_settings_geo_tagging));
        tv = (TextView) textViews.get(7);
        tv.setText(resources.getString(R.string.tv_settings_notification_title));
        tv = (TextView) textViews.get(8);
        tv.setText(resources.getString(R.string.tv_settings_morning_notification));
        tv = (TextView) textViews.get(9);
        tv.setText(resources.getString(R.string.tv_settings_noon_notification));
        tv = (TextView) textViews.get(10);
        tv.setText(resources.getString(R.string.tv_settings_evening_notification));
        Button bt = (Button) textViews.get(11);
        bt.setText(resources.getString(R.string.bt_settings_delete_data));
        bt = (Button) textViews.get(12);
        bt.setText(resources.getText(R.string.bt_settings_logout));
    }

    public void setSpinnerWithoutLanguageSpinner(Spinner spColor, Spinner spDMode) {
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.colortheme));
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spColor.setAdapter(colorAdapter);

        String[] tempC = getResources().getStringArray(R.array.colortheme);
        ArrayList<String> colors = new ArrayList<>(Arrays.asList(tempC));

        if(settings.containsKey("color")) {
            int spinnerPosition = colors.indexOf(settings.get("color"));
            spColor.setSelection(spinnerPosition);
        }

        ArrayAdapter<String> darkModeAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.darkmode));
        darkModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDMode.setAdapter(darkModeAdapter);

        String[] tempD = getResources().getStringArray(R.array.darkmode);
        ArrayList<String> darkmode = new ArrayList<>(Arrays.asList(tempD));

        if(settings.containsKey("dmValue")) {
            int spinnerPosition = darkmode.indexOf(settings.get("dmValue"));
            spDMode.setSelection(spinnerPosition);
        }
    }
    private void cancelAlarms()
    {
        for (int i = 0; i<3;i++){
            Alarm a = new Alarm(i);
            a.cancelAlarm(getContext());
        }
    }
}

