package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

public class QuestionnaireActivity extends AppCompatActivity {

    private int[] questionOneValues = new int[6];
    private SeekBar[] slidersOne = new SeekBar[6];

    private boolean questionTwoBool;
    private SeekBar[] slidersTwo = new SeekBar[2];
    private int[] questionTwoValues = new int[2];

    private boolean questionThreeBool;
    private int[] questionThreeValues = new int[1];

    private String questionFourValue;

    private String questionFiveValue;

    private int[] questionSixValues = new int[1];

    private int[] questionSevenValues = new int[1];

    private int[] questionEightValues = new int[1];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        manageQuestions();
    }

    void  manageQuestions()
    {
        manageQuestionOne();
        manageQuestionTwo();
        manageQuestionThree();
        manageQuestionFour();
        manageQuestionFive();
        manageQuestionSix();
        manageQuestionSeven();
        manageQuestionEight();
    }

    void manageQuestionOne()
    {
        slidersOne[0] = findViewById(R.id.Question1_1Slider);
        slidersOne[1] = findViewById(R.id.Question1_2Slider);
        slidersOne[2] = findViewById(R.id.Question1_3Slider);
        slidersOne[3] = findViewById(R.id.Question1_4Slider);
        slidersOne[4] = findViewById(R.id.Question1_5Slider);
        slidersOne[5] = findViewById(R.id.Question1_6Slider);

        for(int i = 0; i < slidersOne.length ;i++){

            slidersOne[i].setMax(100);
            slidersOne[i].setMin(0);

            slidersOne[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    for(int i = 0; i < slidersOne.length;i++)
                    {
                        if(slidersOne[i] == seekBar)
                        {
                            questionOneValues[i] = progress;
                            break;
                        }
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }

    }

    void manageQuestionTwo()
    {
        slidersTwo[0] = findViewById(R.id.Question2_2Slider);
        slidersTwo[1] = findViewById(R.id.Question2_3Slider);
        RadioGroup radioGroup = findViewById(R.id.Question2YesOrNo);
        RadioButton radioButtonYes = findViewById(R.id.Question2Yes);
        RadioButton radioButtonNo = findViewById(R.id.Question2No);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == radioButtonYes.getId())
                {
                    questionTwoBool = true;
                    for(int i = 0; i < slidersTwo.length; i++)
                    {
                        slidersTwo[i].setEnabled(true);
                    }
                }
                else if (checkedId == radioButtonNo.getId())
                {
                    questionTwoBool = false;
                    for(int i = 0; i < slidersTwo.length; i++)
                    {
                        slidersTwo[i].setEnabled(false);
                    }
                }
            }
        });

        for(int i = 0; i < slidersTwo.length; i++)
        {
            slidersTwo[i].setMax(100);
            slidersTwo[i].setMin(0);
            slidersTwo[i].setEnabled(false);

            slidersTwo[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    for(int i = 0; i < slidersTwo.length;i++)
                    {
                        if(slidersTwo[i] == seekBar)
                        {
                            questionTwoValues[i] = progress;
                            break;
                        }
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    void manageQuestionThree(){}

    void manageQuestionFour(){}

    void manageQuestionFive(){}

    void manageQuestionSix(){}

    void manageQuestionSeven(){}

    void manageQuestionEight(){}


}