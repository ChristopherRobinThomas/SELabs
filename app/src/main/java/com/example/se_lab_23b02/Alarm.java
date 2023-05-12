package com.example.se_lab_23b02;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;


public class Alarm
{
    private static ArrayList<Integer> ids = new ArrayList<Integer>();

    private Calendar calendar;
    private int id;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    Alarm()
    {
        id = 0;
    }

    public  void addTime(MaterialTimePicker picker)
    {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
        calendar.set(Calendar.MINUTE,picker.getMinute());
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
    }


    public void setAlarm(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),10,pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        Log.d("setAlarm","ALarm set");

    }

    public void cancelAlarm() {
    }

}
