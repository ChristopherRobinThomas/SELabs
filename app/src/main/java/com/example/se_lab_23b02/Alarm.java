package com.example.se_lab_23b02;



import android.content.Context;
import android.view.View;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;


public class Alarm
{
    private static ArrayList<Integer> ids = new ArrayList<Integer>();

    private Calendar calendar;
    private int id;

    private Context context;

    Alarm(Context _context)
    {
        context = _context;
    }

    public  void addTime(MaterialTimePicker picker)
    {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
        calendar.set(Calendar.MINUTE,picker.getMinute());
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
    }


    public void addAlarm(android.widget.Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void cancelAlarm(android.widget.Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /*
    public void setAlarmTime(android.widget.Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) { showTimePicker(); }
        });
    }
    */
}
