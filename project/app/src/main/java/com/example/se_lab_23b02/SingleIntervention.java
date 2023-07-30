/*
 * Copyright (c) 2023.
 * Christopher Thomas, Miriam Fee, Linus Brandt, Maximilian Frese, Frederik Hüttemann
 * All rights. reserved.
 */

package com.example.se_lab_23b02;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static android.content.Intent.getIntent;

import com.google.android.material.textfield.TextInputEditText;

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
    private ScrollView constraintLayout;
    private VideoView vv;
    private ImageView iv;

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
        vv = view.findViewById(R.id.videoView);
        iv = view.findViewById(R.id.imageView2);

        // Draft: add parameter counter to oSIF; interventionList.get(counter).get(1->ID/2->Name/3->Desc)
        interventionName = view.findViewById(R.id.textView3);
        interventionDesc = view.findViewById(R.id.textView4);
        bar = (RatingBar) view.findViewById(R.id.ratingBar);
        constraintLayout = view.findViewById(R.id.singleInterventionFragment);
        completeButton = view.findViewById(R.id.buttonComplete);
        completeButton.setOnClickListener(v -> {
            startDoingIntervention();
        });

        String name = DataSavingHelper.getInstance().getTranslationOfName(pInterventionName, getActivity().getApplicationContext());
        interventionName.setText(name);
        String desc = DataSavingHelper.getInstance().getTranslationOfDesc(pInterventionName, getActivity().getApplicationContext());
        interventionDesc.setText(desc);
        TextInputEditText ti = (TextInputEditText) view.findViewById(R.id.tvInterventionAdditionalNotes);
        ti.setHint(SensorManager.getLanguageResources(getActivity().getApplicationContext()).getString(R.string.add_notes));
        chooseAndLoadVideo();
        setColorTheme();


        return view;
    }

    void chooseAndLoadVideo() {
        if(Objects.equals(pInterventionName, "Cobra")) {
            iv.setVisibility(View.INVISIBLE);
            vv.setVideoURI(Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.cobra));
            vv.setVisibility(View.VISIBLE);
        }
        else if(Objects.equals(pInterventionName, "Rueckenentspannung")) {
            iv.setVisibility(View.INVISIBLE);
            vv.setVideoURI(Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.rueckenentspannung));
            vv.setVisibility(View.VISIBLE);
        }
        else if(Objects.equals(pInterventionName, "Laterallinien 1")) {
            iv.setVisibility(View.INVISIBLE);
            vv.setVideoURI(Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.sample_exercise1_1));
            vv.setVisibility(View.VISIBLE);
        }
        else if(Objects.equals(pInterventionName, "Laterallinien 2")) {
            iv.setVisibility(View.INVISIBLE);
            vv.setVideoURI(Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.sample_exercise_2_1));
            vv.setVisibility(View.VISIBLE);
        }
        else if(Objects.equals(pInterventionName, "Seitliche Beinrolle")) {
            iv.setVisibility(View.INVISIBLE);
            vv.setVideoURI(Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.seitliche_beinrolle));
            vv.setVisibility(View.VISIBLE);
        }
        else if(Objects.equals(pInterventionName, "Windmuehle")) {
            iv.setVisibility(View.INVISIBLE);
            vv.setVideoURI(Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.windmuehle));
            vv.setVisibility(View.VISIBLE);
        }
        else if(Objects.equals(pInterventionName, "Burpies")) {
            iv.setVisibility(View.INVISIBLE);
            vv.setVideoURI(Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.burpies));
            vv.setVisibility(View.VISIBLE);
        }
        else if(Objects.equals(pInterventionName, "Jumping Jacks")) {
            iv.setVisibility(View.INVISIBLE);
            vv.setVideoURI(Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.jumpingjacks));
            vv.setVisibility(View.VISIBLE);
        }
        else if(Objects.equals(pInterventionName, "Push-Ups")) {
            iv.setVisibility(View.INVISIBLE);
            vv.setVideoURI(Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.pushups));
            vv.setVisibility(View.VISIBLE);
        }
        else if(Objects.equals(pInterventionName, "Sit-Ups")) {
            iv.setVisibility(View.INVISIBLE);
            vv.setVideoURI(Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.situps));
            vv.setVisibility(View.VISIBLE);
        }
        else {
            iv.setVisibility(View.VISIBLE);
            vv.setVisibility(View.INVISIBLE);
        }
        vv.start();
    }

    void startDoingIntervention() {
        if(!SensorManager.checkRunning(getActivity().getApplicationContext())) {
            completeButton.setText(SensorManager.getLanguageResources(getActivity().getApplicationContext()).getString(R.string.complete));
            interventionStartTime = TimeStamp.getCurrentTimeStamp();
            SensorManager.toggleService(getActivity(), interventionStartTime);

            completeButton.setOnClickListener(v -> {
                SensorManager.toggleService(getActivity(), interventionStartTime);
                hasCompletedIntervention = true;
                saveInterventionCompleted();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), new InterventionsFragment(), "findThisFragment")
                        .commit();
            });
        } else {
            Toast.makeText(getActivity().getApplicationContext(), SensorManager.getLanguageResources(getActivity().getApplicationContext()).getString(R.string.toast_stop_intervention), Toast.LENGTH_LONG).show();
        }
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