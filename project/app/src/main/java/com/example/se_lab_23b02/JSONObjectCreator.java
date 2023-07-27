package com.example.se_lab_23b02;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;

public class JSONObjectCreator {

    public static JSONObject addInformationToJSONObject(JSONObject toSave, Context context) throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        toSave.put("user_id", dsHelper.getSpecificSettingsFromSharedPref("user_id", context));
        return toSave;
    }
    public static JSONObject createNewUserObject(String userName, String password, String passwordConfirm) throws JSONException {
        JSONObject toSave = new JSONObject();
        toSave.put("username", userName);
        toSave.put("password", password);
        toSave.put("passwordConfirm", passwordConfirm);

        return toSave;
    }

    public static JSONObject createLoginUserObject(String userName, String password) throws JSONException {
        JSONObject toSave = new JSONObject();
        toSave.put("username", userName);
        toSave.put("password", password);

        return toSave;
    }

    public static JSONObject createQuestionnaireObject(int[] questionOneValues, int[] questionTwoValues, int[] questionThreeValues, String[] questionFourValue, String[] questionFiveValue, int[] questionSixValues, int[] questionSevenValues, int[] questionEightValues) throws JSONException {
        JSONArray[] valueArrays = {
                new JSONArray(questionOneValues),
                new JSONArray(questionTwoValues),
                new JSONArray(questionThreeValues),
                new JSONArray(questionFourValue),
                new JSONArray(questionFiveValue),
                new JSONArray(questionSixValues),
                new JSONArray(questionSevenValues),
                new JSONArray(questionEightValues)
        };
        JSONObject toSave = new JSONObject();
        for (int i = 1; i <= valueArrays.length; i++) {
            toSave.put("Q"+i, valueArrays[i-1]);
        }
        String timestamp = TimeStamp.getCurrentTimeStamp();
        toSave.put("timestamp: ", timestamp);
        return toSave;
    }

    public static JSONObject createDigitSpanTaskObject(int originalInitSpanLength, int initSpanLength, int maxReachedNumbers, int numberOfRounds, Context context) throws JSONException {
        JSONObject toSave = new JSONObject();

        toSave.put("InitSpanLengtToday",originalInitSpanLength);
        toSave.put("InitSpanLengthTomorrow",initSpanLength);
        toSave.put("MaxKnownNumbers",maxReachedNumbers);
        toSave.put("numberOfSuccessfulRounds", numberOfRounds);
        String timestamp = TimeStamp.getCurrentTimeStamp();
        toSave.put("timestamp", timestamp);
        toSave.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());
        return toSave;
    }

    public static JSONObject createAllQuestionnaireObject(Context context) throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String localData = dsHelper.readSavedDataFromInternalStorage("questionnaire.txt", context);
        localData = localData.trim();
        JSONArray jdata = new JSONArray(localData.split("\n"));
        JSONObject toSave = new JSONObject();
        toSave.put("Questionnaires", jdata);
        toSave.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());
        return toSave;
    }

    public static JSONObject createAllDigitSpanTaskObject(Context context) throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String localData = dsHelper.readSavedDataFromInternalStorage("DigitSpanTask.txt", context);
        localData = localData.trim();
        JSONArray jdata = new JSONArray(localData.split("\n"));
        JSONObject toSave = new JSONObject();
        toSave.put("DigitSpanTasks", jdata);
        toSave.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());
        return toSave;
    }

    public static JSONObject createAllDailyGoalObject(Context context) throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String localData = dsHelper.readSavedDataFromInternalStorage("DailyGoal.txt", context);
        localData = localData.trim();
        JSONArray jdata = new JSONArray(localData.split("\n"));
        JSONObject toSave = new JSONObject();
        toSave.put("DigitSpanTasks", jdata);
        toSave.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());
        return toSave;

    }
    public static JSONObject createDeleteObject(Context context) throws JSONException {
        JSONObject toSave = new JSONObject();
        toSave.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());

        return toSave;
    }

    public static JSONObject createAllSensorreadingsObject(Context context) throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String localData = dsHelper.readSavedDataFromInternalStorage("sensorrecordings.txt", context);
        localData = localData.trim();
        JSONArray jdata = new JSONArray(localData.split("\n"));
        JSONObject toSave = new JSONObject();
        toSave.put("Sensorrecordings", jdata);
        toSave.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());
        return toSave;
    }

    public static JSONObject createAllCompletedInterventions(Context context) throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String localData = dsHelper.readSavedDataFromInternalStorage("completedInterventions.txt", context);
        localData = localData.trim();
        JSONArray jdata = new JSONArray(localData.split("\n"));
        JSONObject toSave = new JSONObject();
        toSave.put("completedInterventions", jdata);
        toSave.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());
        return toSave;
    }
}
