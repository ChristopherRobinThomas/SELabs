package com.example.se_lab_23b02;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    //---------Variables--------
    private String dailyGoal;

    private String todaysDate;
    private String date;

    private float goalProgress;
    private boolean running = false;
    private int innitStepCount;
    private int newStepCount;
    private int overallWellBeing = getOverallWellbeing();

    //--------GUI Element-----
    ProgressBar dailyGoalProgressBar;
    Button pickDailyGoalButton;
    Button markGoalAsCompleteButton;
    TextView dailyGoalTxt;
    Button questionnaireButton;
    ImageButton littleBeingButton;


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
        questionnaireButton = view.findViewById(R.id.QuestionnaireButton);
        pickDailyGoalButton = view.findViewById(R.id.DailyGoalButton);
        markGoalAsCompleteButton = view.findViewById(R.id.GoalCompleteButton);
        dailyGoalTxt = view.findViewById(R.id.DailyGoalTxt);
        dailyGoalProgressBar = view.findViewById(R.id.DailyGoalProgressBar);


        littleBeingButton = view.findViewById(R.id.buttonLittleBeing);

        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        sensor_to_read = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //Retrieve Daily Goal (If already chosen) from Storage
        dsHelper = DataSavingHelper.getInstance();
        getDailyGoalFromStorage();
        setUpProgress();

        questionnaireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), QuestionnaireActivity.class);
                startActivity(myIntent);
            }
        });
        pickDailyGoalButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent myIntent = new Intent(getActivity(),DailyTaskActivity.class);
                startActivity(myIntent);
            }
        });

        markGoalAsCompleteButton.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                goalProgress = 100;
                updateProgress();
                markGoalAsCompleteButton.setVisibility(View.INVISIBLE);
                dailyGoalTxt.setText("You achieved today's goal!");

                //Save Progress Update to Storage
                JSONObject obj = new JSONObject();
                try {
                    obj.put("Date", todaysDate);
                    obj.put("Daily_Task", dailyGoal);
                    obj.put("Progress", 100);
                    obj.put("Step_Count", innitStepCount);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                String data = obj.toString();

                dsHelper.writeDataToInternalStorage("DailyGoal.txt",data,getActivity());

            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    //----------------Helper Functions---------------------------

    private void getDailyGoalFromStorage()
    {
        //Retrieve Daily Goal (If already chosen) from Storage
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        todaysDate = sdf.format(c.getTime());

        dsHelper = DataSavingHelper.getInstance();
        try
        {
            String temp = dsHelper.readSavedDataFromInternalStorage("DailyGoal.txt",getActivity());
            JsonObject obj = new JsonParser().parse(temp).getAsJsonObject();
            date = obj.get("Date").getAsString();

            if(date.equals(todaysDate))
            {
                dailyGoal = obj.get("Daily_Task").getAsString();
                goalProgress = obj.get("Progress").getAsInt();
                innitStepCount = obj.get("Step_Count").getAsInt();
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
        //ToDO get overall wellness and set Little Being according to it


        /**
         * if(overallWellBeing <0 && overallWellbeing <?)
         * littleBeingButton.setBackground(getResources().getDrawable(R.drawable.little_being_happy));
         * */
    }

    private int getOverallWellbeing()
    {
        //TODO
        return 0;
    }



    //---------------Sensor Functions ---------------------


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
            if(dailyGoal.equals("Walk 5.000 Steps"))
            {
                float steps = newStepCount - innitStepCount;

                goalProgress = (steps/5000)*100;
                updateProgress();
            }
            else if(dailyGoal.equals("Walk 10.000 Steps"))
            {
                float steps = newStepCount - innitStepCount;
                goalProgress = (steps/10000)*100;
                updateProgress();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}