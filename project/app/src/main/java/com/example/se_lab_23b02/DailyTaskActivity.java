package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DailyTaskActivity extends AppCompatActivity implements View.OnClickListener{

    //------------GUI Elements-----------------
    Button buttonX;
    Button buttonWalk5000Steps;
    Button buttonWalk10000Steps;
    Button buttonDrinkWater;
    Button buttonClean;
    Button buttonGoForAWalk;
    Button buttonMeditate;
    Button buttonWorkout;
    Button buttonOwnDailyTask;
    Button buttonSaveDailyTask;
    TextView txtChosenDailyGoal;

    //-------------Variables---------------------
    String dailyTask;


    //---------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_task);

        buttonX = findViewById(R.id.buttonX);
        buttonWalk5000Steps = findViewById(R.id.buttonWalk5000Steps);
        buttonWalk10000Steps = findViewById(R.id.buttonWalk10000Steps);
        buttonDrinkWater = findViewById(R.id.buttonDrinkWater);
        buttonClean = findViewById(R.id.buttonClean);
        buttonGoForAWalk = findViewById(R.id.buttonGoForAWalk);
        buttonMeditate = findViewById(R.id.buttonMeditate);
        buttonWorkout = findViewById(R.id.buttonWorkout);
        buttonOwnDailyTask = findViewById(R.id.buttonOwnDailyGoal);
        buttonSaveDailyTask = findViewById(R.id.buttonSaveDailyTask);
        txtChosenDailyGoal = findViewById(R.id.txtChosenDailyGoal);

        buttonX.setOnClickListener(this);
        buttonWalk5000Steps.setOnClickListener(this);
        buttonWalk10000Steps.setOnClickListener(this);
        buttonDrinkWater.setOnClickListener(this);
        buttonClean.setOnClickListener(this);
        buttonGoForAWalk.setOnClickListener(this);
        buttonMeditate.setOnClickListener(this);
        buttonWorkout.setOnClickListener(this);
        buttonOwnDailyTask.setOnClickListener(this);
        buttonSaveDailyTask.setOnClickListener(this);

        buttonSaveDailyTask.setEnabled(false);
    }

    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.buttonX:
                finish();
                break;
            case R.id.buttonWalk5000Steps:
                dailyTask = "Walk 5000 Steps";
                break;
            case R.id.buttonWalk10000Steps:
                dailyTask = "Walk 10000 Steps";
                break;
            case R.id.buttonDrinkWater:
                dailyTask = "Drink 2L Water";
                break;
            case R.id.buttonClean:
                dailyTask = "Clean";
                break;
            case R.id.buttonGoForAWalk:
                dailyTask = "Gor For A Walk";
                break;
            case R.id.buttonMeditate:
                dailyTask = "Meditate";
                break;
            case R.id.buttonWorkout:
                dailyTask = "Workout";
                break;
            case R.id.buttonOwnDailyGoal:
                dailyTask = "OwnDailyGoal";
                //Todo Open Window to Enter Daily Goal
                break;
        }
        txtChosenDailyGoal.setText(dailyTask);
    }
}