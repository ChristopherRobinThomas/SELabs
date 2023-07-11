package com.example.se_lab_23b02;

import android.content.Intent;
import android.os.Bundle;

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
    TextView interventionName;
    TextView interventionDesc;
    Button completeButton;
    EditText et;
    RatingBar bar;

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
        // TODO: Video
        completeButton = view.findViewById(R.id.buttonComplete);
        completeButton.setOnClickListener(v -> {
            startDoingIntervention();
        });
        interventionName.setText(pInterventionName);
        interventionDesc.setText(pInterventionDesc);

        return view;
    }

    void startDoingIntervention() {
        completeButton.setText("Complete!");
        SensorManager.toggleService(getActivity());

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
        String now = TimeStamp.getCurrentTimeStamp();
        JSONObject j = new JSONObject();
        try {
            j.put("timestamp", now);
            j.put("id", pInterventionID);
            j.put("completed", hasCompletedIntervention);
            j.put("notes", additionalNotes);
            j.put("rating", bar.getRating());
        } catch (JSONException ignored) {}
        dsHelper.writeDataToInternalStorage("completedInterventions.txt", j.toString()+"\n", getActivity().getApplicationContext(),true);
    }
}