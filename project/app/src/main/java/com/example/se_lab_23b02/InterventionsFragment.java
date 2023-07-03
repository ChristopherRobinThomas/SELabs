package com.example.se_lab_23b02;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InterventionsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button buttonIntervention1;
    Button buttonIntervention2;
    Button buttonIntervention3;



    public InterventionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InterventionsFragment newInstance(String param1, String param2) {
        InterventionsFragment fragment = new InterventionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_interventions, container, false);

        Vector<Vector<String>> interventionList = new Vector<Vector<String>>();
        // TODO: Read from localStorage
        // populateButtonList(interventionList, view);

        Vector<Button> buttonList = new Vector<Button>();
        buttonIntervention1 = view.findViewById(R.id.buttonIntervention1);
        buttonList.add(buttonIntervention1);
        buttonIntervention2 = view.findViewById(R.id.buttonIntervention2);
        buttonList.add(buttonIntervention2);
        buttonIntervention3 = view.findViewById(R.id.buttonIntervention3);
        buttonList.add(buttonIntervention3);

        Button btStartRecording = view.findViewById(R.id.btStartRecording);
        Button btStopRecording = view.findViewById(R.id.btEndRecording);

        for (Button b : buttonList) {
            b.setOnClickListener(v -> {
                openSingleInterventionFragment(b.getText().toString());
            });
        }

        btStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorManager.toggleService(getActivity());
            }
        });
        btStopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorManager.toggleService(getActivity());
            }
        });

        return view;
    }


    private void openSingleInterventionFragment(String buttonText) {
        SingleIntervention singleIntervention = new SingleIntervention();
        Bundle args = new Bundle();
        // TODO: Get Name and Desc dynamically
        // Draft: add parameter counter to oSIF; interventionList.get(counter).get(1->ID/2->Name/3->Desc)
        args.putInt("InterventionID", 0);
        args.putString("InterventionName", buttonText);
        args.putString("InterventionDescription", "This is a sample description.");
        singleIntervention.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), singleIntervention, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }
    private void populateButtonList(Vector<Vector<String>> interventionList, View view) {
        for (Vector<String> interventionObject : interventionList) {
            Button newButton = new Button(this.getContext());
            newButton.setText(interventionObject.get(0));

            ConstraintLayout cl = view.findViewById(R.id.singleInterventionConstraintLayout);
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            cl.addView(newButton, lp);
        }
    }
}