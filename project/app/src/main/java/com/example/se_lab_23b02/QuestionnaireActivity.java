package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class QuestionnaireActivity extends AppCompatActivity {

    private int[] questionOneValues = new int[6];
    private SeekBar[] slidersOne = new SeekBar[6];

    private boolean questionTwoBool;
    private SeekBar[] slidersTwo = new SeekBar[2];
    private int[] questionTwoValues = new int[2];

    private boolean questionThreeBool;
    private SeekBar[] slidersThree = new SeekBar[1];
    private int[] questionThreeValues = new int[1];

    private String[] questionFourValue = new String[1];

    private String[] questionFiveValue = new String[1];

    private SeekBar[] slidersSix = new SeekBar[4];
    private int[] questionSixValues = new int[4];

    private SeekBar[] slidersSeven = new SeekBar[2];
    private int[] questionSevenValues = new int[2];

    private SeekBar[] slidersEight = new SeekBar[2];
    private int[] questionEightValues = new int[2];

    private String timestamp;

    private DataSavingHelper dsHelper;
    private ServerHelper sHelper;

    private ArrayList<Object> elementsForLanguageChange = new ArrayList<>();
    private Context context;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        dsHelper = DataSavingHelper.getInstance();
        sHelper = ServerHelper.getInstance();

        elementsForLanguageChange.add(findViewById(R.id.Question1_1Text));
        elementsForLanguageChange.add(findViewById(R.id.Question1_2Text));
        elementsForLanguageChange.add(findViewById(R.id.Question1_3Text));
        elementsForLanguageChange.add(findViewById(R.id.Question1_4Text));
        elementsForLanguageChange.add(findViewById(R.id.Question1_5Text));
        elementsForLanguageChange.add(findViewById(R.id.Question1_6Text));
        elementsForLanguageChange.add(findViewById(R.id.Question2Text));
        elementsForLanguageChange.add(findViewById(R.id.Question2_2));
        elementsForLanguageChange.add(findViewById(R.id.Question2_3Text));
        elementsForLanguageChange.add(findViewById(R.id.Question3Text));
        elementsForLanguageChange.add(findViewById(R.id.Question3_1Text));
        elementsForLanguageChange.add(findViewById(R.id.Question4_1Text));
        elementsForLanguageChange.add(findViewById(R.id.Question5_1Text));
        elementsForLanguageChange.add(findViewById(R.id.Question6_1Text));
        elementsForLanguageChange.add(findViewById(R.id.Question6_2Text));
        elementsForLanguageChange.add(findViewById(R.id.Question6_3Text));
        elementsForLanguageChange.add(findViewById(R.id.Question6_4Text));
        elementsForLanguageChange.add(findViewById(R.id.Question7_1Text));
        elementsForLanguageChange.add(findViewById(R.id.Question7_2Text));
        elementsForLanguageChange.add(findViewById(R.id.Question8_1Text));
        elementsForLanguageChange.add(findViewById(R.id.Question8_2Text));
        elementsForLanguageChange.add(findViewById(R.id.Send_Button));

        refreshResources();
        setLanguage();




        manageQuestions();
        findViewById(R.id.Send_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveAnswers();
                }catch (Exception e) { }
                if(TimeStamp.getCurrentTimeSlot() == TimeStamp.TimeSlot.noon)
                {
                    Intent i = new Intent(QuestionnaireActivity.this, DigitSpanTaskActivity.class);
                    startActivity(i);
                }
                else
                {
                    Intent i = new Intent(QuestionnaireActivity.this, HomeScreenActivity.class);
                    startActivity(i);
                }
                finish();
            }
        });

    }

    void saveAnswers() throws JSONException {

        JSONArray[] valueArrays = {
                new JSONArray(questionOneValues),
                new JSONArray(questionTwoValues),
                new JSONArray(questionThreeValues),
                new JSONArray(questionFourValue),
                new JSONArray(questionFiveValue),
                new JSONArray(questionSixValues),
                new JSONArray(questionSevenValues),
                new JSONArray(questionEightValues)
        };
        JSONObject toSave = new JSONObject();
        for (int i = 1; i <= valueArrays.length; i++) {
            toSave.put("Q"+i, valueArrays[i-1]);
        }
        timestamp = TimeStamp.getCurrentTimeStamp();
        toSave.put("timestamp: ", timestamp);

        sHelper.setActivity(this);
        sHelper.addQuestionnaire(toSave, getApplicationContext());
        dsHelper.writeDataToInternalStorage("questionnaire.txt", toSave.toString()+"\n", getApplicationContext(), true);
    }

    void  manageQuestions() {
        manageQuestionOne();
        manageQuestionTwo();
        manageQuestionThree();
        manageQuestionFour();
        manageQuestionFive();
        manageQuestionSix();
        manageQuestionSeven();
        manageQuestionEight();
    }

    void manageQuestionOne() {
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

    void manageQuestionTwo() {
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

    void manageQuestionThree(){

        slidersThree[0] = findViewById(R.id.Question3_2Slider);
        RadioGroup radioGroup = findViewById(R.id.Question3YesOrNo);
        RadioButton radioButtonYes = findViewById(R.id.Question3Yes);
        RadioButton radioButtonNo = findViewById(R.id.Question3No);

        Spinner spinner = findViewById(R.id.Question4_1DropDown);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioButtonNo.getId()) {
                    questionThreeBool = true;
                    spinner.setEnabled(true);
                    for (int i = 0; i < slidersThree.length; i++) {
                        slidersThree[i].setEnabled(true);
                    }
                } else if (checkedId == radioButtonYes.getId()) {
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
    void manageQuestionFour() {
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
                questionFourValue[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void manageQuestionFive() {
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
                questionFiveValue[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void manageQuestionSix() {
        slidersSix[0] = findViewById(R.id.Question6_1Slider);
        slidersSix[1] = findViewById(R.id.Question6_2Slider);
        slidersSix[2] = findViewById(R.id.Question6_3Slider);
        slidersSix[3] = findViewById(R.id.Question6_4Slider);

        for(int i = 0; i < slidersSix.length ;i++){

            slidersSix[i].setMax(100);
            slidersSix[i].setMin(0);

            slidersSix[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    for(int i = 0; i < slidersSix.length;i++)
                    {
                        if(slidersSix[i] == seekBar)
                        {
                            questionSixValues[i] = progress;
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

    void manageQuestionSeven() {
        slidersSeven[0] = findViewById(R.id.Question7_1Slider);
        slidersSeven[1] = findViewById(R.id.Question7_2Slider);

        for(int i = 0; i < slidersSeven.length ;i++){

            slidersSeven[i].setMax(9);
            slidersSeven[i].setMin(0);

            slidersSeven[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    for(int i = 0; i < slidersSeven.length;i++)
                    {
                        if(slidersSeven[i] == seekBar)
                        {
                            questionSevenValues[i] = progress;
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

    void manageQuestionEight() {
        slidersEight[0] = findViewById(R.id.Question8_1Slider);
        slidersEight[1] = findViewById(R.id.Question8_2Slider);

        for(int i = 0; i < slidersEight.length ;i++){

            slidersEight[i].setMax(6);
            slidersEight[i].setMin(0);

            slidersEight[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    for(int i = 0; i < slidersEight.length;i++)
                    {
                        if(slidersEight[i] == seekBar)
                        {
                            questionEightValues[i] = progress;
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
        tv.setText(resources.getString(R.string.tv_questionnaire_q1_1));
        tv = (TextView) elementsForLanguageChange.get(1);
        tv.setText(resources.getString(R.string.tv_questionnaire_q1_2));
        tv = (TextView) elementsForLanguageChange.get(2);
        tv.setText(resources.getString(R.string.tv_questionnaire_q1_3));
        tv = (TextView) elementsForLanguageChange.get(3);
        tv.setText(resources.getString(R.string.tv_questionnaire_q1_4));
        tv = (TextView) elementsForLanguageChange.get(4);
        tv.setText(resources.getString(R.string.tv_questionnaire_q1_5));
        tv = (TextView) elementsForLanguageChange.get(5);
        tv.setText(resources.getString(R.string.tv_questionnaire_q1_6));
        tv = (TextView) elementsForLanguageChange.get(6);
        tv.setText(resources.getString(R.string.tv_questionnaire_q2));
        tv = (TextView) elementsForLanguageChange.get(7);
        tv.setText(resources.getString(R.string.tv_questionnaire_q2_2));
        tv = (TextView) elementsForLanguageChange.get(8);
        tv.setText(resources.getString(R.string.tv_questionnaire_q2_3));
        tv = (TextView) elementsForLanguageChange.get(9);
        tv.setText(resources.getString(R.string.tv_questionnaire_q3));
        tv = (TextView) elementsForLanguageChange.get(10);
        tv.setText(resources.getString(R.string.tv_questionnaire_q3_1));
        tv = (TextView) elementsForLanguageChange.get(11);
        tv.setText(resources.getString(R.string.tv_questionnaire_q4_1));
        tv = (TextView) elementsForLanguageChange.get(12);
        tv.setText(resources.getString(R.string.tv_questionnaire_q5_1));
        tv = (TextView) elementsForLanguageChange.get(13);
        tv.setText(resources.getString(R.string.tv_questionnaire_q6_1));
        tv = (TextView) elementsForLanguageChange.get(14);
        tv.setText(resources.getString(R.string.tv_questionnaire_q6_2));
        tv = (TextView) elementsForLanguageChange.get(15);
        tv.setText(resources.getString(R.string.tv_questionnaire_q6_3));
        tv = (TextView) elementsForLanguageChange.get(16);
        tv.setText(resources.getString(R.string.tv_questionnaire_q6_4));
        tv = (TextView) elementsForLanguageChange.get(17);
        tv.setText(resources.getString(R.string.tv_questionnaire_q7_1));
        tv = (TextView) elementsForLanguageChange.get(18);
        tv.setText(resources.getString(R.string.tv_questionnaire_q7_2));
        tv = (TextView) elementsForLanguageChange.get(19);
        tv.setText(resources.getString(R.string.tv_questionnaire_q8_1));
        tv = (TextView) elementsForLanguageChange.get(20);
        tv.setText(resources.getString(R.string.tv_questionnaire_q8_2));
        Button bt = (Button) elementsForLanguageChange.get(21);
        bt.setText(resources.getString(R.string.bt_send));




    }
}