package com.example.se_lab_23b02;

import android.content.Context;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
                        Pair<Integer, Integer> newEntry = new Pair<>(this.calculateHealthScoreFromQuestionaire(linesForMentalHealth[i]), hours);
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

            case ACCESS_COUNT_OF_INTERVENTIONS:
                fileContent = dsHelper.readSavedDataFromInternalStorage("", context);
                String[] linesInterventions = fileContent.split(System.lineSeparator());
                for (int i = 1; i < linesInterventions.length; i++) {
                    JSONObject jsonObject = new JSONObject(linesInterventions[i]);
                    LocalDateTime timestampAsLocalDateTime = TimeStamp.getLocalDateTime(jsonObject.get("timestamp"));
                    if(timestampAsLocalDateTime.compareTo(ChronoLocalDateTime.from(lowerBorder)) >= 0 && timestampAsLocalDateTime.compareTo(ChronoLocalDateTime.from(upperBorder)) <= 0) {
                        //append count oder so
                    }
                }
                // count
                //append
                break;
            default:
                break;
        }
        return outputList;
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
