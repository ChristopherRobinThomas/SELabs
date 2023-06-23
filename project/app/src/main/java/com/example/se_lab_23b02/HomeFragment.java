package com.example.se_lab_23b02;

import static android.content.Context.SENSOR_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.system.Os;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    //---------Variables--------
    private String dailyGoal;
    private String todaysDate;
    private String date;

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

    //--------Context and Resources for Language----
    private Context context;
    private Resources resources;

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

        elementsForLanguageChange.add(view.findViewById(R.id.tvHomeTitle));
        elementsForLanguageChange.add(view.findViewById(R.id.btHomeQuestionnaire));
        elementsForLanguageChange.add(view.findViewById(R.id.btHomeDailyGoal));
        elementsForLanguageChange.add(view.findViewById(R.id.btHomeGoalComplete));

        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        sensor_to_read = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //Retrieve Daily Goal (If already chosen) from Storage
        dsHelper = DataSavingHelper.getInstance();
        getDailyGoalFromStorage();
        setUpProgress();
        updateLittleBeing();

        // set text according to the language
        refreshResources();
        setLanguage();

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

                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, 0);
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
                    Toast.makeText(getActivity(),"Larry is feeling sad right now. That's okay!", Toast.LENGTH_SHORT).show();
                }
                else if(overallWellBeing >=33 && overallWellBeing<66)
                {
                    Toast.makeText(getActivity(),"Larry is feeling okay at the moment", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(),"Larry is feeling really happy right now!", Toast.LENGTH_SHORT).show();
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
                dailyGoal = "No Goal chosen yet";
                goalProgress = 0;
                markGoalAsCompleteButton.setEnabled(false);
            }

        }catch(Exception e){
            dailyGoal = "No Goal chosen yet";
            goalProgress = 0;
            markGoalAsCompleteButton.setEnabled(false);
        }
    }

    private void setUpProgress()
    {
        if(goalProgress==100)
        {
            markGoalAsCompleteButton.setVisibility(View.INVISIBLE);
            dailyGoalTxt.setText("You achieved today's goal!");
        }
        else{
            dailyGoalTxt.setText("Today's Goal: "+ dailyGoal);
        }
        dailyGoalProgressBar.setProgress((int)goalProgress);
    }
    private void updateProgress()
    {
        dailyGoalProgressBar.setProgress((int)goalProgress);
    }

    private void updateLittleBeing()
    {

        dataAccessHelper = DataAccessHelper.getInstance();
        try
        {
            String[] questionnaires = dsHelper.readSavedDataFromInternalStorage("questionnaire.txt",getActivity()).split("\n");
            String questionnaire = questionnaires[questionnaires.length-1];
            try {
                overallWellBeing = dataAccessHelper.calculateHealthScoreFromQuestionaire(questionnaire);
                if(goalProgress==100)
                {
                    overallWellBeing+=34;
                    Toast.makeText(getActivity(),"You've completed your daily goal. That makes Larry a bit happier!",Toast.LENGTH_SHORT).show();
                }
                else if(getNumberOfTodaysQuestionnaires() == 3)
                {
                    overallWellBeing+=34;
                    Toast.makeText(getActivity(),"You've filled out 3 Questionnaires. That makes Larry a bit happier!",Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        catch (Exception e)
        {
            overallWellBeing = 100;
        }

        if(overallWellBeing >= 0 && overallWellBeing <= 33) {
            littleBeingButton.setBackground(getResources().getDrawable(R.drawable.little_being_sad));
        }
        else if(overallWellBeing > 33 && overallWellBeing <= 66)
        {
            littleBeingButton.setBackground(getResources().getDrawable(R.drawable.little_being_okay));
        }
        else
        {
            littleBeingButton.setBackground(getResources().getDrawable(R.drawable.little_being_happy));
        }
    }

    private void markGoalAsComplete()
    {
        updateProgress();
        updateLittleBeing();
        markGoalAsCompleteButton.setVisibility(View.INVISIBLE);
        dailyGoalTxt.setText("You achieved today's goal!");
        todaysDate = TimeStamp.getCurrentTimeStamp();
        dsHelper = DataSavingHelper.getInstance();
        String temp = dsHelper.readSavedDataFromInternalStorage("DailyGoal.txt",getActivity());
        String[] temp2 = temp.split("\n");
        //Save Progress Update to Storage
        JSONObject obj = new JSONObject();
        try {
            obj.put("Date", todaysDate);
            obj.put("Daily_Task", dailyGoal);
            obj.put("Progress", 100);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String data = obj.toString() + "\n";
        dsHelper.writeDataToInternalStorage("DailyGoal.txt",data,getActivity(),true);
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
            stepCountTxt.setText("Today's Steps: \n" +  String.valueOf((int) steps));
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

    }

}