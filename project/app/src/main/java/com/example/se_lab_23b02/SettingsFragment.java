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

        context = getContext();

        DataSavingHelper dsHelper = DataSavingHelper.getInstance();


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



        HashMap<String, String> settings = (HashMap<String, String>) dsHelper.getAllSettingsFromSharedPref(context);


        setupSpinnerValues(spinnerLanguage, spinnerColortheme, spinnerDarkMode, settings);

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

                System.out.println(b);
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
                System.out.println(b);
            }
        });

        switchStepCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                System.out.println(b);
            }
        });

        btnNotifMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle b = new Bundle();


                if(settings.containsKey("mornLB") && settings.containsKey("mornUB") && settings.containsKey("mornCurH") && settings.containsKey("mornCurM")) {
                    //do nothing
                } else {
                    settings.put("mornLB", "7");
                    settings.put("mornUB", "11");
                    settings.put("mornCurH", "8");
                    settings.put("mornCurM", "15");


                }
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

                if(settings.containsKey("noonLB") && settings.containsKey("noonUB") && settings.containsKey("noonCurH") && settings.containsKey("noonCurM")) {
                    //do nothing
                } else {
                    settings.put("noonLB", "12");
                    settings.put("noonUB", "14");
                    settings.put("noonCurH", "13");
                    settings.put("noonCurM", "0");


                }
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

                if(settings.containsKey("evnLB") && settings.containsKey("evnUB") && settings.containsKey("evnCurH") && settings.containsKey("evnCurM")) {
                    //do nothing
                } else {
                    settings.put("evnLB", "12");
                    settings.put("evnUB", "14");
                    settings.put("evnCurH", "13");
                    settings.put("evnCurM", "0");
                }

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
            }
        });
        /*
        sensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorSettingsFragment sensorSettingsFragment = new SensorSettingsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,sensorSettingsFragment).commit();

            }
        });
        */


        // Inflate the layout for this fragment
        return view;
    }

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
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:

                switchGPS.setChecked(grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;

        }

    }
}

