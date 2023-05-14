package com.example.se_lab_23b02;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TimePickerActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_picker_for_notifications);

        Bundle b = getIntent().getExtras();
        TextView tv = (TextView) findViewById(R.id.tvSlot);

        int lb = (int) b.get("lowerBound");
        int ub = (int) b.get("upperBound");
        String text = lb + ":00 - " + ub + ":00";
        tv.setText(text);

        Button btDestroy = (Button) findViewById(R.id.btDestroy);
        btDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
