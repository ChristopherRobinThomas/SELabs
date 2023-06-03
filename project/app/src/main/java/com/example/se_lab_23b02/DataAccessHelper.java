package com.example.se_lab_23b02;

import android.content.Context;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

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
    public ArrayList<Pair<String, Integer>> collectDataFromTimeframe(LocalDate lowerBorder, LocalDate upperBorder, int accessMode, Context context) throws JSONException {
        ArrayList<Pair<String, Integer>> outputList = new ArrayList<>();
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
                    LocalDate timestampAsDate = LocalDate.parse(timestamp.split(" ")[0]);
                    if(timestampAsDate.compareTo(lowerBorder) >= 0 && timestampAsDate.compareTo(upperBorder) <= 0) {
                        Pair<String, Integer> newEntry = new Pair<>(timestampAsDate.toString(), this.calculateHealthScoreFromQuestionaire(lines[i]));
                        outputList.add(newEntry);
                    }
                }
                break;
            default:
                break;
        }
        return outputList;
    }


    private int calculateHealthScoreFromQuestionaire(String questionaireAnswering) {
        // calculate the questionaire score here, for now using random values
        Random random = new Random();
        return random.nextInt(101);

    }


}
