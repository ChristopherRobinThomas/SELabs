package com.example.se_lab_23b02;

import android.app.Activity;
import android.content.Context;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class UserHelper {

    private static UserHelper instance;
    private DataSavingHelper dsh;

    private User error;
    private UserHelper(){
        dsh = DataSavingHelper.getInstance();
        error = new User(-1,"NONE","NONE");
    }
    static public UserHelper getInstance(){
         if(instance == null)
             instance = new UserHelper();
         return instance;
    }

    public User getCurrentUser(Context context)
    {
        if(dsh.getAllSettingsFromSharedPref(context).containsKey("ActiveUser"))
        {
            return new User(dsh.getAllSettingsFromSharedPref(context).get("ActiveUser"));
        }
        return error;
    }

    public User getUserById(Context context, int id)
    {
        if(dsh.getAllSettingsFromSharedPref(context).containsKey("ActiveUser"))
        {
            if(id < 1 || id > 4)
                return error;

            String userIdString = "User" + intToStringOneToFour(id);
            return new User(dsh.getAllSettingsFromSharedPref(context).get(userIdString));
        }
        return error;
    }

    public int getCurrentUserID(Context context){
        if(dsh.getAllSettingsFromSharedPref(context).containsKey("ActiveUser"))
        {
            String current = getCurrentUser(context).toString();
            for(int i = 1; i <= 4; i++)
            {
                if(current.equals(getUserById(context,i).toString()))
                    return i;
            }
        }
        return -1;
    }

    public String intToStringOneToFour(int i) {
        switch (i){
            case 1: return "One";
            case 2: return "Two";
            case 3: return "Three";
            case 4: return "Four";
            default: return "";
        }
    }
    //Returned empty String on failure
    public String getSha256(String value){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            for(byte hashByte : hashBytes){
                stringBuilder.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        }catch (Exception e){
            return "";
        }
    }

}
