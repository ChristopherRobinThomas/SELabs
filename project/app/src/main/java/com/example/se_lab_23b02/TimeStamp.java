package com.example.se_lab_23b02;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
        return lt.toString();
    }

}
