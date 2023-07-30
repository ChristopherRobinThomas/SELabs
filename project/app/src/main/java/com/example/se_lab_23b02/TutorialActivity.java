package com.example.se_lab_23b02;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import java.util.HashMap;

public class TutorialActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        Button returnButton = (Button) findViewById(R.id.btReturnToSettings);


        HashMap<String, String> settings = (HashMap<String, String>) DataSavingHelper.getInstance().getAllSettingsFromSharedPref(getApplicationContext());

        String language = settings.get("language");

        Context context;

        if(language.equals("deutsch")) {
            context = LocaleHelper.setLocale(getApplicationContext(), "de");
        } else if (language.equals("english")) {
            context = LocaleHelper.setLocale(getApplicationContext(), "en");
        } else {
            context = LocaleHelper.setLocale(getApplicationContext(), "fr");
        }

        Resources resources = context.getResources();
        returnButton.setText(resources.getString(R.string.bt_tutorial_return));

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endActivity();
            }
        });

        VideoView videoView = (VideoView) findViewById(R.id.vvTutorial);
        videoView.setVideoURI(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.selab_2));
        videoView.start();
    }
    @Override
    public void onBackPressed() {
        endActivity();
    }

    private void endActivity() {
        Intent myIntent = new Intent(this, HomeScreenActivity.class);
        startActivity(myIntent);
        finish();
    }
}
