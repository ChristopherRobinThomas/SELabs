package com.example.se_lab_23b02;

import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeStampTest {

    @Test
    public void testGetTimeSlotFromString() {

        assertEquals(TimeStamp.TimeSlot.morning, TimeStamp.getTimeSlotFromString("2023-06-14 07:45:54"));
        assertEquals(TimeStamp.TimeSlot.noon, TimeStamp.getTimeSlotFromString("2023-06-12 12:41:51"));
        assertEquals(TimeStamp.TimeSlot.evening, TimeStamp.getTimeSlotFromString("2023-06-13 18:50:49"));
        assertEquals(TimeStamp.TimeSlot.no_TimeSLot, TimeStamp.getTimeSlotFromString("2023-06-14 14:26:14"));
    }

    @Test
    public void testGetCurrentTimeStamp() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now().format(dateTimeFormatter);
        assertEquals(time, TimeStamp.getCurrentTimeStamp());
    }

}
