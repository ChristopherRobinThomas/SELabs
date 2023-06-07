package com.example.se_lab_23b02;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

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
    DataSavingHelper dsHelper;
    private SensorManager sensorManager;

    //---------Variables--------
    private String dailyGoal;
    private int goalProgress;
    private boolean running = false;
    int stepCount;

    //--------GUI Element-----
    ProgressBar dailyGoalProgressBar;


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
        Button questionnaireButton;
        Button pickDailyGoalButton;
        Button markGoalAsCompleteButton;
        TextView dailyGoalTxt;


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        questionnaireButton = view.findViewById(R.id.QuestionnaireButton);
        pickDailyGoalButton = view.findViewById(R.id.DailyGoalButton);
        markGoalAsCompleteButton = view.findViewById(R.id.GoalCompleteButton);
        dailyGoalTxt = view.findViewById(R.id.DailyGoalTxt);
        dailyGoalProgressBar = view.findViewById(R.id.DailyGoalProgressBar);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);


        //Retrieve Daily Goal (If already chosen) from Storage
        dsHelper = DataSavingHelper.getInstance();
        /**try {

            String[] dailyGoalArray = dsHelper.readSavedDataFromInternalStorage("DailyGoal.txt",getActivity()).split("_");
            dailyGoal = dailyGoalArray[0];
            goalProgress = Integer.valueOf(dailyGoalArray[1]);
            pickDailyGoalButton.setEnabled(false);
        } catch (Exception e) {
            dailyGoal = "No Goal chosen yet";
            goalProgress = 0;
            markGoalAsCompleteButton.setEnabled(false);
        }*/

        try
        {
            String temp = dsHelper.readSavedDataFromInternalStorage("DailyGoal.txt",getActivity());
            JsonObject obj = new JsonParser().parse(temp).getAsJsonObject();
            dailyGoal = obj.get("Daily_Task").getAsString();
            goalProgress = obj.get("Progress").getAsInt();
            pickDailyGoalButton.setEnabled(false);

        }catch(Exception e){
            dailyGoal = "No Goal chosen yet";
            goalProgress = 0;
            markGoalAsCompleteButton.setEnabled(false);
        }

        if(goalProgress==100)
        {
            markGoalAsCompleteButton.setVisibility(View.INVISIBLE);
            dailyGoalTxt.setText("You achieved today's goal!");
        }
        else{
            dailyGoalTxt.setText("Today's Goal: "+ dailyGoal);
        }
        dailyGoalProgressBar.setProgress(goalProgress);

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
                //markGoalAsCompleteButton.setEnabled(false);
                markGoalAsCompleteButton.setVisibility(View.INVISIBLE);
                dailyGoalTxt.setText("You achieved today's goal!");

                //Save Progress Update to Storage
                JSONObject obj = new JSONObject();
                try {
                    obj.put("Daily_Task", dailyGoal);
                    obj.put("Progress", 100);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                String data = obj.toString();

                dsHelper.writeDataToInternalStorage("DailyGoal.txt",data,getActivity());
                //dsHelper.writeDataToInternalStorage("DailyGoal.txt",dailyGoal + "_" + String.valueOf(goalProgress),getActivity());

            }
        });

        // Inflate the layout for this fragment
        return view;
    }



    private void updateProgress()
    {
        dailyGoalProgressBar.setProgress(goalProgress);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null)
        {
            sensorManager.registerListener(this, countSensor,SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        running = false;
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        if(running)
        {
            stepCount = (int) sensorEvent.values[0];
            if(dailyGoal.equals("Walk 5.000 Steps"))
            {
                stepCount += (goalProgress/100)*5000;
                goalProgress = (stepCount/5000)*100;
                updateProgress();
            }
            else if(dailyGoal.equals("Walk 10.000 Steps"))
            {
                stepCount += (goalProgress/100)*10000;
                goalProgress = (stepCount/10000)*100;
                updateProgress();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}