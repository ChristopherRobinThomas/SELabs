package com.example.se_lab_23b02;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Time;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //-------Objects--------------
    DataSavingHelper dsHelper;

    //---------Variables--------
    private String dailyGoal;
    int goalProgress;

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

        //Retrieve Daily Goal (If already chosen) from Storage
        dsHelper = DataSavingHelper.getInstance();
        try {
            String[] dailyGoalArray = dsHelper.readSavedDataFromInternalStorage("DailyGoal.txt",getActivity()).split("_");
            dailyGoal = dailyGoalArray[0];
            goalProgress = Integer.valueOf(dailyGoalArray[1]);
            pickDailyGoalButton.setEnabled(false);
        } catch (Exception e) {
            dailyGoal = "No Goal chosen yet";
            goalProgress = 0;
            markGoalAsCompleteButton.setEnabled(false);
        }
        if(goalProgress==100)
        {
            markGoalAsCompleteButton.setVisibility(View.INVISIBLE);
            dailyGoalTxt.setText("Congrats: You achieved your daily goal!");
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

        markGoalAsCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProgress();
                //markGoalAsCompleteButton.setEnabled(false);
                markGoalAsCompleteButton.setVisibility(View.INVISIBLE);
                dailyGoalTxt.setText("Congrats: You achieved your daily goal!");
                //Save Progress Update to Storage
                dsHelper.writeDataToInternalStorage("DailyGoal.txt",dailyGoal + "_" + String.valueOf(goalProgress),getActivity());
            }
        });


        // Inflate the layout for this fragment
        return view;
    }



    private void updateProgress()
    {
        //TODO Get step count and show in progress bar if goal is a certain step count
        goalProgress = 100;
        dailyGoalProgressBar.setProgress(goalProgress);
    }

}