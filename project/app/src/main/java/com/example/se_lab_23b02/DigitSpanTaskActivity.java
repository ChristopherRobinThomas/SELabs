package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Random;

public class DigitSpanTaskActivity extends AppCompatActivity implements View.OnClickListener {

    //------Variables--------------------------------------------------------
    private int spanTaskCount;
    private int inputCount;
    private int spanLength;
    private int numberOfRounds;
    private int initSpanLength = 2;
    private int maxReachedNumbers = 0;

    private Context contextForColor;
    private ConstraintLayout constraintLayout;

    String[] spanNumbers;

    //------Objects--------------------------------------------------------
    Handler handler = new Handler();
    Random rand = new Random();
    Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
    DataSavingHelper dsHelper;
    ServerHelper sHelper;


    //------GUI Elements----------------------------------------------------
    TextView numberField;
    TextView inputField;
    Button startingButton;
    Button exitButton;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dsHelper = DataSavingHelper.getInstance();
        sHelper = ServerHelper.getInstance();
        setContentView(R.layout.activity_digit_span_task);
        numberOfRounds = 0;
        try {
            initSpanLength = Integer.parseInt(dsHelper.readSavedDataFromInternalStorage("DigitSpanTask.txt",getApplicationContext()));
        } catch (Exception e) {
            dsHelper.writeDataToInternalStorage("DigitSpanTask.txt",String.valueOf(2),getApplicationContext());
            initSpanLength = 2;
        }
        spanLength = initSpanLength;
        startingButton = findViewById(R.id.startButton);
        exitButton = findViewById(R.id.exitButton);
        numberField = findViewById(R.id.numberView);
        constraintLayout = findViewById(R.id.digitSpanTaskActivity);

        inputField = findViewById(R.id.inputView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);



        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);


        inputCount = 0;
        switchNumberButtons(false);
        setColorTheme();

    }

    //----------GUI Functions--------------------

    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.button0:
                compareInputToNumbers("0");
                break;
            case R.id.button1:
                compareInputToNumbers("1");
                break;
            case R.id.button2:
                compareInputToNumbers("2");
                break;
            case R.id.button3:
                compareInputToNumbers("3");
                break;
            case R.id.button4:
                compareInputToNumbers("4");
                break;
            case R.id.button5:
                compareInputToNumbers("5");
                break;
            case R.id.button6:
                compareInputToNumbers("6");
                break;
            case R.id.button7:
                compareInputToNumbers("7");
                break;
            case R.id.button8:
                compareInputToNumbers("8");
                break;
            case R.id.button9:
                compareInputToNumbers("9");
                break;
        }
        if(inputCount==spanLength)
        {
            maxReachedNumbers = inputCount;
            switchNumberButtons(false);
            inputField.setText("You won - press start again");

            //The next round should be longer by one
            spanLength = spanLength+1;
            numberOfRounds++;
            startingButton.setEnabled(true);
        }
        else if(inputCount == spanLength+1)
        {
            switchNumberButtons(false);
            inputField.setText("You lost");
        }

    }

    public void startButtonClicked(View v)
    {
        startingButton.setEnabled(false);
        spanNumbers = generateRandomNumbers();
        spanTaskCount = 0;
        inputCount = 0;

        fadeOut.setDuration(1500);
        fadeOut.setFillAfter(true);
        numberField.startAnimation(fadeOut);
        numberField.setText(spanNumbers[spanTaskCount]);
        spanTaskCount++;
        Runnable runnable = displayNumbers();
        handler.postDelayed(runnable,1500);

    }

    public void exitButtonClicked(View v) throws JSONException {
        int originalInitSpanLength = initSpanLength;
        //If the user did not get past the first round the span task length for the next day decreases
        if(numberOfRounds == 0 && initSpanLength != 0){
            initSpanLength--;

        }
        //If the user managed to get past his initial length the span task for the next increases
        else if(numberOfRounds > 1){
            initSpanLength++;
        }

        JSONObject toSave = JSONObjectCreator.createDigitSpanTaskObject(originalInitSpanLength, initSpanLength, maxReachedNumbers, numberOfRounds);

        sHelper.setActivity(this);
        sHelper.addDigitSpanTask(toSave, getApplicationContext());

        dsHelper.writeDataToInternalStorage("DigitSpanTask.txt",String.valueOf(initSpanLength),getApplicationContext());
        Intent myIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(myIntent);
    }


    //----------------Helper Functions--------------------------------


    //This function generates an array of length spanLength and returns it
    // The entries are numbers between 0 and 9
    private String[] generateRandomNumbers()
    {
        String[] spanNumbers = new String[spanLength];
        for(int i = 0; i < spanLength; i++ ){
            spanNumbers[i] = Integer.toString(rand.nextInt(10));
        }
        return spanNumbers;
    }


    //Displays the numbers in the spanNumbers Array with a pause of 1,5s in between

    private Runnable displayNumbers()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if(spanTaskCount == spanLength)
                {
                    handler.removeCallbacks(this);
                    switchNumberButtons(true);
                    return;
                }
                else
                {
                    fadeOut.setDuration(1500);
                    fadeOut.setFillAfter(true);
                    numberField.startAnimation(fadeOut);
                    numberField.setText(spanNumbers[spanTaskCount]);
                    spanTaskCount++;
                    handler.postDelayed(this,1500);
                }
                return;
            }
        };

        return runnable;
    }

    private void switchNumberButtons(boolean onOff)
    {
        button0.setEnabled(onOff);
        button1.setEnabled(onOff);
        button2.setEnabled(onOff);
        button3.setEnabled(onOff);
        button4.setEnabled(onOff);
        button5.setEnabled(onOff);
        button6.setEnabled(onOff);
        button7.setEnabled(onOff);
        button8.setEnabled(onOff);
        button9.setEnabled(onOff);
    }

    private void compareInputToNumbers(String input)
    {
        if(!spanNumbers[inputCount].equals(input))
        {
            if(maxReachedNumbers == 0)maxReachedNumbers = inputCount; //if no previous successfully round was completed the number of the fail run is added instead
            inputCount = spanLength+1;
        }
        else
        {
            inputField.setText(input);
            inputCount++;
        }
    }




    private void setColorTheme()
    {
        contextForColor = this.getApplicationContext();
        String mode = dsHelper.getInstance().getSpecificSettingsFromSharedPref("dmValue",contextForColor);
        String color = dsHelper.getInstance().getSpecificSettingsFromSharedPref("color",contextForColor);
        switch(mode)
        {
            case "dark-mode":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                switch (color)
                {
                    case "blue":
                        constraintLayout.setBackgroundResource(R.drawable.blue_dark_mode_background);
                        break;
                    case "red":
                        constraintLayout.setBackgroundResource(R.drawable.red_dark_mode_background);
                        break;
                    case "green":
                        constraintLayout.setBackgroundResource(R.drawable.green_dark_mode_background);
                        break;
                }
                break;
            case "white-mode":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                switch (color)
                {
                    case "blue":
                        constraintLayout.setBackgroundResource(R.drawable.blue_light_mode_background);
                        break;
                    case "red":
                        constraintLayout.setBackgroundResource(R.drawable.red_light_mode_background);
                        break;
                    case "green":
                        constraintLayout.setBackgroundResource(R.drawable.green_light_mode_background);
                        break;
                }
                break;
            case "match OS":
                break;
        }
    }

}