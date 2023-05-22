package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

public class QuestionnaireActivity extends AppCompatActivity {

    private int[] questionOneValues = new int[6];
    private SeekBar[] slidersOne = new SeekBar[6];

    private boolean questionTwoBool;
    private SeekBar[] slidersTwo = new SeekBar[2];
    private int[] questionTwoValues = new int[2];

    private boolean questionThreeBool;
    private SeekBar[] slidersThree = new SeekBar[1];
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

    void manageQuestionThree() {
        slidersThree[0] = findViewById(R.id.Question3_2Slider);
        RadioGroup radioGroup = findViewById(R.id.Question3YesOrNo);
        RadioButton radioButtonYes = findViewById(R.id.Question3Yes);
        RadioButton radioButtonNo = findViewById(R.id.Question3No);

        Spinner spinner = findViewById(R.id.Question4_1DropDown);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioButtonYes.getId()) {
                    questionThreeBool = true;
                    spinner.setEnabled(true);
                    for (int i = 0; i < slidersThree.length; i++) {
                        slidersThree[i].setEnabled(true);
                    }
                } else if (checkedId == radioButtonNo.getId()) {
                    questionThreeBool = false;
                    spinner.setEnabled(false);
                    for (int i = 0; i < slidersThree.length; i++) {
                        slidersThree[i].setEnabled(false);
                    }
                }
            }
        });

        for (int i = 0; i < slidersThree.length; i++) {
            slidersThree[i].setMax(100);
            slidersThree[i].setMin(0);
            slidersThree[i].setEnabled(false);

            slidersThree[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    for (int i = 0; i < slidersThree.length; i++) {
                        if (slidersThree[i] == seekBar) {
                            questionThreeValues[i] = progress;
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
    void manageQuestionFour()
    {
        Spinner spinner = findViewById(R.id.Question4_1DropDown);
        spinner.setEnabled(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item);
        adapter.add("Partner");
        adapter.add("Family");
        adapter.add("Friends");
        adapter.add("Coworkers");
        adapter.add("Strangers");
        adapter.add("Other"); // To be implemented

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionFourValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void manageQuestionFive()
    {
        Spinner spinner = findViewById(R.id.Question5_1DropDown);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item);
        adapter.add("At home");
        adapter.add("School/University");
        adapter.add("Work");
        adapter.add("Sport");
        adapter.add("other recreational Activity");
        adapter.add("Shopping");
        adapter.add("Visiting");
        adapter.add("Other"); // To be implemented

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionFiveValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void manageQuestionSix(){}

    void manageQuestionSeven(){}

    void manageQuestionEight(){}


}