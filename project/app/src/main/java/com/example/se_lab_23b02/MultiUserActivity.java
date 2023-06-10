package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class MultiUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_user);
    }

    private ArrayList<User> getLocalUser()
    {
        return new ArrayList<User>();
    }
}