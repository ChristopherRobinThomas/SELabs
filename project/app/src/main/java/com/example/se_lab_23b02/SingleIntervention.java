package com.example.se_lab_23b02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Intent.getIntent;

public class SingleIntervention extends Fragment {

    DataSavingHelper dsHelper;
    private static int pInterventionID;
    private static String pInterventionName;
    private static String pInterventionDesc;
    private boolean hasCompletedIntervention;
    private TextView interventionName;
    private TextView interventionDesc;
    private Button completeButton;
    private EditText et;
    private RatingBar bar;
    String interventionStartTime;
    private ConstraintLayout constraintLayout;

    public SingleIntervention() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pInterventionID = getArguments().getInt("InterventionID");
            pInterventionName = getArguments().getString("InterventionName");
            pInterventionDesc = getArguments().getString("InterventionDescription");
        }
        dsHelper = DataSavingHelper.getInstance();
        hasCompletedIntervention = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_intervention, container, false);

        et = (EditText) view.findViewById(R.id.tvInterventionAdditionalNotes);

        // Draft: add parameter counter to oSIF; interventionList.get(counter).get(1->ID/2->Name/3->Desc)
        interventionName = view.findViewById(R.id.textView3);
        interventionDesc = view.findViewById(R.id.textView4);
        bar = (RatingBar) view.findViewById(R.id.ratingBar);
        constraintLayout = view.findViewById(R.id.singleInterventionFragment);
        // TODO: Video
        completeButton = view.findViewById(R.id.buttonComplete);
        completeButton.setOnClickListener(v -> {
            startDoingIntervention();
        });
        interventionName.setText(pInterventionName);
        interventionDesc.setText(pInterventionDesc);
        setColorTheme();

        return view;
    }

    void startDoingIntervention() {
        completeButton.setText("Complete!");
        SensorManager.toggleService(getActivity());
        interventionStartTime = TimeStamp.getCurrentTimeStamp();

        completeButton.setOnClickListener(v -> {
            SensorManager.toggleService(getActivity());
            hasCompletedIntervention = true;
            saveInterventionCompleted();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup)getView().getParent()).getId(), new InterventionsFragment(), "findThisFragment")
                    .commit();
        });
    }

    public void saveInterventionCompleted() {
        String additionalNotes;
        additionalNotes = String.valueOf(et.getText());
        JSONObject j = new JSONObject();
        try {
            j.put("timestamp", interventionStartTime);
            j.put("id", pInterventionID);
            j.put("completed", hasCompletedIntervention);
            j.put("notes", additionalNotes);
            j.put("rating", bar.getRating());
        } catch (JSONException ignored) {}
        dsHelper.writeDataToInternalStorage("completedInterventions.txt", j.toString()+"\n", getActivity().getApplicationContext(),true);
    }




    private void setColorTheme()
    {
        int ButtonTransparency = 50;
        Context contextForColor = getActivity().getApplicationContext();
        String mode = dsHelper.getInstance().getSpecificSettingsFromSharedPref("dmValue", contextForColor);
        String color = dsHelper.getInstance().getSpecificSettingsFromSharedPref("color", contextForColor);

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
        completeButton.getBackground().setAlpha(ButtonTransparency);
    }
}