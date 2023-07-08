package com.example.se_lab_23b02;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

public class DailyTaskActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    //------------GUI Elements-----------------
    private Button buttonX;
    private Button buttonWalk5000Steps;
    private Button buttonWalk10000Steps;
    private Button buttonDrinkWater;
    private Button buttonClean;
    private Button buttonGoForAWalk;
    private Button buttonMeditate;
    private Button buttonWorkout;
    private Button buttonOwnDailyTask;
    private Button buttonSaveDailyTask;
    private TextView txtChosenDailyGoal;

    private ArrayList<Object> elementsForLanguageChange = new ArrayList<>();

    //-------------Variables---------------------
    private String dailyTask;

    private String saveDailyTask;
    private int stepCount;
    private boolean running = false;

    //--------------Objects----------------------
    private DataSavingHelper dsHelper;
    private SensorManager sensorManager;
    private Sensor sensor_to_read;


    //--------Context and Resources for Language----
    private Context context;
    private Resources resources;

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


        elementsForLanguageChange.add(findViewById(R.id.pickDailyGoalTxt));
        elementsForLanguageChange.add(findViewById(R.id.buttonWalk5000Steps));
        elementsForLanguageChange.add(findViewById(R.id.buttonWalk10000Steps));
        elementsForLanguageChange.add(findViewById(R.id.buttonDrinkWater));
        elementsForLanguageChange.add(findViewById(R.id.buttonClean));
        elementsForLanguageChange.add(findViewById(R.id.buttonGoForAWalk));
        elementsForLanguageChange.add(findViewById(R.id.buttonMeditate));
        elementsForLanguageChange.add(findViewById(R.id.buttonWorkout));
        elementsForLanguageChange.add(findViewById(R.id.buttonOwnDailyGoal));
        elementsForLanguageChange.add(findViewById(R.id.txtChosenDailyGoal));
        elementsForLanguageChange.add(findViewById(R.id.buttonSaveDailyTask));

        // set text according to the language
        refreshResources();
        setLanguage();

        buttonSaveDailyTask.setEnabled(false);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor_to_read = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
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
                saveDailyTask = "Walk 5000 Steps";
                dailyTask = resources.getString(R.string.goal_walk_5000_steps);
                break;
            case R.id.buttonWalk10000Steps:
                saveDailyTask = "Walk 10000 Steps";
                dailyTask = resources.getString(R.string.goal_walk_10000_steps);
                break;
            case R.id.buttonDrinkWater:
                saveDailyTask = "Drink 2L Water";
                dailyTask = resources.getString(R.string.goal_drink_water);
                break;
            case R.id.buttonClean:
                saveDailyTask = "Clean";
                dailyTask = resources.getString(R.string.goal_clean);
                break;
            case R.id.buttonGoForAWalk:
                saveDailyTask = "Go for a Walk";
                dailyTask = resources.getString(R.string.goal_go_for_a_walk);
                break;
            case R.id.buttonMeditate:
                saveDailyTask = "Meditate";
                dailyTask = resources.getString(R.string.goal_meditate);
                break;
            case R.id.buttonWorkout:
                saveDailyTask = "Do a Workout";
                dailyTask = resources.getString(R.string.goal_do_a_workout);
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
                                saveDailyTask = dailyTask;
                                txtChosenDailyGoal.setText(resources.getString(R.string.tv_daily_goal_goal_choice)+ dailyTask);
                            }
                        })
                        .setNegativeButton(resources.getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
        }
        txtChosenDailyGoal.setText(resources.getString(R.string.tv_daily_goal_goal_choice) + dailyTask);
    }



    public void saveGoalButtonClicked(View v) throws JSONException {

        String now = TimeStamp.getCurrentTimeStamp();

        //Save Date, Task, Progress, and Step Count
        JSONObject obj = new JSONObject();
        obj.put("Date", now);
        obj.put("Daily_Task", saveDailyTask);
        obj.put("Progress", 0);

        String data = obj.toString() + "\n";

        dsHelper.writeDataToInternalStorage("DailyGoal.txt",data,getApplicationContext(), true);

        Intent myIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(myIntent);
        finish();
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
            stepCount = (int) sensorEvent.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }




    //---------------Language Functions----------------

    private void refreshResources() {

        String language = DataSavingHelper.getInstance().getSelectedLanguage(getApplicationContext());

        String language_short = "en";

        switch(language) {
            case "francais":
                language_short = "fr";
                break;
            case "deutsch":
                language_short = "de";
                break;
        }
        context = LocaleHelper.setLocale(getApplicationContext(), language_short);
        resources = context.getResources();
    }
    private void setLanguage() {

        TextView tv = (TextView) elementsForLanguageChange.get(0);
        tv.setText(resources.getString(R.string.tv_daily_goal_pick_daily_goal));
        Button bt = (Button) elementsForLanguageChange.get(1);
        bt.setText(resources.getString(R.string.goal_walk_5000_steps));
        bt = (Button) elementsForLanguageChange.get(2);
        bt.setText(resources.getString(R.string.goal_walk_10000_steps));
        bt = (Button) elementsForLanguageChange.get(3);
        bt.setText(resources.getString(R.string.goal_drink_water));
        bt = (Button) elementsForLanguageChange.get(4);
        bt.setText(resources.getString(R.string.goal_clean));
        bt = (Button) elementsForLanguageChange.get(5);
        bt.setText(resources.getString(R.string.goal_go_for_a_walk));
        bt = (Button) elementsForLanguageChange.get(6);
        bt.setText(resources.getString(R.string.goal_meditate));
        bt = (Button) elementsForLanguageChange.get(7);
        bt.setText(resources.getString(R.string.goal_do_a_workout));
        bt = (Button) elementsForLanguageChange.get(8);
        bt.setText(resources.getString(R.string.goal_own_daily_goal));
        tv = (TextView) elementsForLanguageChange.get(9);
        tv.setText(resources.getString(R.string.tv_daily_goal_goal_choice));
        bt = (Button) elementsForLanguageChange.get(10);
        bt.setText(resources.getString(R.string.bt_daily_goal_save_daily_goal));

    }
}