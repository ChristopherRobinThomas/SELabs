package com.example.se_lab_23b02;

import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testGetLocalId() {
        long localId = 123456;
        User user = new User(localId, "lorum","ipsum");
        assertEquals(localId, user.getLocalId());
    }

    @Test
    public void testGetGlobalId() {
        String globalID = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        User user = new User("1," + globalID + ",lorum,ipsum");
        assertEquals(globalID, user.getGlobalId());
    }

    @Test
    public void testGetName() {
        String name = "lorum";
        User user = new User(123456, name, "ipsum");
        assertEquals(name, user.getName());
    }

    @Test
    public void testSetName() {
        User user = new User(123456, "lorum","ipsum");
        String newName = "hans Wurst";
        user.setName(newName);
        assertEquals(newName, user.getName());
    }

    @Test
    public void testSetGlobalId() {
        User user = new User(123456, "lorum","ipsum");
        String globalID = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        user.setGlobalId(globalID);
        assertEquals(globalID, user.getGlobalId());
    }
    @Test
    public void testGetPassword() {
        String password = "ipsum";
        User user = new User(123456, "lorum",password);
        assertEquals(password, user.getPassword());
    }

    @Test
    public void testToString() {
        String globalID = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        long localID = 478;
        String name = "lorum";
        String password = "ipsum";
        String output = localID + "," + globalID + "," + name + "," + password;
        User user = new User(output);
        assertEquals(output, user.toString());

    }

    @Test
    public void testCheckPassword() {
        String password = "ipsum";
        User user = new User(123456, "lorum", password);
        assertTrue(user.checkPassword(password));


    }

}
