package com.example.se_lab_23b02;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TimePickerActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_picker_for_notifications);
        this.setFinishOnTouchOutside(false);

        TextView tv = (TextView) findViewById(R.id.tvSlot);
        Button btDestroy = (Button) findViewById(R.id.btDestroy);
        SeekBar sliderBar = (SeekBar) findViewById(R.id.sbTimePicker);
        TextView tvOut = (TextView) findViewById(R.id.tvTime);

        Bundle b = getIntent().getExtras();

        int lb = (int) b.get("lowerBound");
        int ub = (int) b.get("upperBound");
        int cHour = (int) b.get("currentHour");
        int cMin = (int) b.get("currentMin");





        // set time frame in Textview
        String text = lb + ":00 - " + ub + ":00";
        tv.setText(text);

        // calc Steps (15 min steps)
        int diff = ub - lb;
        int steps = diff*4;
        sliderBar.setMax(steps);


        //calculate current progress
        int prog = (cHour - lb) * 4 + cMin / 15;
        sliderBar.setProgress(prog);
        if(cMin == 0)
            tvOut.setText("selected: " + cHour + ":00");
        else
            tvOut.setText("selected: " + cHour + ":" + cMin);

        sliderBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //convert seekbar steps to real time and show result on tvOut
                int result = seekBar.getProgress();
                int hours = lb + ((int) result /4);
                int mins = (result % 4) * 15;
                if(mins == 0)
                    tvOut.setText("selected: " + hours + ":00");
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


        btDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
