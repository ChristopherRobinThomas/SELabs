package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.example.se_lab_23b02.databinding.ActivityHomeScreenBinding;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;


public class HomeScreenActivity extends AppCompatActivity {


    private ActivityHomeScreenBinding binding;

    private Context context;
    private Resources resources;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.bottomNavigation.setSelectedItemId(R.id.home);

        refreshResources();

        binding.bottomNavigation.setOnItemSelectedListener(item ->
        {
            refreshResources();
            switch(item.getItemId())
            {
                case R.id.home:

                    replaceFragment(new HomeFragment());
                    setTitle(resources.getString(R.string.home_heading));
                    break;
                case R.id.settings:
                    replaceFragment(new SettingsFragment());
                    setTitle(resources.getString(R.string.settings_heading));
                    break;
                case R.id.data:
                    replaceFragment(new OverviewFragment());
                    setTitle(resources.getString(R.string.overview_heading));
                    break;
                case R.id.interventions:
                    replaceFragment(new InterventionsFragment());
                    setTitle(resources.getString(R.string.interventions_heading));
                    break;
            }

            return true;
        });

        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        dsHelper.writeDataToInternalStorage("interventions.txt", "", HomeScreenActivity.this);
        Vector<Vector<String>> emptyV = new Vector<Vector<String>>();
        Vector<Vector<String>> tempV = new Vector<Vector<String>>();
        try {
            tempV = getSavedInterventions();
        } catch (JSONException e) {}
        if(tempV.equals(emptyV)) {
            Vector<Vector<String>> newInterventions = new Vector<Vector<String>>();
            Vector<String> sampleInterventionA = new Vector<String>();
            sampleInterventionA.add("0");
            sampleInterventionA.add("Push-Ups");
            sampleInterventionA.add("Perform 10 push-ups.");
            newInterventions.add(sampleInterventionA);
            Vector<String> sampleInterventionB = new Vector<String>();
            sampleInterventionB.add("1");
            sampleInterventionB.add("Sit-Ups");
            sampleInterventionB.add("Perform 10 sit-ups.");
            newInterventions.add(sampleInterventionB);
            Vector<String> sampleInterventionC = new Vector<String>();
            sampleInterventionC.add("2");
            sampleInterventionC.add("Light Run");
            sampleInterventionC.add("Run a comfortable distance at a slow pace. Our recommendation: 2 kilometers in 10 minutes");
            newInterventions.add(sampleInterventionC);
            try {
                saveInterventions(newInterventions);
            } catch (JSONException e) {}
        }
        try {
            Intent sourceIntent = getIntent();
            boolean doRandIntv = sourceIntent.getBooleanExtra("openRandomIntervention", false);
            if(doRandIntv) {
                Log.i("HomeScreen", "Trying to open random intervention");
                Vector<Vector<String>> allI = getSavedInterventions();
                int randomNum = ThreadLocalRandom.current().nextInt(0, allI.size());
                Vector<String> randomIntervention = allI.get(randomNum);
                SingleIntervention singleIntervention = new SingleIntervention();
                Bundle args = new Bundle();
                args.putInt("InterventionID", Integer.parseInt(randomIntervention.get(0)));
                args.putString("InterventionName", randomIntervention.get(1));
                args.putString("InterventionDescription", randomIntervention.get(2));
                singleIntervention.setArguments(args);
                replaceFragment(singleIntervention);
                setTitle(resources.getString(R.string.interventions_heading));
            }
        } catch (Exception ignored) {}
    }
    private Vector<Vector<String>> getSavedInterventions() throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String savedInterventionsString = dsHelper.readSavedDataFromInternalStorage("interventions.txt", HomeScreenActivity.this);
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
                // V of V of S
            }
            endList.add(temp);
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
                // J of J of S
            }
            endArray.put(temp);
        }
        dsHelper.writeDataToInternalStorage("interventions.txt", endArray.toString(), HomeScreenActivity.this);
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
        context = LocaleHelper.setLocale(getApplicationContext(), language_short);
        resources = context.getResources();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
