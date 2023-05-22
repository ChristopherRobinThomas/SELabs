package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

public class QuestionnaireActivity extends AppCompatActivity {

    private int[] questionOneValues = new int[6];
    private SeekBar[] slidersOne = new SeekBar[6];

    private boolean questionTwoBool;
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

    void manageQuestionTwo(){}

    void manageQuestionThree(){}

    void manageQuestionFour(){}

    void manageQuestionFive(){}

    void manageQuestionSix(){}

    void manageQuestionSeven(){}

    void manageQuestionEight(){}


}