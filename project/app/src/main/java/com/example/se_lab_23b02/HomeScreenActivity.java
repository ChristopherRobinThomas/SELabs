package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import com.example.se_lab_23b02.databinding.ActivityHomeScreenBinding;


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
