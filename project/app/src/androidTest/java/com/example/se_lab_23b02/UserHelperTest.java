package com.example.se_lab_23b02;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import android.content.Context;

import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class UserHelperTest {
    Context context;
    UserHelper userHelper;
    User error;
    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        userHelper = UserHelper.getInstance();
        error = new User(-1,"NONE","NONE");
    }

    @Test
    public void testIntToStringOneToFour() {
        assertEquals("One", userHelper.intToStringOneToFour(1));
        assertEquals("Two", userHelper.intToStringOneToFour(2));
        assertEquals("Three", userHelper.intToStringOneToFour(3));
        assertEquals("Four", userHelper.intToStringOneToFour(4));
        assertEquals("", userHelper.intToStringOneToFour(834));

    }

    @Test
    public void testGetUserById() {

        DataSavingHelper dataSavingHelper = DataSavingHelper.getInstance();
        for (int i = 1; i < 5; i++) {
            String userId = "User" + userHelper.intToStringOneToFour(i);
            String userString = dataSavingHelper.getSpecificSettingsFromSharedPref(userId, context);
            if (userString.equals("")) {
                assertEquals(error.toString(), userHelper.getUserById(context, i).toString());
            } else {
                User compare = new User(userString);
                assertEquals(compare.toString(), userHelper.getUserById(context, i).toString());
            }
        }
    }
    @Test
    public void testGetCurrentUser() {
        DataSavingHelper dataSavingHelper = DataSavingHelper.getInstance();
        String userString = dataSavingHelper.getSpecificSettingsFromSharedPref("ActiveUser", context);
        if(!userString.equals("")) {
            User compare = new User(userString);
            assertEquals(compare.toString(), userHelper.getCurrentUser(context).toString());
        } else {
            assertEquals(error.toString(), userHelper.getCurrentUser(context).toString());
        }
    }
}
