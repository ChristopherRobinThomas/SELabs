package com.example.se_lab_23b02;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeStamp {
    public enum TimeSlot
    {
        morning,
        noon,
        evening,
        no_TimeSLot
    };
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public TimeSlot getTimeSlotFromString(String s)
    {
        String clockTime = s.split(" ")[1];
        int hour = Integer.parseInt(clockTime.split(":")[0]);
        if(hour >= 7 && hour < 11)
            return TimeSlot.morning;
        if(hour >= 12 && hour < 14)
            return TimeSlot.noon;
        if(hour >= 17 && hour < 19)
            return TimeSlot.evening;

        return  TimeSlot.no_TimeSLot;
    }

    public TimeSlot getCurrentTimeSlot()
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

    public String getCurrentTimeStamp()
    {
        LocalDateTime now = LocalDateTime.now();
        return dateTimeFormatter.format(now);
    }
}
