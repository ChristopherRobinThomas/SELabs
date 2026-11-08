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
        User u = new User("2,6b8cd66f-2794-11ee-bd9f-0adab99dbc12,test,5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8");
        DataSavingHelper.getInstance().saveSettingAsSharedPref("ActiveUser",u.toString(), context);
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
