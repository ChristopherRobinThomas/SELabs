package com.example.se_lab_23b02;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import android.content.Context;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class JSONObjectCreatorTest {

    Context context;
    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        User u = new User("2,6b8cd66f-2794-11ee-bd9f-0adab99dbc12,test,5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8");
        DataSavingHelper.getInstance().saveSettingAsSharedPref("ActiveUser",u.toString(), context);

    }

    @Test
    public void testAddInformationToJSONObject() throws JSONException {
        JSONObject to_test = new JSONObject();
        to_test.put("test1", 0);
        JSONObject to_compare = JSONObjectCreator.addInformationToJSONObject(to_test, context);
        DataSavingHelper dataSavingHelper = DataSavingHelper.getInstance();

        to_test.put("user_id", dataSavingHelper.getSpecificSettingsFromSharedPref("user_id", context));
        assertEquals(to_test, to_compare);

    }

    @Test
    public void testCreateNewUserObject() throws JSONException {
        String username = "lorum";
        String password = "ipsum";

        JSONObject to_compare = new JSONObject();
        to_compare.put("username", username);
        to_compare.put("password", password);
        to_compare.put("passwordConfirm", password);

        assertEquals(to_compare.toString(), JSONObjectCreator.createNewUserObject(username, password, password).toString());
    }

    @Test
    public void testCreateLoginUserObject() throws JSONException {
        String username = "lorum";
        String password = "ipsum";

        JSONObject to_compare = new JSONObject();
        to_compare.put("username", username);
        to_compare.put("password", password);

        JSONObject result = JSONObjectCreator.createLoginUserObject(username, password);

        assertEquals(result.toString(), to_compare.toString());
    }

    @Test
    public void testCreateQuestionnaireObject() throws JSONException {
        int[] q1 = {1, 2, 3, 4, 5, 6};
        int[] q2 = {1, 5};
        int[] q3 = {0};
        String[] q4 = {"test"};
        String[] q5 = {"At Home"};
        int[] q6 = {928, 23, 1, -4};
        int[] q7 = {-123, 3};
        int[] q8 = {913, 13};
        JSONObject result = JSONObjectCreator.createQuestionnaireObject(q1, q2, q3, q4, q5, q6, q7, q8);
        String timestamp = TimeStamp.getCurrentTimeStamp();

        JSONArray a1 = new JSONArray(q1);
        JSONArray a2 = new JSONArray(q2);
        JSONArray a3 = new JSONArray(q3);
        JSONArray a4 = new JSONArray(q4);
        JSONArray a5 = new JSONArray(q5);
        JSONArray a6 = new JSONArray(q6);
        JSONArray a7 = new JSONArray(q7);
        JSONArray a8 = new JSONArray(q8);


        JSONObject compare = new JSONObject();
        compare.put("Q1", a1);
        compare.put("Q2", a2);
        compare.put("Q3", a3);
        compare.put("Q4", a4);
        compare.put("Q5", a5);
        compare.put("Q6", a6);
        compare.put("Q7", a7);
        compare.put("Q8", a8);
        compare.put("timestamp", timestamp);

        assertEquals(compare.toString(), result.toString());
    }

    @Test
    public void testCreateDigitSpanTaskObject() throws JSONException{
        JSONObject compare = new JSONObject();

        int originalInitSpanLength = 5;
        int initSpanLength = 3;
        int maxReachedNumbers = 3;
        int numberOfRounds = 4;

        compare.put("InitSpanLengtToday", originalInitSpanLength);
        compare.put("InitSpanLengthTomorrow", initSpanLength);
        compare.put("MaxKnownNumbers", maxReachedNumbers);
        compare.put("numberOfSuccessfulRounds", numberOfRounds);
        String timestamp = TimeStamp.getCurrentTimeStamp();
        compare.put("timestamp", timestamp);
        compare.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());

        assertEquals(compare.toString(), JSONObjectCreator.createDigitSpanTaskObject(originalInitSpanLength, initSpanLength, maxReachedNumbers, numberOfRounds, context).toString());

    }
    @Test
    public void testCreateAllQuestionnaireObject() throws JSONException{
        DataSavingHelper dataSavingHelper = DataSavingHelper.getInstance();
        String localData = dataSavingHelper.readSavedDataFromInternalStorage("questionnaire.txt", context);
        localData = localData.trim();
        JSONArray jdata = new JSONArray(localData.split("\n"));
        JSONObject compare = new JSONObject();
        compare.put("Questionnaires", jdata);
        compare.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());
        assertEquals(compare.toString(), JSONObjectCreator.createAllQuestionnaireObject(context).toString());
    }

    @Test
    public void testCreateAllDigitSpanTaskObject() throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String localData = dsHelper.readSavedDataFromInternalStorage("DigitSpanTask.txt", context);
        localData = localData.trim();
        JSONArray jdata = new JSONArray(localData.split("\n"));
        JSONObject compare = new JSONObject();
        compare.put("DigitSpanTasks", jdata);
        compare.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());
        assertEquals(compare.toString(), JSONObjectCreator.createAllDigitSpanTaskObject(context).toString());
    }

    @Test
    public void testCreateAllDailyGoalObject() throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String localData = dsHelper.readSavedDataFromInternalStorage("DailyGoal.txt", context);
        localData = localData.trim();
        JSONArray jdata = new JSONArray(localData.split("\n"));
        JSONObject compare = new JSONObject();
        compare.put("DigitSpanTasks", jdata);
        compare.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());
        assertEquals(compare.toString(), JSONObjectCreator.createAllDailyGoalObject(context).toString());
    }

    @Test
    public void testCreateDeleteObject() throws JSONException {
        JSONObject compare = new JSONObject();
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        compare.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());

        assertEquals(compare.toString(), JSONObjectCreator.createDeleteObject(context).toString());

    }

    @Test
    public void testCreateAllSensorreadingsObject() throws JSONException {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        String localData = dsHelper.readSavedDataFromInternalStorage("sensorrecordings.txt", context);
        localData = localData.trim();
        JSONArray jdata = new JSONArray(localData.split("\n"));
        JSONObject compare = new JSONObject();
        compare.put("Sensorrecordings", jdata);

        compare.put("user_id", UserHelper.getInstance().getCurrentUser(context).getGlobalId());

        System.out.println(compare);
        assertEquals(compare.toString(), JSONObjectCreator.createAllSensorreadingsObject(context).toString());

    }



}
