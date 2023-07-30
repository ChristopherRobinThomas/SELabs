/*
 * Copyright (c) 2023.
 * Christopher Thomas, Miriam Fee, Linus Brandt, Maximilian Frese, Frederik Hüttemann
 * All rights. reserved.
 */

package com.example.se_lab_23b02;

import static android.content.Context.SENSOR_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.provider.ContactsContract;
import android.util.Log;
import android.system.Os;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.sql.Time;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements SensorEventListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //-------Objects--------------
    private DataSavingHelper dsHelper;
    private SensorManager sensorManager;
    private Sensor sensor_to_read;
    private DataAccessHelper dataAccessHelper;
    Vector<Vector<String>> interventionList;

    //---------Variables--------
    private String dailyGoal;
    private String todaysDate;
    private String date;
    private String innitdailyGoal;

    private float goalProgress;
    private boolean running = false;
    private int innitStepCount;
    private int newStepCount;
    private int overallWellBeing;

    //--------GUI Element-----
    private ProgressBar dailyGoalProgressBar;
    private Button pickDailyGoalButton;
    private Button markGoalAsCompleteButton;
    private Button seeAllPastActivitiesButton;
    private TextView dailyGoalTxt;
    private Button questionnaireButton;
    private ImageButton littleBeingButton;
    private TextView stepCountTxt;
    private ArrayList<Object> elementsForLanguageChange = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private LinearLayout linearLayoutStepCount;
    private ImageView imageViewStepCount;

    //--------Context and Resources for Language----
    private Context context;
    private Resources resources;

    private Context contextForColor;
    //--------GUI Elements to add Dynamically-------------
    private LinearLayout linearLayout;
    private TextView txtViewDate;

    private LinearLayout linearLayoutToAdd;
    private ImageView imageViewIntervention;

    private TextView txtViewInterventions;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        questionnaireButton = view.findViewById(R.id.btHomeQuestionnaire);
        pickDailyGoalButton = view.findViewById(R.id.btHomeDailyGoal);
        markGoalAsCompleteButton = view.findViewById(R.id.btHomeGoalComplete);
        dailyGoalTxt = view.findViewById(R.id.DailyGoalTxt);
        dailyGoalProgressBar = view.findViewById(R.id.DailyGoalProgressBar);
        littleBeingButton = view.findViewById(R.id.buttonLittleBeing);
        seeAllPastActivitiesButton = view.findViewById(R.id.SeeAllPastActivitiesButton);
        stepCountTxt = view.findViewById(R.id.textViewStepCount);

        constraintLayout = view.findViewById(R.id.homeFragment);
        linearLayoutStepCount = view.findViewById(R.id.linearLayoutStepCount);
        imageViewStepCount = view.findViewById(R.id.imageViewStepCount);
        linearLayout = view.findViewById(R.id.linearLayoutHomeRecentActivities);


        elementsForLanguageChange.add(view.findViewById(R.id.tvHomeTitle));
        elementsForLanguageChange.add(view.findViewById(R.id.btHomeQuestionnaire));
        elementsForLanguageChange.add(view.findViewById(R.id.btHomeDailyGoal));
        elementsForLanguageChange.add(view.findViewById(R.id.btHomeGoalComplete));
        elementsForLanguageChange.add(view.findViewById(R.id.SeeAllPastActivitiesButton));
        elementsForLanguageChange.add(view.findViewById(R.id.textViewStepCount));
        elementsForLanguageChange.add(view.findViewById(R.id.tv_recent_interventions));

        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        sensor_to_read = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        setColorTheme();

        // set text according to the language
        refreshResources();
        setLanguage();
        //Retrieve Daily Goal (If already chosen) from Storage
        dsHelper = DataSavingHelper.getInstance();
        getDailyGoalFromStorage();
        convertDailyGoal();
        setUpProgress();
        updateLittleBeing();

        getPastInterventions();
        txtViewDate = new TextView(getActivity());
        txtViewDate.setTextSize(20);
        txtViewDate.setGravity(Gravity.CENTER);
        linearLayout.addView(txtViewDate);

        boolean timeslotDoesNotAlreadyExists = !TimeStamp.timeSlotExistsAlready(getContext());
        boolean timeslotCorrect =  TimeStamp.getCurrentTimeSlot() != TimeStamp.TimeSlot.no_TimeSLot;
        if(timeslotDoesNotAlreadyExists && timeslotCorrect) {
            questionnaireButton.setEnabled(true);
        }else{
            questionnaireButton.setEnabled(false);
        }

        questionnaireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), QuestionnaireActivity.class);
                startActivity(myIntent);
            }
        });

        pickDailyGoalButton.setOnClickListener(new View.OnClickListener()
        {

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v)
            {

                if(Integer.parseInt(Build.VERSION.SDK) > 28) {
                    if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, 0);
                    } else {
                        Intent myIntent = new Intent(getActivity(),DailyTaskActivity.class);
                        startActivity(myIntent);
                    }
                } else {
                    Intent myIntent = new Intent(getActivity(),DailyTaskActivity.class);
                    startActivity(myIntent);
                }
            }
        });
        markGoalAsCompleteButton.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                goalProgress = 100;
                markGoalAsComplete();

            }
        });
        littleBeingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(overallWellBeing >=0 && overallWellBeing <= 33) {
                    Toast.makeText(getActivity(),resources.getString(R.string.toast_little_being_sad), Toast.LENGTH_SHORT).show();
                }
                else if(overallWellBeing >=33 && overallWellBeing<66)
                {
                    Toast.makeText(getActivity(),resources.getString(R.string.toast_little_being_okay), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(),resources.getString(R.string.toast_little_being_happy), Toast.LENGTH_SHORT).show();
                }
            }
        });
        seeAllPastActivitiesButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent myIntent = new Intent(getActivity(),AllPastInterventionsActivity.class);
                startActivity(myIntent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }



    //----------------Helper Functions---------------------------

    private void getDailyGoalFromStorage()
    {
        //Retrieve Daily Goal (If already chosen) from Storage
        todaysDate = TimeStamp.getCurrentTimeStamp().split(" ")[0];

        dsHelper = DataSavingHelper.getInstance();
        try
        {
            String temp = dsHelper.readSavedDataFromInternalStorage("DailyGoal.txt",getActivity());
            String[] temp2 = temp.split("\n");
            temp = temp2[temp2.length-1];
            JsonObject obj = new JsonParser().parse(temp).getAsJsonObject();
            date = obj.get("Date").getAsString().split(" ")[0];

            if(date.equals(todaysDate))
            {
                dailyGoal = obj.get("Daily_Task").getAsString();
                goalProgress = obj.get("Progress").getAsInt();
                pickDailyGoalButton.setEnabled(false);
                if(dailyGoal.equals("Walk 5.000 Steps")||dailyGoal.equals("Walk 10.000 Steps"))
                {
                    markGoalAsCompleteButton.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                dailyGoal = resources.getString(R.string.tv_home_goal_status_not_picked);
                goalProgress = 0;
                markGoalAsCompleteButton.setEnabled(false);
            }

        }catch(Exception e){
            dailyGoal = resources.getString(R.string.tv_home_goal_status_not_picked);
            goalProgress = 0;
            markGoalAsCompleteButton.setEnabled(false);
        }
    }

    private void setUpProgress()
    {
        if(goalProgress==100)
        {
            markGoalAsCompleteButton.setVisibility(View.INVISIBLE);
            dailyGoalTxt.setText(resources.getString(R.string.tv_home_goal_status_achieved));
        }
        else{
            dailyGoalTxt.setText(resources.getString(R.string.tv_home_goal_status_not_achieved) + " " + dailyGoal);
        }
        dailyGoalProgressBar.setProgress((int)goalProgress);
    }
    private void updateProgress()
    {
        dailyGoalProgressBar.setProgress((int)goalProgress);
    }

    private void updateLittleBeing()
    {
        try
        {
            String[] questionnaires = dsHelper.readSavedDataFromInternalStorage("questionnaire.txt",getActivity()).split("\n");
            String questionnaire = questionnaires[questionnaires.length-1];
            try {
                overallWellBeing = DataAccessHelper.calculateHealthScoreFromQuestionaire(questionnaire);
                if(goalProgress==100)
                {
                    overallWellBeing+=33;
                    Toast.makeText(getActivity(),resources.getString(R.string.toast_daily_goal_achieved),Toast.LENGTH_SHORT).show();
                }
                else if(getNumberOfTodaysQuestionnaires() == 3)
                {
                    overallWellBeing+=33;
                    Toast.makeText(getActivity(),resources.getString(R.string.toast_three_questionnaires_answered),Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        catch (Exception e)
        {
            overallWellBeing = 100;
        }

        if(overallWellBeing >= 0 && overallWellBeing < 33) {
            littleBeingButton.setBackground(getResources().getDrawable(R.drawable.little_being_sad));
        }
        else if(overallWellBeing >= 33 && overallWellBeing < 66)
        {
            littleBeingButton.setBackground(getResources().getDrawable(R.drawable.little_being_okay));
        }
        else
        {
            littleBeingButton.setBackground(getResources().getDrawable(R.drawable.little_being_happy));
        }
    }

    private void markGoalAsComplete() {
        updateProgress();
        updateLittleBeing();
        markGoalAsCompleteButton.setVisibility(View.INVISIBLE);
        dailyGoalTxt.setText(resources.getString(R.string.tv_home_goal_status_achieved));
        todaysDate = TimeStamp.getCurrentTimeStamp();
        dsHelper = DataSavingHelper.getInstance();
        String temp = dsHelper.readSavedDataFromInternalStorage("DailyGoal.txt",getActivity());
        String[] temp2 = temp.split("\n");

        temp = temp2[temp2.length-1];
        try
        {
            JSONObject obj = new JSONObject(temp);
            obj.put("Progress", 100);
            String data = obj + "\n";
            temp2[temp2.length-1] = data;
            String toSave = "";
            for(int i = 1; i<temp2.length-1;i++)
            {
                toSave += temp2[i] + "\n";
            }
            toSave += temp2[temp2.length-1];
            dsHelper.writeDataToInternalStorage("DailyGoal.txt",toSave,getActivity());
            ServerHelper sHelper = ServerHelper.getInstance();
            sHelper.setActivity(getActivity());
            sHelper.synchronize(getContext());

        }
        catch(JSONException e){
            throw new RuntimeException(e);
        }
    }


    private int getNumberOfTodaysQuestionnaires()
    {
        dataAccessHelper = DataAccessHelper.getInstance();
        try
        {
            String[] questionnaires = dsHelper.readSavedDataFromInternalStorage("questionnaire.txt", getActivity()).split("\n");
            String temp;
            int counter = 0;
            for(int i = questionnaires.length-1; i>0; i--)
            {
                temp = questionnaires[i];
                JsonObject obj = new JsonParser().parse(temp).getAsJsonObject();
                date = obj.get("timestamp").getAsString().split(" ")[0];
                if(date.equals(todaysDate)) counter++;
                else break;
            }
            return counter;
        }
        catch(Exception e)
        {
            return 0;
        }
    }




    //---------------Sensor Functions ---------------------

    private void saveInitialStepCount()
    {
        String now = TimeStamp.getCurrentTimeStamp();
        try
        {
            //Retrieve Count
            String temp = dsHelper.readSavedDataFromInternalStorage("TodaysStepCount.txt",getActivity());
            JsonObject obj = new JsonParser().parse(temp).getAsJsonObject();
            date = obj.get("Date").getAsString().split(" ")[0];
            if(todaysDate.equals(date)) innitStepCount = obj.get("StepCount").getAsInt();
            else throw new Exception();
        }
        catch(Exception e)
        {

            JSONObject obj = new JSONObject();
            try {
                obj.put("Date",now);
                obj.put("StepCount",newStepCount);
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
            String data = obj.toString();
            //Save Count for the first time
            dsHelper.writeDataToInternalStorage("TodaysStepCount.txt",data,getActivity(),true);
            innitStepCount = newStepCount;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null)
        {
            sensorManager.registerListener(this, sensor_to_read,SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
        running = false;
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        if(running)
        {

            newStepCount = (int) sensorEvent.values[0];
            if(innitStepCount == 0)  saveInitialStepCount();
            //The first time we start the app we save the current step count so far
            float steps = newStepCount - innitStepCount;
            stepCountTxt.setText(resources.getString(R.string.tv_home_step_count) +"\n" +  String.valueOf((int) steps));
            if(dailyGoal.equals("Walk 5.000 Steps"))
            {
                goalProgress = (steps/5000)*100;
                updateProgress();

            }
            else if(dailyGoal.equals("Walk 10.000 Steps"))
            {
                goalProgress = (steps/10000)*100;
                updateProgress();

            }
            if(goalProgress == 100)
            {
                markGoalAsComplete();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void refreshResources() {

        String language = DataSavingHelper.getInstance().getSelectedLanguage(getActivity().getApplicationContext());

        String language_short = "en";

        switch(language) {
            case "francais":
                language_short = "fr";
                break;
            case "deutsch":
                language_short = "de";
                break;
        }
        context = LocaleHelper.setLocale(getActivity().getApplicationContext(), language_short);
        resources = context.getResources();
    }
    private void setLanguage() {
        TextView tv = (TextView) elementsForLanguageChange.get(0);
        tv.setText(resources.getString(R.string.tv_home_title));
        Button bt = (Button) elementsForLanguageChange.get(1);
        bt.setText(resources.getString(R.string.bt_home_questionnaire));
        bt = (Button) elementsForLanguageChange.get(2);
        bt.setText(resources.getString(R.string.bt_home_daily_goal));
        bt = (Button) elementsForLanguageChange.get(3);
        bt.setText(resources.getString(R.string.bt_home_goal_complete));
        bt = (Button) elementsForLanguageChange.get(4);
        bt.setText(resources.getString(R.string.bt_home_all_past_activities));
        tv = (TextView) elementsForLanguageChange.get(5);
        tv.setText(resources.getString(R.string.tv_home_step_count));
        tv = (TextView) elementsForLanguageChange.get(6);
        tv.setText(resources.getString(R.string.tv_recent_interventions));

    }


    private void convertDailyGoal()
    {
        innitdailyGoal = dailyGoal;
        switch(dailyGoal) {
            case "Walk 5.000 Steps":
                dailyGoal = resources.getString(R.string.goal_walk_5000_steps);
                break;
            case "Walk 10.000 Steps":
                dailyGoal = resources.getString(R.string.goal_walk_10000_steps);
                break;
            case "Drink 2L Water":
                dailyGoal = resources.getString(R.string.goal_drink_water);
                break;
            case "Clean":
                dailyGoal = resources.getString(R.string.goal_clean);
                break;
            case "Go for a Walk":
                dailyGoal = resources.getString(R.string.goal_go_for_a_walk);
                break;
            case "Meditate":
                dailyGoal = resources.getString(R.string.goal_meditate);
                break;
            case "Do a Workout":
                dailyGoal = resources.getString(R.string.goal_do_a_workout);
                break;
        }
    }


    //----------Color Functions ---------------------
    private void setColorTheme()
    {
        int ButtonTransparency = 50;
        contextForColor = getActivity().getApplicationContext();
        String mode = dsHelper.getInstance().getSpecificSettingsFromSharedPref("dmValue",contextForColor);
        String color = dsHelper.getInstance().getSpecificSettingsFromSharedPref("color",contextForColor);
        switch(mode)
        {
            case "dark-mode":
                ButtonTransparency = 70;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                imageViewStepCount.setImageResource(R.drawable.baseline_directions_walk_24_dark_mode);
                linearLayoutStepCount.setBackground(getResources().getDrawable(R.drawable.step_count_border));

                //TODO Do the same thing for the dynamic Interventions when you add them
                switch (color)
                {
                    case "blue":
                        constraintLayout.setBackgroundResource(R.drawable.blue_dark_mode_background);
                        dailyGoalProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_background));
                        break;
                    case "red":
                        constraintLayout.setBackgroundResource(R.drawable.red_dark_mode_background);
                        dailyGoalProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_background_dark_mode_red));
                        break;
                    case "green":
                        constraintLayout.setBackgroundResource(R.drawable.green_dark_mode_background);
                        dailyGoalProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_background_light_mode_green));
                        break;
                }
                break;
            case "white-mode":
                ButtonTransparency = 150;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                imageViewStepCount.setImageResource(R.drawable.baseline_directions_walk_24);
                linearLayoutStepCount.setBackground(getResources().getDrawable(R.drawable.step_count_border_light_mode));
                switch (color)
                {
                    case "blue":
                        constraintLayout.setBackgroundResource(R.drawable.blue_light_mode_background);
                        dailyGoalProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_background_light_mode_blue));
                        break;
                    case "red":
                        constraintLayout.setBackgroundResource(R.drawable.red_light_mode_background);
                        dailyGoalProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_background_light_mode_red));
                        break;
                    case "green":
                        constraintLayout.setBackgroundResource(R.drawable.green_light_mode_background);
                        dailyGoalProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_background_light_mode_green));
                        break;
                }
                break;
        }
        questionnaireButton.getBackground().setAlpha(ButtonTransparency);
        seeAllPastActivitiesButton.getBackground().setAlpha(ButtonTransparency);
        pickDailyGoalButton.getBackground().setAlpha(ButtonTransparency);
        markGoalAsCompleteButton.getBackground().setAlpha(ButtonTransparency);
        linearLayoutStepCount.setPadding(100,50,100,50);

    }





    //--------------Display Last Interventions--------------------



    private void getPastInterventions()
    {
        try {
            interventionList = dsHelper.getSavedInterventions(this.getContext());
        } catch (JSONException ignored) {}
        //-----Variables----------
        JsonObject obj = new JsonObject();
        JsonObject obj2 = new JsonObject();
        JsonParser parser = new JsonParser();
        String date = " ";
        String currentDate;
        String id;
        String[] time;
        String type;


        String singleIntervention;
        String singleRecording;
        int interventionCounter;
        int recordingCounter;
        String[] allInterventions;
        String[] allRecordings;
        dsHelper = DataSavingHelper.getInstance();

        try {
            String temp = dsHelper.readSavedDataFromInternalStorage("completedInterventions.txt",getActivity());
            allInterventions = temp.split("\n");
            interventionCounter = allInterventions.length-1;

        }
        catch(Exception e){
            interventionCounter = 0;
            allInterventions = new String[]{" "};
        }

        try {
            String temp2 = dsHelper.readSavedDataFromInternalStorage("sensorrecordings.txt", getActivity());
            allRecordings = temp2.split("\n");
            recordingCounter = allRecordings.length-1;
        }
        catch(Exception e)
        {
            recordingCounter = 0;
            allRecordings = new String[]{" "};
        }

        for(int i = 0; i<5; i++) {

            boolean isBefore;
            if (interventionCounter >= 1 && recordingCounter >= 1) {

                //Both Recordings and Interventions exists
                singleIntervention = allInterventions[interventionCounter];
                obj = parser.parse(singleIntervention).getAsJsonObject();
                singleRecording = allRecordings[recordingCounter];
                obj2 = parser.parse(singleRecording).getAsJsonObject();

                //--------------------Check Date-------------------------------
                String startInput = obj.get("timestamp").getAsString();
                String stopInput = obj2.get("timestamp").getAsString();
                if(startInput.equals(stopInput))
                {
                    isBefore = false;
                    recordingCounter--;
                }
                else
                {
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
                    LocalDateTime start = LocalDateTime.parse(startInput, f);
                    LocalDateTime stop = LocalDateTime.parse(stopInput, f);
                    isBefore = start.isBefore(stop);
                }

                //---------------------------------------------------
            }
            else if(recordingCounter < 1 && interventionCounter >= 1)
            {
                //No Recordings exists but interventions do
                singleIntervention = allInterventions[interventionCounter];
                obj = parser.parse(singleIntervention).getAsJsonObject();
                isBefore = false;
            }
            else if(recordingCounter >= 1 && interventionCounter < 1)
            {
                //No interventions exists but recordings do
                singleRecording = allRecordings[recordingCounter];
                obj2 = parser.parse(singleRecording).getAsJsonObject();
                isBefore = true;

            }
            else
            {
                //None of them exists
                break;
            }


            if (!isBefore) {
                currentDate = obj.get("timestamp").getAsString().split(" ")[0];
                time = obj.get("timestamp").getAsString().split(" ")[1].split(":");
                type = "Intervention";
                id = String.valueOf(obj.get("id").getAsInt());
                interventionCounter--;
            }
            else if (recordingCounter >= 1) {
                currentDate = obj2.get("timestamp").getAsString().split(" ")[0];
                time = obj2.get("timestamp").getAsString().split(" ")[1].split(":");
                type = "Recording";
                recordingCounter--;
                id = "0";
            }
            else {
                break;
            }

            if (!date.equals(currentDate)) {
                date = currentDate;
                addTextView(currentDate);
            }

            addInterventionView(id, time, date, type);
        }
    }
    private void addTextView(String date)
    {
        txtViewDate = new TextView(getActivity());
        txtViewDate.setTextSize(20);
        txtViewDate.setGravity(Gravity.CENTER);
        txtViewDate.setText(date);
        linearLayout.addView(txtViewDate);
    }

    @SuppressLint("SetTextI18n")
    private void addInterventionView(String id, String[] time, String date, String type)
    {

        contextForColor = getActivity().getApplicationContext();
        String mode = dsHelper.getInstance().getSpecificSettingsFromSharedPref("dmValue",contextForColor);

        linearLayoutToAdd = new LinearLayout(getActivity());
        linearLayoutToAdd.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayoutToAdd.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutToAdd.setGravity(Gravity.LEFT);
        linearLayoutToAdd.setDividerDrawable(getResources().getDrawable(R.drawable.divider_big));
        linearLayoutToAdd.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        if(mode.equals("white-mode")) linearLayoutToAdd.setBackground(getResources().getDrawable(R.drawable.step_count_border_light_mode));
        else linearLayoutToAdd.setBackground(getResources().getDrawable(R.drawable.step_count_border));


        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER;
        imageViewIntervention = new ImageView(getActivity());
        imageViewIntervention.setLayoutParams(layoutParams);
        //If else with different Picture
        if(type.equals("Recording")) {
            if(mode.equals("white-mode")) imageViewIntervention.setImageResource((R.drawable.baseline_sensors_24));
            else imageViewIntervention.setImageResource((R.drawable.baseline_sensors_24_dark_mode));
        }
        else
        {
            if(mode.equals("white-mode")) imageViewIntervention.setImageResource(R.drawable.baseline_directions_run_24);
            else imageViewIntervention.setImageResource(R.drawable.baseline_directions_run_24_dark_mode);
        }
        imageViewIntervention.setForegroundGravity(Gravity.CENTER);
        linearLayoutToAdd.addView(imageViewIntervention);

        txtViewInterventions = new TextView(getActivity());
        txtViewInterventions.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));



        //If else with different Text
        if(type.equals("Recording")) {

            txtViewInterventions.setText(resources.getString(R.string.Own_Recording) +  "\n" + resources.getString(R.string.Time) + ": " + time[0] + ":" + time[1]);
        }
        else
        {
            String name = interventionList.get(Integer.valueOf(id)).get(1);
            txtViewInterventions.setText(name + "\n" + resources.getString(R.string.Time) + ": " + time[0] + ":" + time[1]);
        }
        txtViewInterventions.setTextSize(20);
        linearLayoutToAdd.addView(txtViewInterventions);

        linearLayoutToAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("date", date);
                b.putStringArray("time", time);
                Intent intent = new Intent(getActivity(), SensorRecordingGraphActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        linearLayout.addView(linearLayoutToAdd);
    }

}