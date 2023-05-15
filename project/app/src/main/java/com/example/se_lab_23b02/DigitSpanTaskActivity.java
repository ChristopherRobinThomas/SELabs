package com.example.se_lab_23b02;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class DigitSpanTaskActivity extends AppCompatActivity {

    //Variables
    private int count_up;
    int spanLength = 5;

    //Objects
    Random rand = new Random();
    Button startingButton;
    TextView numberField;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digit_span_task);
        startingButton = findViewById(R.id.startButton);
        numberField = findViewById(R.id.numberView);
    }

    Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);

    public Runnable changeNumber()
    {
        String[] spanNumbers = generateRandomNumbers();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fadeOut.setDuration(1500);
                fadeOut.setFillAfter(true);
                numberField.startAnimation(fadeOut);
                numberField.setText(spanNumbers[count_up]);
                count_up++;
                if(count_up == spanLength)
                {
                    return;
                }
                handler.postDelayed(this,1500);

            }
        };
        return runnable;
    }

    public void startButtonClicked(View v)
    {
        startingButton.setEnabled(false);
        count_up = 0;
        Runnable runnable = changeNumber();
        handler.postDelayed(runnable,1500);

    }

    private String[] generateRandomNumbers()
    {
        String[] spanNumbers = new String[spanLength];
        for(int i = 0; i < spanLength-1; i++ ){
            spanNumbers[i] = Integer.toString(rand.nextInt(10));
        }
        spanNumbers[spanLength-1] = " ";
        return spanNumbers;
    }
}