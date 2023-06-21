package com.example.se_lab_23b02;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    Button buttonIntervention1;
    Button buttonIntervention2;
    Button buttonIntervention3;
    Button buttonAddNewIntervention;
    Vector<Vector<String>> interventionList;



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

        try {
            interventionList = getSavedInterventions();
        } catch (Exception e) {
            interventionList = new Vector<Vector<String>>();
            Vector<String> sampleInterventionA = new Vector<String>();
            sampleInterventionA.add("00000000");
            sampleInterventionA.add("Sample Name");
            sampleInterventionA.add("Sample Desc");
            interventionList.add(sampleInterventionA);
        }

        Vector<Button> buttonList = populateButtonList(interventionList, view);
        buttonAddNewIntervention = view.findViewById(R.id.buttonAddNewIntervention);
        for (Button b : buttonList) {
            b.setOnClickListener(v -> {
                openSingleInterventionFragment(b.getText().toString());
            });
        }
        buttonAddNewIntervention.setOnClickListener(v -> {});
        
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
    private Vector<Button> populateButtonList(Vector<Vector<String>> interventionList, View view) {
        int i = 0;
        Vector<Button> buttonList = new Vector<Button>();
        for (Vector<String> interventionObject : interventionList) {
            Button newButton = new Button(this.getContext());
            newButton.setText(interventionObject.get(1));

            ConstraintLayout cl = view.findViewById(R.id.singleInterventionConstraintLayout);
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(407, 58);
            lp.topMargin = 58 * i;
            cl.addView(newButton, lp);
            buttonList.add(newButton);
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