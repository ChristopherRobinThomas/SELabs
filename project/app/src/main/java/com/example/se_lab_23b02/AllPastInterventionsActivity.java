package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AllPastInterventionsActivity extends AppCompatActivity {


    //----GUI Elements-------
    private LinearLayout linearLayout;
    private TextView txtViewDate;

    private LinearLayout linearLayoutToAdd;
    private ImageView imageViewIntervention;

    private TextView txtViewInterventions;


    //------Objects-----------
    private DataSavingHelper dsHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_past_interventions);
        linearLayout = findViewById(R.id.linearLayoutAllPastInterventions);


        getPastInterventions();
        txtViewDate = new TextView(this);
        txtViewDate.setTextSize(20);
        txtViewDate.setGravity(Gravity.CENTER);
        linearLayout.addView(txtViewDate);
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
                DateTimeFormatter f = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

                LocalDateTime start = LocalDateTime.parse(startInput, f);
                LocalDateTime stop = LocalDateTime.parse(stopInput, f);
                isBefore = start.isBefore(stop);
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
        linearLayoutToAdd = new LinearLayout(this);
        linearLayoutToAdd.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayoutToAdd.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutToAdd.setGravity(Gravity.LEFT);
        linearLayoutToAdd.setDividerDrawable(getDrawable(R.drawable.divider_big));
        linearLayoutToAdd.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayoutToAdd.setBackground(getDrawable(R.drawable.linear_layout_border));


        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity=Gravity.CENTER;
        imageViewIntervention = new ImageView(this);
        imageViewIntervention.setLayoutParams(layoutParams);
        if(type.equals("Recording")) {
        imageViewIntervention.setImageResource((R.drawable.baseline_play_circle_24));
        }
        else
        {
            imageViewIntervention.setImageResource(R.drawable.baseline_directions_run_24);
        }
        imageViewIntervention.setForegroundGravity(Gravity.CENTER);
        linearLayoutToAdd.addView(imageViewIntervention);

        txtViewInterventions = new TextView(this);
        txtViewInterventions.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if(type.equals("Recording")) {

            txtViewInterventions.setText("Own Recording \n" + "Time: " + time[0] + ":" + time[1]);
        }
        else
        {
            txtViewInterventions.setText("Name: " + id + "\nUhrzeit: " + time[0] + ":" + time[1]);
        }
        txtViewInterventions.setTextSize(20);
        linearLayoutToAdd.addView(txtViewInterventions);


        linearLayoutToAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Open Activity that shows Data and übergebe Timestamp
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
}
