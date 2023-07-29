package com.example.se_lab_23b02;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
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
import android.widget.ScrollView;
import android.widget.TextView;

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
    DataSavingHelper dsHelper;
    private LinearLayout innerLinearLayout;

    private TextView txtViewInterventions;
    private LinearLayout linearLayoutToAdd;
    private LinearLayout linearLayout;
    private ScrollView sv;
    private ConstraintLayout constraintLayout;
    private Context contextForColor;
    private Button btStartRecording;

    private Context context;
    private Resources resources;
    private ArrayList<Object> elementsForLanguageChange = new ArrayList<>();


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
        dsHelper = DataSavingHelper.getInstance();
        linearLayout = view.findViewById(R.id.linearLayoutInterventions);
        sv = view.findViewById(R.id.interventionsScrollView);
        buttonAddNewIntervention = view.findViewById(R.id.buttonAddNewIntervention);
        constraintLayout = view.findViewById(R.id.interventionsFragment);
        innerLinearLayout = view.findViewById(R.id.linearLayoutInInterventions);
        btStartRecording = view.findViewById(R.id.btStartRecording);
        elementsForLanguageChange.add(buttonAddNewIntervention);

        setColorTheme();
        refreshResources();
        setLanguage();

        try {
            interventionList = dsHelper.getSavedInterventions(this.getContext());
        } catch (JSONException ignored) {}

        populateButtonList(interventionList);

        buttonAddNewIntervention.setOnClickListener(v -> {
            showAddInterventionDialogue(view);
        });

        btStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timestamp = TimeStamp.getCurrentTimeStamp();
                SensorManager.toggleService(getActivity(), timestamp);
            }
        });
        return view;
    }
    private void showAddInterventionDialogue(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Enter Intervention Name");
        final EditText input = new EditText(this.getContext());
        //input.setInputType(InputType.TYPE_CLASS_TEXT);
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

        try {
            dsHelper.saveInterventions(interventionList,getActivity().getApplicationContext());
        } catch (JSONException ignored) {}

        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .detach(this).commit();
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .attach(this).commit();
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
    private void populateButtonList(Vector<Vector<String>> interventionList) {
        contextForColor = getActivity().getApplicationContext();
        String mode = dsHelper.getInstance().getSpecificSettingsFromSharedPref("dmValue",contextForColor);
        for (Vector<String> interventionObject : interventionList) {
            linearLayoutToAdd = new LinearLayout(getActivity());
            linearLayoutToAdd.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayoutToAdd.setOrientation(LinearLayout.HORIZONTAL);
            linearLayoutToAdd.setGravity(Gravity.CENTER);
            if(mode.equals("white-mode")) linearLayoutToAdd.setBackground(getResources().getDrawable(R.drawable.step_count_border_light_mode));
            else linearLayoutToAdd.setBackground(getResources().getDrawable(R.drawable.step_count_border));

            txtViewInterventions = new TextView(getActivity());
            txtViewInterventions.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewInterventions.setText(interventionObject.get(1));
            txtViewInterventions.setTextSize(20);
            linearLayoutToAdd.addView(txtViewInterventions);

            linearLayoutToAdd.setOnClickListener(v -> openSingleInterventionFragment(interventionObject));
            innerLinearLayout.addView(linearLayoutToAdd);
        }
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
                btStartRecording.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_skip_next_24_dark_mode,0,0,0);
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
                btStartRecording.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_skip_next_24_light_mode,0,0,0);
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
        buttonAddNewIntervention.getBackground().setAlpha(ButtonTransparency);
    }




    private void refreshResources() {

        String language = DataSavingHelper.getInstance().getSelectedLanguage(getActivity().getApplicationContext());

        String language_short = "en";

        switch(language) {
            case "francais":
                language_short = "fr";
                break;
            case "deutsch":
                language_short = "de";
                break;
        }
        context = LocaleHelper.setLocale(getActivity().getApplicationContext(), language_short);
        resources = context.getResources();
    }
    private void setLanguage() {
        Button bt = (Button) elementsForLanguageChange.get(0);
        bt.setText(resources.getString(R.string.add_intervention_button));

    }
}