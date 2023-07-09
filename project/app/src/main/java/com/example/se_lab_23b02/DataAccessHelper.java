package com.example.se_lab_23b02;

import android.content.Context;
import android.util.Pair;

import androidx.constraintlayout.motion.widget.Debug;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.sql.SQLOutput;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

import kotlin.Triple;

public class DataAccessHelper {

    // to divide different
    public static final int ACCESS_MENTAL_HEALTH_SCORE = 1;
    public static final int ACCESS_COUNT_OF_INTERVENTIONS = 2;
    public static final int ACCESS_DAILY_TASK_DISTRIBUTION = 3;

    public static final String[] LIST_OF_DAILY_TASKS_TO_INDEX = {"5.000 Steps", "10.000 Steps", "Drink 2L", "Clean", "Walk", "Meditate", "Workout", "other"};


    private static DataAccessHelper INSTANCE;
    private DataSavingHelper dsHelper;
    private DataAccessHelper() {
        dsHelper = DataSavingHelper.getInstance();
    }
    // Singleton Pattern - yehee

    public static DataAccessHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataAccessHelper();
        }
        return INSTANCE;
    }

    // returns a list of all the wanted data (accessMode) in the given time span
    public ArrayList<Pair<Integer, Integer>> collectDataFromTimeframe(LocalDateTime lowerBorder, LocalDateTime upperBorder, int accessMode, Context context) throws JSONException {
        ArrayList<Pair<Integer, Integer>> outputList = new ArrayList<>();
        String fileContent;

        switch (accessMode) {
            // case: list of mental health scores
            case ACCESS_MENTAL_HEALTH_SCORE:
                fileContent = dsHelper.readSavedDataFromInternalStorage("questionnaire.txt", context);
                String[] linesForMentalHealth = fileContent.split(System.lineSeparator());
                // reads data from file and then checks for each element if it is within the given timeframe
                for (int i = 1; i < linesForMentalHealth.length; i++) {
                    JSONObject jsObject = new JSONObject(linesForMentalHealth[i]);
                    LocalDateTime timestampAsLocalDateTime = TimeStamp.getLocalDateTime(jsObject.get("timestamp"));
                    if(timestampAsLocalDateTime.compareTo(ChronoLocalDateTime.from(lowerBorder)) >= 0 && timestampAsLocalDateTime.compareTo(ChronoLocalDateTime.from(upperBorder)) <= 0) {
                        int hours = (int) ChronoUnit.HOURS.between(lowerBorder, timestampAsLocalDateTime);
                        Pair<Integer, Integer> newEntry = new Pair<>(calculateHealthScoreFromQuestionaire(linesForMentalHealth[i]), hours);
                        outputList.add(newEntry);
                    }
                }
                break;

            case ACCESS_DAILY_TASK_DISTRIBUTION:
                fileContent = dsHelper.readSavedDataFromInternalStorage("DailyGoal.txt", context);
                String[] linesForDailyTask = fileContent.split(System.lineSeparator());
                // initialize every entry with 0
                for (int i = 0; i < LIST_OF_DAILY_TASKS_TO_INDEX.length; i++) {
                    Pair<Integer, Integer> tmpPair = new Pair<>(0, i);
                    outputList.add(tmpPair);
                }

                for (int i = 1; i < linesForDailyTask.length; i++) {

                    JSONObject jsonObject = new JSONObject(linesForDailyTask[i]);
                    LocalDateTime timestampAsLocalDateTime = TimeStamp.getLocalDateTime(jsonObject.get("Date"));

                    if(timestampAsLocalDateTime.compareTo(ChronoLocalDateTime.from(lowerBorder)) >= 0 && timestampAsLocalDateTime.compareTo(ChronoLocalDateTime.from(upperBorder)) <= 0) {
                        int index = getIndexFromTaskListByString((String) jsonObject.get("Daily_Task"));
                        Pair<Integer, Integer> newPair = new Pair<>(outputList.get(index).first + 1, index);
                        outputList.remove(index);
                        outputList.add(index, newPair);

                    }
                }
                break;

            default:
                break;
        }
        return outputList;
    }


    public float getWalkedDistanceFromRecording(String timestamp, Context context) throws JSONException {
        String[] splitted = timestamp.split(" ");
        String content = dsHelper.readSavedDataFromInternalStorage("sensorrecordings.txt", context);
        LocalDate tsDate = LocalDate.parse(splitted[0]);
        LocalTime tsTime = LocalTime.parse(splitted[1]);

        String[] lines = content.split(System.lineSeparator());
        for (int i = 1; i < lines.length; i++) {
            JSONObject jsonLine = new JSONObject(lines[i]);
            String jsonTimestampString = (String) jsonLine.get("timestamp");
            String[] jsonTimestampSplitted = jsonTimestampString.split(" ");
            LocalDate jsTimestampDate = LocalDate.parse(jsonTimestampSplitted[0]);
            LocalTime jsTimestampTime = LocalTime.parse(jsonTimestampSplitted[1]);
            if (tsDate.isEqual(jsTimestampDate) && tsTime.equals(jsTimestampTime)) {
                JSONArray jsonArray = (JSONArray) jsonLine.get("distance");
                if(jsonArray.length() == 0)
                    return -1;
                String distance = String.valueOf(jsonArray.get(0));
                return (int) (Float.parseFloat(distance));


            }
        }
        return -1;
   }

    public Triple<ArrayList<String>, ArrayList<Integer>, ArrayList<Integer>> getInterventionsValues(LocalDateTime lowerBorder, LocalDateTime upperBorder, Context context) throws JSONException {

        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer> completed = new ArrayList<>();
        ArrayList<Integer> incompleted = new ArrayList<>();

        String fileContent = dsHelper.readSavedDataFromInternalStorage("completedInterventions.txt", context);
        String[] linesInterventions = fileContent.split(System.lineSeparator());

        Instant instant = Instant.now();
        long diff = upperBorder.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(instant)) - lowerBorder.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(instant));

        int daysDifference = (int) (diff / (60*60*24)) + 2;
        System.out.println("difference:" + daysDifference);


        LocalDateTime tempdate = lowerBorder;
        for(int i = 0; i < daysDifference; i++) {
            dates.add(tempdate.toLocalDate().toString());
            tempdate = tempdate.plusDays(1);
            completed.add(0);
            incompleted.add(0);
        }


        for (int i = 1; i < linesInterventions.length; i++) {
            JSONObject jsonObject = new JSONObject(linesInterventions[i]);
            LocalDateTime timestampAsLocalDateTime = TimeStamp.getLocalDateTime(jsonObject.get("timestamp"));
            if(timestampAsLocalDateTime.compareTo(ChronoLocalDateTime.from(lowerBorder)) >= 0 && timestampAsLocalDateTime.compareTo(ChronoLocalDateTime.from(upperBorder)) <= 0) {
                long daydiff = timestampAsLocalDateTime.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(instant)) - lowerBorder.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(instant));
                int day = (int) (daydiff / (60*60*24));
                System.out.println("timestamp: " + timestampAsLocalDateTime + " daysdiff: " + day);


                if((boolean)jsonObject.get("completed")) {
                    int entry = completed.get(day);
                    completed.remove(day);
                    completed.add(day, entry + 1);
                } else {
                    int entry = incompleted.get(day);
                    incompleted.remove(day);
                    incompleted.add(day, entry + 1);
                }
            }
        }

        return new Triple<>(dates, completed, incompleted);
    }

    public int getStepCountFromRecording(String timestamp, Context context) throws JSONException {
        String[] splitTS = timestamp.split(" ");
        String content = dsHelper.readSavedDataFromInternalStorage("sensorrecordings.txt", context);

        LocalDate tsDate = LocalDate.parse(splitTS[0]);
        LocalTime tsTime = LocalTime.parse(splitTS[1]);

        String[] lines = content.split(System.lineSeparator());
        for (int i = 1; i < lines.length; i++) {
            JSONObject jsonLine = new JSONObject(lines[i]);
            String jsonTimestampString = (String) jsonLine.get("timestamp");
            String[] jsonTimestampSplitted = jsonTimestampString.split(" ");
            LocalDate jsTimestampDate = LocalDate.parse(jsonTimestampSplitted[0]);
            LocalTime jsTimestampTime = LocalTime.parse(jsonTimestampSplitted[1]);

            if (tsDate.isEqual(jsTimestampDate) && tsTime.equals(jsTimestampTime)) {

                JSONArray jsonArray = (JSONArray) jsonLine.get("stepcounter");

                String stepContent = String.valueOf(jsonArray.get(0));
                if(stepContent.length() == 0)
                    return -1;
                return (int) (Float.parseFloat(stepContent));
            }

        }
        return -1;
    }

    public Triple<ArrayList<Pair<Long, Float>>, ArrayList<Pair<Long, Float>>, ArrayList<Pair<Long, Float>>> getValuesFromRecording(String timestamp, Context context) throws JSONException {

        ArrayList<Pair<Long, Float>> x_list = new ArrayList<>();
        ArrayList<Pair<Long, Float>> y_list = new ArrayList<>();
        ArrayList<Pair<Long, Float>> z_list = new ArrayList<>();

        String[] timestampSplitted = timestamp.split(" ");

        LocalDate compTimestampDate = LocalDate.parse(timestampSplitted[0]);
        LocalTime compTimestampTime = LocalTime.parse(timestampSplitted[1]);

        String fileContent = dsHelper.readSavedDataFromInternalStorage("sensorrecordings.txt", context);

        String[] fileLines = fileContent.split(System.lineSeparator());
        for (int i = 1; i < fileLines.length; i++) {
            JSONObject jsonLine = new JSONObject(fileLines[i]);
            String jsonTimestampString = (String) jsonLine.get("timestamp");
            String[] jsonTimestampSplitted = jsonTimestampString.split(" ");
            LocalDate jsTimestampDate = LocalDate.parse(jsonTimestampSplitted[0]);
            LocalTime jsTimestampTime = LocalTime.parse(jsonTimestampSplitted[1]);

            if(compTimestampDate.isEqual(jsTimestampDate) && compTimestampTime.equals(jsTimestampTime)) {


                JSONArray jsonArrayAcceleration = (JSONArray) jsonLine.get("acceleration");
                for(int entryIndex = 0; entryIndex < jsonArrayAcceleration.length(); entryIndex++) {

                    JSONObject entry = (JSONObject) jsonArrayAcceleration.get(entryIndex);
                    Pair<Long, Float> x = new Pair<>(Long.parseLong((String)entry.get("timestamp")), Float.parseFloat((String)entry.get("x")));
                    x_list.add(x);
                    Pair<Long, Float> y = new Pair<>(Long.parseLong((String)entry.get("timestamp")), Float.parseFloat((String)entry.get("y")));
                    y_list.add(y);
                    Pair<Long, Float> z = new Pair<>(Long.parseLong((String)entry.get("timestamp")), Float.parseFloat((String)entry.get("z")));
                    z_list.add(z);
                }
                Triple<ArrayList<Pair<Long, Float>>, ArrayList<Pair<Long, Float>>, ArrayList<Pair<Long, Float>>> triple = new Triple<>(x_list, y_list, z_list);
                return triple;
            }

        }

        return new Triple<>(x_list, y_list, z_list);

    }

    private int getIndexFromTaskListByString(String taskString) {
        switch (taskString) {
            case "Walk 5.000 Steps":
                return 0;
            case "Walk 10.000 Steps":
                return 1;
            case "Drink 2L Water":
                return 2;
            case "Clean":
                return 3;
            case "Gor For A Walk":
                return 4;
            case "Meditate":
                return 5;
            case "Workout":
                return 6;
            default:
                return 7;
        }
    }

    public static int calculateHealthScoreFromQuestionaire(String questionaireAnswering) throws JSONException {
        // calculate the questionaire score here, for now using random values

        JSONObject questionaire = new JSONObject(questionaireAnswering);
        JSONArray jsonArray = (JSONArray) questionaire.get("Q1");

        int calmnessIndex = (int)(100 - jsonArray.getInt(0) + jsonArray.getInt(1))/2;
        int satisfactionIndex = (int)(200 - jsonArray.getInt(2) - jsonArray.getInt(3))/2;
        int energyIndex = (int)(100 - jsonArray.getInt(4) + jsonArray.getInt(5))/2;


        return (int) (calmnessIndex + satisfactionIndex + energyIndex)/3;

    }
}
