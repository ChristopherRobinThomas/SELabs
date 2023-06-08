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
import java.util.Random;

import kotlin.Triple;

public class DataAccessHelper {

    // to divide different
    public static final int ACCESS_MENTAL_HEALTH_SCORE = 1;
    public static final int ACCESS_COUNT_OF_INTERVENTIONS = 2;
    public static final int ACCESS_COUNT_OF_ACTIVITIES = 3;


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
                //System.out.println(fileContent);
                String[] lines = fileContent.split(System.lineSeparator());

                // reads data from file and then checks for each element if it is within the given timeframe
                for (int i = 1; i < lines.length; i++) {
                    JSONObject jsObject = new JSONObject(lines[i]);
                    String timestamp = (String) jsObject.get("timestamp");
                    String[] times = timestamp.split(" ");
                    LocalDate tmpLocalDate = LocalDate.parse(times[0]);
                    LocalTime tmpLocalTime = LocalTime.parse(times[1]);
                    LocalDateTime ldtTimestampAsDate = LocalDateTime.of(tmpLocalDate, tmpLocalTime);
                    if(ldtTimestampAsDate.compareTo(ChronoLocalDateTime.from(lowerBorder)) >= 0 && ldtTimestampAsDate.compareTo(ChronoLocalDateTime.from(upperBorder)) <= 0) {
                        System.out.println("line read: timestamp: " + ldtTimestampAsDate.toString() + ", lb: " +
                                lowerBorder.toString() + ", ub: " + upperBorder.toString());
                        int hours = (int) ChronoUnit.HOURS.between(lowerBorder, ldtTimestampAsDate);
                        Pair<Integer, Integer> newEntry = new Pair<>(this.calculateHealthScoreFromQuestionaire(lines[i]), hours);
                        outputList.add(newEntry);
                    }
                }
                break;

            case ACCESS_COUNT_OF_ACTIVITIES:
                fileContent = dsHelper.readSavedDataFromInternalStorage("activities.txt", context);
                // count here
                // append to outputList
                break;

            case ACCESS_COUNT_OF_INTERVENTIONS:
                fileContent = dsHelper.readSavedDataFromInternalStorage("", context);
                // count
                //append
                break;
            default:
                break;
        }
        return outputList;
    }



    public int calculateHealthScoreFromQuestionaire(String questionaireAnswering) throws JSONException {
        // calculate the questionaire score here, for now using random values

        JSONObject questionaire = new JSONObject(questionaireAnswering);
        JSONArray jsonArray = (JSONArray) questionaire.get("Q1");

        int calmnessIndex = (int)(100 - jsonArray.getInt(0) + jsonArray.getInt(1))/2;
        int satisfactionIndex = (int)(200 - jsonArray.getInt(2) + jsonArray.getInt(3))/2;
        int energyIndex = (int)(100 - jsonArray.getInt(4) + jsonArray.getInt(5))/2;


        return (int) (calmnessIndex + satisfactionIndex + energyIndex)/3;

    }
}
