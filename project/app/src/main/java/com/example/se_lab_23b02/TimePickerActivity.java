package com.example.se_lab_23b02;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TimePickerActivity extends Activity {

    private final int interval = 5;

    //----------GUI Elements----------
    private ConstraintLayout constraintLayout;
    private Button btSubmit;
    private Button btDestroy;
    private DataSavingHelper dsHelper;
    private Context contextForColor;

    private SeekBar sliderBar;
    private TextView tv;
    private TextView tvOut;
    private TextView tvTitle;


    //--------Context and Resources for Language----
    private Context context;
    private Resources resources;
    private ArrayList<Object> elementsForLanguageChange = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_picker_for_notifications);
        this.setFinishOnTouchOutside(false);

        tv = (TextView) findViewById(R.id.tvSlot);
        btDestroy = (Button) findViewById(R.id.btDestroy);
        sliderBar = (SeekBar) findViewById(R.id.sbTimePicker);
        tvOut = (TextView) findViewById(R.id.tvTime);
        btSubmit = (Button) findViewById(R.id.btSubmitNewTime);
        constraintLayout = findViewById(R.id.constraintLayoutTimerPicker);
        tvTitle = findViewById(R.id.tvTimePickerText1);


        elementsForLanguageChange.add(btDestroy);
        elementsForLanguageChange.add(btSubmit);
        elementsForLanguageChange.add(tvTitle);

        dsHelper = DataSavingHelper.getInstance();
        refreshResources();
        setLanguage();


        Bundle b = getIntent().getExtras();

        int id = (int) b.get("id");
        int lb = (int) b.get("lowerBound");
        int ub = (int) b.get("upperBound");
        final int[] cHour = {(int) b.get("currentHour")};
        final int[] cMin = {(int) b.get("currentMin")};
        createNotificationChannel();

        // set time frame in Textview
        String text = lb + ":00 - " + ub + ":00";
        tv.setText(text);

        int stepsPerHour = 60/interval;
        // calc Steps (15 min steps)
        int diff = ub - lb;
        int steps = diff*stepsPerHour;
        sliderBar.setMax(steps);


        //calculate current progress
        int prog = (cHour[0] - lb) * stepsPerHour + cMin[0] / interval;
        sliderBar.setProgress(prog);
        if(cMin[0] == 0)
            tvOut.setText(resources.getString(R.string.timepicker_selected) + cHour[0] + ":00");
        else if(cMin[0] == 5)
            tvOut.setText(resources.getString(R.string.timepicker_selected) + cHour[0] + ":05");
        else
            tvOut.setText(resources.getString(R.string.timepicker_selected) + cHour[0] + ":" + cMin[0]);
        sliderBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //convert seekbar steps to real time and show result on tvOut
                int result = seekBar.getProgress();
                int hours = lb + ((int) result /stepsPerHour);
                int mins = (result % stepsPerHour) * interval;
                cHour[0] = hours;
                cMin[0] = mins;
                if(mins == 0)
                    tvOut.setText(resources.getString(R.string.timepicker_selected) + hours + ":00");
                else if (mins == 5)
                    tvOut.setText(resources.getString(R.string.timepicker_selected)+ hours + ":05");
                else
                    tvOut.setText(resources.getString(R.string.timepicker_selected) + hours + ":" + mins);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alarm cur = new Alarm(id);
                cur.addTime(cHour[0], cMin[0]);
                cur.setAlarm(TimePickerActivity.this);
                String dataFromStorage = dsHelper.readSavedDataFromInternalStorage("Timers",getApplicationContext());
                String data = "";
                switch(id) {
                    case 0:
                        data = "mornCurH:" + String.valueOf(cHour[0]) + ",mornCurM:" + String.valueOf(cMin[0]);
                        //dsHelper.saveSettingAsSharedPref("mornCurH", Integer.toString(cHour[0]), getApplicationContext());
                        //dsHelper.saveSettingAsSharedPref("mornCurM", Integer.toString(cMin[0]), getApplicationContext());
                        break;
                    case 1:
                        data = "noonCurH:" + String.valueOf(cHour[0]) + ",noonCurM:" + String.valueOf(cMin[0]);
                        //dsHelper.saveSettingAsSharedPref("noonCurH", Integer.toString(cHour[0]), getApplicationContext());
                        //dsHelper.saveSettingAsSharedPref("noonCurM", Integer.toString(cMin[0]), getApplicationContext());
                        break;
                    case 2:
                        data = "evnCurH:" + String.valueOf(cHour[0]) + ",evnCurM:" + String.valueOf(cMin[0]);
                        //dsHelper.saveSettingAsSharedPref("evnCurH", Integer.toString(cHour[0]), getApplicationContext());
                        //dsHelper.saveSettingAsSharedPref("evnCurM", Integer.toString(cMin[0]), getApplicationContext());
                        break;
                }

                if(!dataFromStorage.equals("")){
                    String[] timeslots = dataFromStorage.split("\n");
                    boolean changed = false;
                    for(int i = 0; i < timeslots.length;i++){
                        if(timeslots[i].contains(data.split(":")[0]))
                        {
                            timeslots[i] = data;
                            changed = true;
                            break;
                        }
                    }
                    String oldData = data;
                    data = "";

                    for(int i = 0; i < timeslots.length; i++){
                        String s = timeslots[i].replace("\n","");
                        if(s.equals(""))
                            continue;
                        data += s + "\n";
                   }
                    if(!changed)
                        data += oldData;
                }

                dsHelper.writeDataToInternalStorage("Timers",data,getApplicationContext());
                Toast.makeText(TimePickerActivity.this, "Alarm set successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setColorTheme();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            CharSequence name = "AlarmClockChannel";
            String description = "Channel for the ALarm Clock";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("AlarmClock",name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    private void setColorTheme()
    {
        int ButtonTransparency = 50;
        contextForColor = this.getApplicationContext();
        String mode = dsHelper.getInstance().getSpecificSettingsFromSharedPref("dmValue",contextForColor);
        String color = dsHelper.getInstance().getSpecificSettingsFromSharedPref("color",contextForColor);
        switch(mode)
        {
            case "dark-mode":
                ButtonTransparency = 70;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                tv.setTextColor(getColor(R.color.dark_mode_color_on_primary));
                tvOut.setTextColor(getColor(R.color.dark_mode_color_on_primary));
                tvTitle.setTextColor(getColor(R.color.dark_mode_color_on_primary));

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
                ButtonTransparency = 150;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                tv.setTextColor(getColor(R.color.black));
                tvOut.setTextColor(getColor(R.color.black));
                tvTitle.setTextColor(getColor(R.color.black));
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
        btDestroy.getBackground().setAlpha(ButtonTransparency);
        btSubmit.getBackground().setAlpha(ButtonTransparency);

    }



    private void refreshResources() {

        String language = DataSavingHelper.getInstance().getSelectedLanguage(this.getApplicationContext());

        String language_short = "en";

        switch(language) {
            case "francais":
                language_short = "fr";
                break;
            case "deutsch":
                language_short = "de";
                break;
        }
        context = LocaleHelper.setLocale(this.getApplicationContext(), language_short);
        resources = context.getResources();
    }
    private void setLanguage() {
        Button bt = (Button) elementsForLanguageChange.get(0);
        bt.setText(resources.getString(R.string.timepicker_cancel));
        bt = (Button) elementsForLanguageChange.get(1);
        bt.setText(resources.getString(R.string.timepicker_submit));
        TextView languageTv = (TextView) elementsForLanguageChange.get(2);
        languageTv.setText(resources.getString(R.string.timepicker_preferred_choice));

    }

}
