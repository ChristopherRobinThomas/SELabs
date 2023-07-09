package com.example.se_lab_23b02;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class TimePickerActivity extends Activity {

    private final int interval = 5;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_picker_for_notifications);
        this.setFinishOnTouchOutside(false);

        TextView tv = (TextView) findViewById(R.id.tvSlot);
        Button btDestroy = (Button) findViewById(R.id.btDestroy);
        SeekBar sliderBar = (SeekBar) findViewById(R.id.sbTimePicker);
        TextView tvOut = (TextView) findViewById(R.id.tvTime);
        Button btSubmit = (Button) findViewById(R.id.btSubmitNewTime);

        DataSavingHelper dsHelper = DataSavingHelper.getInstance();

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
            tvOut.setText("selected: " + cHour[0] + ":00");
        else if(cMin[0] == 5)
            tvOut.setText("selected: " + cHour[0] + ":05");
        else
            tvOut.setText("selected: " + cHour[0] + ":" + cMin[0]);

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
                    tvOut.setText("selected: " + hours + ":00");
                else if (mins == 5)
                    tvOut.setText("selected: " + hours + ":05");
                else
                    tvOut.setText("selected: " + hours + ":" + mins);

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
                        data = "mornCurH:" + String.valueOf(cHour[0]) + ",mornCurM" + String.valueOf(cMin[0]);
                        //dsHelper.saveSettingAsSharedPref("mornCurH", Integer.toString(cHour[0]), getApplicationContext());
                        //dsHelper.saveSettingAsSharedPref("mornCurM", Integer.toString(cMin[0]), getApplicationContext());
                        break;
                    case 1:
                        data = "noonCurH:" + String.valueOf(cHour[0]) + ",noonCurM" + String.valueOf(cMin[0]);
                        //dsHelper.saveSettingAsSharedPref("noonCurH", Integer.toString(cHour[0]), getApplicationContext());
                        //dsHelper.saveSettingAsSharedPref("noonCurM", Integer.toString(cMin[0]), getApplicationContext());
                        break;
                    case 2:
                        data = "evnCurH:" + String.valueOf(cHour[0]) + ",evnCurM" + String.valueOf(cMin[0]);
                        //dsHelper.saveSettingAsSharedPref("evnCurH", Integer.toString(cHour[0]), getApplicationContext());
                        //dsHelper.saveSettingAsSharedPref("evnCurM", Integer.toString(cMin[0]), getApplicationContext());
                        break;
                }

                if(!dataFromStorage.equals("")){
                    String[] timeslots = dataFromStorage.split("\n");
                    for(int i = 0; i < timeslots.length;i++){
                        if(timeslots[i].contains(data.split(":")[0]))
                            timeslots[i] = data;
                    }
                    data = "";
                    for(int i = 0; i < timeslots.length;i++){
                        if(i < timeslots.length - 1 )
                            data += timeslots[i] + "\n";
                        else
                            data += timeslots[i];
                   }
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
}
