package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class AllPastInterventionsActivity extends AppCompatActivity {


    //----GUI Elements-------
    private LinearLayout linearLayout;
    private TextView txtViewDate;

    private LinearLayout linearLayoutToAdd;
    private ImageView imageViewIntervention;

    private TextView txtViewInterventions;

    private Resources resources;

    //------Objects-----------
    private DataSavingHelper dsHelper;
    Vector<Vector<String>> interventionList;

    //-------Color Variables -----
    private Context contextForColor;
    private ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_past_interventions);
        linearLayout = findViewById(R.id.linearLayoutAllPastInterventions);
        constraintLayout = findViewById(R.id.allPastInterventionConstraintLayout);
        refreshResources();

        getPastInterventions();
        txtViewDate = new TextView(this);
        txtViewDate.setTextSize(20);
        txtViewDate.setGravity(Gravity.CENTER);
        linearLayout.addView(txtViewDate);

        setColorTheme();
        TextView textView = (TextView) findViewById(R.id.tv_allInterventions_pastInterventions);
        textView.setText(resources.getString(R.string.tv_recent_interventions));

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
        Context c = LocaleHelper.setLocale(getApplicationContext(), language_short);
        resources = c.getResources();

    }


    private void getPastInterventions()
    {


        //-----Variables----------
        JsonObject obj = new JsonObject();
        JsonObject obj2 = new JsonObject();
        JsonParser parser = new JsonParser();
        String date = " ";
        String currentDate;
        String id;
        String[] time;
        String type;


        String singleIntervention;
        String singleRecording;
        int interventionCounter;
        int recordingCounter;
        String[] allInterventions;
        String[] allRecordings;
        dsHelper = DataSavingHelper.getInstance();
        try {
            interventionList = dsHelper.getSavedInterventions(this);
        } catch (JSONException ignored) {}

        try {
            String temp = dsHelper.readSavedDataFromInternalStorage("completedInterventions.txt",this);
            allInterventions = temp.split("\n");
            interventionCounter = allInterventions.length-1;

        }
        catch(Exception e){
            interventionCounter = 0;
            allInterventions = new String[]{" "};
        }

        try {
            String temp2 = dsHelper.readSavedDataFromInternalStorage("sensorrecordings.txt", this);
            allRecordings = temp2.split("\n");
            recordingCounter = allRecordings.length-1;
        }
        catch(Exception e)
        {
            recordingCounter = 0;
            allRecordings = new String[]{" "};
        }

        while(true) {

            boolean isBefore;
            if (interventionCounter >= 1 && recordingCounter >= 1) {

                //Both Recordings and Interventions exists
                singleIntervention = allInterventions[interventionCounter];
                obj = parser.parse(singleIntervention).getAsJsonObject();
                singleRecording = allRecordings[recordingCounter];
                obj2 = parser.parse(singleRecording).getAsJsonObject();

                //--------------------Check Date-------------------------------
                String startInput = obj.get("timestamp").getAsString();
                String stopInput = obj2.get("timestamp").getAsString();
                if(startInput.equals(stopInput))
                {
                    isBefore = false;
                    recordingCounter--;
                }
                else
                {
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
                    LocalDateTime start = LocalDateTime.parse(startInput, f);
                    LocalDateTime stop = LocalDateTime.parse(stopInput, f);
                    isBefore = start.isBefore(stop);
                }
                //---------------------------------------------------
            }
            else if(recordingCounter < 1 && interventionCounter >= 1)
            {
                //No Recordings exists but interventions do
                singleIntervention = allInterventions[interventionCounter];
                obj = parser.parse(singleIntervention).getAsJsonObject();
                isBefore = false;
            }
            else if(recordingCounter >= 1 && interventionCounter < 1)
            {
                //No interventions exists but recordings do
                singleRecording = allRecordings[recordingCounter];
                obj2 = parser.parse(singleRecording).getAsJsonObject();
                isBefore = true;

            }
            else
            {
                //None of them exists
                break;
            }


            if (!isBefore) {
                currentDate = obj.get("timestamp").getAsString().split(" ")[0];
                time = obj.get("timestamp").getAsString().split(" ")[1].split(":");
                type = "Intervention";
                id = String.valueOf(obj.get("id").getAsInt());
                interventionCounter--;
            }
            else if (recordingCounter >= 1) {
                currentDate = obj2.get("timestamp").getAsString().split(" ")[0];
                time = obj2.get("timestamp").getAsString().split(" ")[1].split(":");
                type = "Recording";
                recordingCounter--;
                id = "0";
            }
            else {
                break;
            }

            if (!date.equals(currentDate)) {
                date = currentDate;
                addTextView(currentDate);
            }

            addInterventionView(id, time, date, type);
        }
    }


    private void addTextView(String date)
    {
        txtViewDate = new TextView(this);
        txtViewDate.setTextSize(20);
        txtViewDate.setGravity(Gravity.CENTER);
        txtViewDate.setText(date);
        linearLayout.addView(txtViewDate);
    }

    private void addInterventionView(String id, String[] time, String date, String type)
    {
        contextForColor = this.getApplicationContext();
        String mode = dsHelper.getInstance().getSpecificSettingsFromSharedPref("dmValue",contextForColor);

        linearLayoutToAdd = new LinearLayout(this);
        linearLayoutToAdd.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayoutToAdd.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutToAdd.setGravity(Gravity.LEFT);
        linearLayoutToAdd.setDividerDrawable(getResources().getDrawable(R.drawable.divider_big));
        linearLayoutToAdd.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        if(mode.equals("white-mode")) linearLayoutToAdd.setBackground(getResources().getDrawable(R.drawable.step_count_border_light_mode));
        else linearLayoutToAdd.setBackground(getResources().getDrawable(R.drawable.step_count_border));


        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER;
        imageViewIntervention = new ImageView(this);
        imageViewIntervention.setLayoutParams(layoutParams);
        //If else with different Picture
        if(type.equals("Recording")) {
            if(mode.equals("white-mode")) imageViewIntervention.setImageResource((R.drawable.baseline_sensors_24));
            else imageViewIntervention.setImageResource((R.drawable.baseline_sensors_24_dark_mode));
        }
        else
        {
            if(mode.equals("white-mode")) imageViewIntervention.setImageResource(R.drawable.baseline_directions_run_24);
            else imageViewIntervention.setImageResource(R.drawable.baseline_directions_run_24_dark_mode);
        }
        imageViewIntervention.setForegroundGravity(Gravity.CENTER);
        linearLayoutToAdd.addView(imageViewIntervention);

        txtViewInterventions = new TextView(this);
        txtViewInterventions.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if(type.equals("Recording")) {

            txtViewInterventions.setText(resources.getString(R.string.Own_Recording) +  "\n" + resources.getString(R.string.Time) + ": " + time[0] + ":" + time[1]);
        }
        else
        {
            String name = interventionList.get(Integer.valueOf(id)).get(1);
            txtViewInterventions.setText(name + "\n" + resources.getString(R.string.Time) + ": " + time[0] + ":" + time[1]);
        }
        txtViewInterventions.setTextSize(20);
        linearLayoutToAdd.addView(txtViewInterventions);


        linearLayoutToAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("date", date);
                b.putStringArray("time", time);
                Intent intent = new Intent(v.getContext(), SensorRecordingGraphActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        linearLayout.addView(linearLayoutToAdd); 
    }




    private void setColorTheme()
    {
        int ButtonTransparency = 50;

        contextForColor = this.getApplicationContext();
        String mode = dsHelper.getInstance().getSpecificSettingsFromSharedPref("dmValue",contextForColor);
        String color = dsHelper.getInstance().getSpecificSettingsFromSharedPref("color",contextForColor);

        switch(mode)
        {
            case "dark-mode":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                ButtonTransparency = 70;
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
                ButtonTransparency = 150;
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
        }


    }
}
