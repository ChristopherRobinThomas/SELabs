package com.example.se_lab_23b02;

import android.app.AlertDialog;
import android.content.DialogInterface;
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


import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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
    private String m_Text = "";

    Button buttonIntervention1;
    Button buttonIntervention2;
    Button buttonIntervention3;
    Button buttonAddNewIntervention;
    Vector<Button> buttonList;
    Vector<Vector<String>> interventionList;
    private LinearLayout linearLayoutToAdd;



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

        // TODO: Save Interventions in file, then:
        //  interventionList = getSavedInterventions();

        interventionList = new Vector<Vector<String>>();
        Vector<String> sampleInterventionA = new Vector<String>();
        sampleInterventionA.add("0");
        sampleInterventionA.add("Push-Ups");
        sampleInterventionA.add("Perform 10 push-ups.");
        interventionList.add(sampleInterventionA);
        Vector<String> sampleInterventionB = new Vector<String>();
        sampleInterventionB.add("1");
        sampleInterventionB.add("Sit-Ups");
        sampleInterventionB.add("Perform 10 sit-ups.");
        interventionList.add(sampleInterventionB);
        Vector<String> sampleInterventionC = new Vector<String>();
        sampleInterventionC.add("2");
        sampleInterventionC.add("Light Run");
        sampleInterventionC.add("Run a comfortable distance at a slow pace. Our recommendation: 2 kilometers in 10 minutes");
        interventionList.add(sampleInterventionC);

        buttonList = populateButtonList(interventionList, view);
        buttonAddNewIntervention = view.findViewById(R.id.buttonAddNewIntervention);
        for (int i = 0; i < buttonList.size(); i++) {
            int finalI = i;
            buttonList.get(i).setOnClickListener(v -> {
                openSingleInterventionFragment(interventionList.get(finalI));
            });
        }
        buttonAddNewIntervention.setOnClickListener(v -> {
            showAddInterventionDialogue(view);
        });
        
        Button btStartRecording = view.findViewById(R.id.btStartRecording);

        btStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorManager.toggleService(getActivity());
            }
        });

        return view;
    }
    private void showAddInterventionDialogue(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Title");
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNewIntervention(input.getText().toString(), view);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void addNewIntervention(String name, View view) {
        Vector<String> newIntervention = new Vector<String>();
        int currHighestIndex = Integer.parseInt(interventionList.get(interventionList.size() - 1).get(0));
        newIntervention.add(String.valueOf(currHighestIndex + 1));
        newIntervention.add(name);
        newIntervention.add("");
        interventionList.add(newIntervention);

        int newButtonTopMargin = 58 * (interventionList.size() - 1);
        Button newButton = new Button(this.getContext());
        newButton.setText(name);
        //ConstraintLayout cl = view.findViewById(R.id.singleInterventionConstraintLayout);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(407, 58);
        lp.topMargin = newButtonTopMargin;
        //cl.addView(newButton, lp);
        buttonList.add(newButton);
    }
    private void openSingleInterventionFragment(Vector<String> intervention) {
        SingleIntervention singleIntervention = new SingleIntervention();
        Bundle args = new Bundle();
        args.putInt("InterventionID", Integer.parseInt(intervention.get(0)));
        args.putString("InterventionName", intervention.get(1));
        args.putString("InterventionDescription", intervention.get(2));
        singleIntervention.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), singleIntervention, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }
    private Vector<Button> populateButtonList(Vector<Vector<String>> interventionList, View view) {
        int i = 0;
        Vector<Button> buttonList = new Vector<Button>();
        for (Vector<String> interventionObject : interventionList) {
            linearLayoutToAdd = new LinearLayout(getContext());
            linearLayoutToAdd.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayoutToAdd.setOrientation(LinearLayout.HORIZONTAL);
            linearLayoutToAdd.setGravity(Gravity.LEFT);
            //linearLayoutToAdd.setDividerDrawable(getDrawable(R.drawable.divider_big));
            linearLayoutToAdd.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            //linearLayoutToAdd.setBackground(getDrawable(R.drawable.linear_layout_border));


            //ConstraintLayout cl = view.findViewById(R.id.singleInterventionConstraintLayout);
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(407, 58);
            lp.leftMargin = 0;
            lp.topMargin = 58 * i;
            //cl.addView(newButton, lp);
            //buttonList.add(newButton);
            i++;
        }
        return buttonList;
    }
    private Vector<Vector<String>> getSavedInterventions() throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String savedInterventionsString = dsHelper.readSavedDataFromInternalStorage("interventions.txt", getContext());

        JSONArray jsonArray = new JSONArray(savedInterventionsString);          // J of J of S
        Vector<JSONArray> intermediateList = new Vector<JSONArray>();
        for(int i=0; i < jsonArray.length(); i++) {
            intermediateList.add(jsonArray.getJSONArray(i));                    // V of J of S
        }
        Vector<Vector<String>> endList = new Vector<Vector<String>>();
        for(int j=0; j < intermediateList.size(); j++) {                        // Jedes JSONA im Vector
            Vector<String> temp = new Vector<String>();
            for(int k = 0; k < intermediateList.get(j).length(); k++) {         // Jeden Eintrag im JSONA im Vector
                temp.add((String) intermediateList.get(j).get(k));              // V of S
                endList.add(temp);                                              // V of V of S
            }
        }
        return endList;
    }
    private void saveInterventions(Vector<Vector<String>> interventions) throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        JSONArray endArray = new JSONArray();
        // Start: V of V of S
        JSONArray intermediateArr = new JSONArray();
        for(int i=0; i < interventions.size(); i++) {
            intermediateArr.put(interventions.get(i));                          // J of V of S
        }
        for(int j=0; j < intermediateArr.length(); j++) {                        // Jeder Vector im JSONA
            JSONArray temp = new JSONArray();
            Vector<String> elem = (Vector<String>)intermediateArr.get(j);
            for(int k = 0; k < elem.size(); k++) {                              // Jeden Eintrag im Vector im JSONA
                temp.put((String) (elem.get(k)));                               // J of S
                endArray.put(temp);                                             // J of J of S
            }
        }
        dsHelper.writeDataToInternalStorage("interventions.txt", endArray.toString(), getContext());
    }

}