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


    public Alarm(int _id)
    {
        id = _id;
    }

    public void addTime(MaterialTimePicker picker)
    {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
        calendar.set(Calendar.MINUTE,picker.getMinute());
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
    }

    public void addTime(int hour, int minute)
    {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
    }


    public void setAlarm(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),10,pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d("setAlarm","Alarm set");
    }

    public void cancelAlarm(Context context) {
        try {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
            Log.d("Alarm","Alarm got canceled : " + String.valueOf(id));
        }catch (Exception e)
        {
            Log.d("Alarm","Alarm couldn't be canceled");
        }
    }
}
