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
    DataSavingHelper dsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.bottomNavigation.setSelectedItemId(R.id.home);

        refreshResources();
        dsHelper = DataSavingHelper.getInstance();

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

        tryOpenRandomIntervention();

    }

    private void tryOpenRandomIntervention() {
        ensureInterventions();
        try {
            Intent sourceIntent = getIntent();
            boolean doRandIntv = sourceIntent.getBooleanExtra("openRandomIntervention", false);
            if(doRandIntv) {
                Log.i("HomeScreen", "Trying to open random intervention");
                Vector<Vector<String>> allI = dsHelper.getSavedInterventions(HomeScreenActivity.this);
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

    private void ensureInterventions() {
        Vector<Vector<String>> emptyV = new Vector<Vector<String>>();
        Vector<Vector<String>> tempV = new Vector<Vector<String>>();
        try {
            tempV = dsHelper.getSavedInterventions(HomeScreenActivity.this);
        } catch (JSONException e) {}
        if(tempV.equals(emptyV)) {
            try {
                dsHelper.saveInterventions(dsHelper.getSampleInterventions(), HomeScreenActivity.this);
            } catch (JSONException e) {}
        }
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
