package com.example.se_lab_23b02;

import android.content.Context;
import android.util.Pair;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import kotlin.Triple;


@RunWith(AndroidJUnit4.class)
public class DataAccessHelperTest {
    DataAccessHelper dataAccessHelper;
    Context context;

    @Before
    public void initialize() {
        dataAccessHelper = DataAccessHelper.getInstance();
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        User u = new User("2,6b8cd66f-2794-11ee-bd9f-0adab99dbc12,test,5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8");
        DataSavingHelper.getInstance().saveSettingAsSharedPref("ActiveUser",u.toString(), context);


    }

    @Test
    public void testCollectDataFromTimeframe() throws JSONException {
        ArrayList<Pair<Integer, Integer>> out = new ArrayList<>();
        assertEquals(out, dataAccessHelper.collectDataFromTimeframe(LocalDateTime.now().minusDays(7), LocalDateTime.now(), DataAccessHelper.ACCESS_MENTAL_HEALTH_SCORE, context));

    }
    @Test
    public void testGetWalkedDistanceFromRecording() throws JSONException {
        assertEquals(-1.0, dataAccessHelper.getWalkedDistanceFromRecording("2023-07-10 03:12:01", context), 0.0);
    }

    @Test
    public void testGetInterventionsValueS() throws JSONException {
        String[] stringinit = {"12.06.2023", "13.06.2023", "14.06.2023", "15.06.2023", "16.06.2023", "17.06.2023", "18.06.2023", "19.06.2023", "20.06.2023"};
        Integer[] intInit = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        ArrayList<String> a1 = new ArrayList<>(Arrays.asList(stringinit));
        ArrayList<Integer> a2 = new ArrayList<>(Arrays.asList(intInit));
        ArrayList<Integer> a3 = new ArrayList<>(Arrays.asList(intInit));
        Triple<ArrayList<String>, ArrayList<Integer>, ArrayList<Integer>> triple = new Triple<>(a1, a2, a3);
        assertEquals(triple, dataAccessHelper.getInterventionsValues(TimeStamp.getLocalDateTime("2023-06-12 00:00:00"), TimeStamp.getLocalDateTime("2023-06-19 00:00:00"), context));
    }

    @Test
    public void testGetStepCountFromRecording() throws JSONException {
        assertEquals(-1.0, dataAccessHelper.getStepCountFromRecording("2023-07-10 03:12:01", context), 0.0);
    }

    @Test
    public void testGetValuesFromRecording() throws JSONException {
        ArrayList<Pair<Long, Float>> x_list = new ArrayList<>();
        ArrayList<Pair<Long, Float>> y_list = new ArrayList<>();
        ArrayList<Pair<Long, Float>> z_list = new ArrayList<>();
        Triple<ArrayList<Pair<Long, Float>>, ArrayList<Pair<Long, Float>>, ArrayList<Pair<Long, Float>>> toCompare = new Triple<>(x_list, y_list, z_list);
        assertEquals(toCompare, dataAccessHelper.getValuesFromRecording("2023-07-10 03:12:01", context));
    }

    @Test
    public void testGetIndexFromTasks() {
        final String[] list = {"Walk 5.000 Steps", "Walk 10.000 Steps", "Drink 2L Water", "Clean", "Go for a Walk", "Meditate", "Do a Workout", "other"};
        for(String entry : list) {
            checkGetIndexFromTask(entry);
        }
    }

    private void checkGetIndexFromTask(String taskString) {
        final String[] list = {"Walk 5.000 Steps", "Walk 10.000 Steps", "Drink 2L Water", "Clean", "Go for a Walk", "Meditate", "Do a Workout", "other"};
        assertEquals(Arrays.asList(list).indexOf(taskString), dataAccessHelper.getIndexFromTaskListByString(taskString));
    }

    @Test
    public void testCalculateHealthIndexFromQuestionnaire() throws JSONException {
        String questionnaireAnswers = "{\"Q1\":[0,32,10,62,0,100],\"Q2\":[0,0],\"Q3\":[0],\"Q4\":[\"Family\"],\"Q5\":[\"At home\"],\"Q6\":[8,35,66,29],\"Q7\":[8,1],\"Q8\":[1,0],\"Q9\":[\"\"],\"timestamp\":\"2023-07-17 09:35:10\",\"user_id\":\"49c7aee0-2474-11ee-bd9f-0adab99dbc12\"}";
        assertEquals(76, DataAccessHelper.calculateHealthScoreFromQuestionaire(questionnaireAnswers));
    }
}
