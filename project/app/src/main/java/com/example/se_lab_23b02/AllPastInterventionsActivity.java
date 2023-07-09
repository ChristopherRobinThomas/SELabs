package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

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
        dsHelper = DataSavingHelper.getInstance();
        try {
            String temp = dsHelper.readSavedDataFromInternalStorage("completedInterventions.txt",this);
            String[] allInterventions = temp.split("\n");
            String singleIntervention;
            JsonObject obj;
            JsonParser parser = new JsonParser();
            String date = " ";
            String id;
            String[] time;
            for(int i = allInterventions.length-1; i>0; i--)
            {
                singleIntervention = allInterventions[i];
                obj = parser.parse(singleIntervention).getAsJsonObject();
                if(!date.equals(obj.get("timestamp").getAsString().split(" ")[0]))
                {
                    date = obj.get("timestamp").getAsString().split(" ")[0];
                    addTextView(date);
                }
                id = String.valueOf(obj.get("id").getAsInt());
                time = obj.get("timestamp").getAsString().split(" ")[1].split(":");
                addInterventionView(id, time);

            }
        }
        catch(Exception e){

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

    private void addInterventionView(String id, String[] time)
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
        imageViewIntervention.setImageResource(R.drawable.baseline_directions_run_24);
        imageViewIntervention.setForegroundGravity(Gravity.CENTER);
        linearLayoutToAdd.addView(imageViewIntervention);

        txtViewInterventions = new TextView(this);
        txtViewInterventions.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        txtViewInterventions.setText("Name: " + id + "\nUhrzeit: " + time[0] + ":" + time[1]);
        txtViewInterventions.setTextSize(20);
        linearLayoutToAdd.addView(txtViewInterventions);


        linearLayoutToAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Open Activity that shows Data and übergebe Timestamp
                System.out.println(time[0] + ":" + time[1] + ":" + time[2] + "\n");
            }
        });

        linearLayout.addView(linearLayoutToAdd); 
    }
}
