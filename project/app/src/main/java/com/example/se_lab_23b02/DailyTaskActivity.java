package com.example.se_lab_23b02;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

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

    //--------------Objects----------------------
    DataSavingHelper dsHelper;

    //---------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_task);

        dsHelper = DataSavingHelper.getInstance();

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

        buttonSaveDailyTask.setEnabled(false);
    }

    public void onClick(View v)
    {
        buttonSaveDailyTask.setEnabled(true);
        switch(v.getId())
        {
            case R.id.buttonX:
                finish();
                break;
            case R.id.buttonWalk5000Steps:
                dailyTask = "Walk 5.000 Steps";
                break;
            case R.id.buttonWalk10000Steps:
                dailyTask = "Walk 10.000 Steps";
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

                LayoutInflater li = LayoutInflater.from(this);
                View promptView = li.inflate(R.layout.prompts,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setView(promptView);
                EditText userInput = (EditText) promptView.findViewById(R.id.editTxtUserInput);

                //Open Prompt
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dailyTask = String.valueOf(userInput.getText());
                                txtChosenDailyGoal.setText("Your choice: "+ dailyTask);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
        }
        txtChosenDailyGoal.setText("Your choice: " + dailyTask);
    }



    public void saveGoalButtonClicked(View v) throws JSONException {
        dsHelper.writeDataToInternalStorage("DailyGoal.txt",dailyTask + "_0",getApplicationContext());
        Intent myIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(myIntent);
    }


}