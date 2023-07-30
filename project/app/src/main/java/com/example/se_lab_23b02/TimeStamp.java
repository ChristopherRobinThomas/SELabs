/*
 * Copyright (c) 2023.
 * Christopher Thomas, Miriam Fee, Linus Brandt, Maximilian Frese, Frederik Hüttemann
 * All rights. reserved.
 */

package com.example.se_lab_23b02;

import android.content.Context;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class TimeStamp {
    public enum TimeSlot
    {
        morning,
        noon,
        evening,
        no_TimeSLot
    };
    static private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static public TimeSlot getTimeSlotFromString(String s)
    {
        String clockTime = s.split(" ")[1];
        int hour = Integer.parseInt(clockTime.split(":")[0]);
        if(hour >= 7 && hour < 11)
            return TimeSlot.morning;
        if(hour >= 12 && hour < 14)
            return TimeSlot.noon;
        if(hour >= 17 && hour < 19)
            return TimeSlot.evening;

        return TimeSlot.no_TimeSLot;
    }

    static public TimeSlot getCurrentTimeSlot()
    {
        LocalDateTime now = LocalDateTime.now();
        String clockTime = dateTimeFormatter.format(now).split(" ")[1];
        int hour = Integer.parseInt(clockTime.split(":")[0]);
        if(hour >= 7 && hour < 11)
            return TimeSlot.morning;
        if(hour >= 12 && hour < 14)
            return TimeSlot.noon;
        if(hour >= 17 && hour < 19)
            return TimeSlot.evening;

        return TimeSlot.no_TimeSLot;
    }

    static public String getCurrentTimeStamp()
    {
        LocalDateTime now = LocalDateTime.now();
        return dateTimeFormatter.format(now);
    }

    public static LocalDateTime getLocalDateTime(Object dateAsObject) {
        String dateAsString = (String) dateAsObject;
        String[] times = dateAsString.split(" ");
        LocalDate tmpLocalDate = LocalDate.parse(times[0]);
        LocalTime tmpLocalTime = LocalTime.parse(times[1]);
        LocalDateTime ldtTimestampAsDate = LocalDateTime.of(tmpLocalDate, tmpLocalTime);
        return ldtTimestampAsDate;
    }
    static public void setStandardTimers(Context context)
    {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String data;
        data = dsHelper.readSavedDataFromInternalStorage("Timers",context);

        if(!data.equals(""))
        {
            boolean morn = false;
            boolean noon = false;
            boolean evn = false;
            String[] values = data.split("\n");
            for(int i = 0; i < values.length;i++)
            {
                int id;
                if(values[i].contains("morn"))
                {
                    id = 0;
                    morn = true;
                } else if (values[i].contains("noon")) {
                    id = 1;
                    noon = true;
                } else if (values[i].contains("evn")) {
                    id = 2;
                    evn = true;
                }else
                {
                    return;
                }
                int hour = Integer.parseInt(values[i].split(",")[0].split(":")[1].replace(":",""));
                int min = Integer.parseInt(values[i].split(",")[1].split(":")[1].replace(":",""));
                Alarm cur = new Alarm(id);
                cur.addTime(hour, min);
                cur.setAlarm(context);
            }
            if(!morn) {
                data += "mornCurH:" + String.valueOf(7) + ",mornCurM:" + String.valueOf(0) + "\n";
                Alarm cur = new Alarm(0);
                cur.addTime(7, 0);
                cur.setAlarm(context);
            }
            if(!noon){
                data += "noonCurH:" + String.valueOf(12) + ",noonCurM:" + String.valueOf(0) + "\n";
                Alarm cur = new Alarm(1);
                cur.addTime(12, 0);
                cur.setAlarm(context);
            }
            if(!evn){
                Alarm cur = new Alarm(2);
                cur.addTime(17, 0);
                data += "evnCurH:" + String.valueOf(17) + ",evnCurM:" + String.valueOf(0) + "\n";
                cur.setAlarm(context);
            }
            return;
        }
        Alarm cur = new Alarm(0);
        cur.addTime(7, 0);
        cur.setAlarm(context);
        cur = new Alarm(1);
        cur.addTime(12, 0);
        cur.setAlarm(context);
        cur = new Alarm(2);
        cur.addTime(17, 0);
        cur.setAlarm(context);

        data = "mornCurH:" + String.valueOf(7) + ",mornCurM:" + String.valueOf(0) + "\n";
        data += "noonCurH:" + String.valueOf(12) + ",noonCurM:" + String.valueOf(0) + "\n";
        data += "evnCurH:" + String.valueOf(17) + ",evnCurM:" + String.valueOf(0);
        dsHelper.writeDataToInternalStorage("Timers",data,context);
    }
    public static boolean timeSlotExistsAlready(Context context) {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();

        String text = dsHelper.readSavedDataFromInternalStorage("questionnaire.txt",context);
        if(text.equals(""))
            return false;
        String[] allQuestionnaire = text.split(System.lineSeparator());
        String currentTimeStamp = TimeStamp.getCurrentTimeStamp();
        TimeStamp.TimeSlot currentTimeSlot = TimeStamp.getCurrentTimeSlot();
        for(int i = 0; i < allQuestionnaire.length; i++){
            if( allQuestionnaire[i].equals(""))
                continue;

            String temp = allQuestionnaire[i];
            temp = temp.split("\":")[10].split(",")[0].replace("\"","");
            String day = temp.split(" ")[0];
            TimeStamp.TimeSlot timeS = TimeStamp.getTimeSlotFromString(temp);
            if(day.equals(currentTimeStamp.split(" ")[0]) && timeS == currentTimeSlot)
                return true;
        }
        return false;
    }
    public static LocalDateTime getLocalDateTimeFromSeconds(int seconds) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds), TimeZone.getDefault().toZoneId());
        return ldt;

    }

    public static LocalTime getLocalTimeFromSeconds(int seconds) {
        LocalDateTime ldt = getLocalDateTimeFromSeconds(seconds);
        return ldt.toLocalTime();
    }

    public static LocalDate getLocalDateFromSeconds(int seconds) {
        LocalDateTime ldt = getLocalDateTimeFromSeconds(seconds);
        return ldt.toLocalDate();
    }

    public static String getLocalTimeAsStringFromSeconds(int seconds) {
        LocalTime lt = getLocalTimeFromSeconds(seconds);
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(0, 0, 0, lt.getHour(), lt.getMinute(), lt.getSecond());
        String formatted = df.format(date);
        return formatted;
    }
}
