package com.example.se_lab_23b02;

import android.content.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
        Alarm cur = new Alarm(0);
        cur.addTime(7, 0);
        cur = new Alarm(1);
        cur.addTime(12, 0);
        cur = new Alarm(2);
        cur.addTime(17, 0);

        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String data;
        data = "mornCurH:" + String.valueOf(7) + ",mornCurM" + String.valueOf(0) + "\n";
        data += "noonCurH:" + String.valueOf(12) + ",noonCurM" + String.valueOf(0) + "\n";
        data += "evnCurH:" + String.valueOf(17) + ",evnCurM" + String.valueOf(0);
        dsHelper.writeDataToInternalStorage("Timers",data,context);
    }

}
