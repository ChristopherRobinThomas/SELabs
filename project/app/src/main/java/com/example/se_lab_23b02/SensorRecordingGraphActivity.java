package com.example.se_lab_23b02;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Pair;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import kotlin.Triple;

public class SensorRecordingGraphActivity extends Activity {
    private XYPlot xyPlot;
    private CheckBox checkX;
    private CheckBox checkY;
    private CheckBox checkZ;

    private DataAccessHelper daHelper;
    private boolean xChecked = true;
    private boolean yChecked = true;
    private boolean zChecked = true;

    private Triple<ArrayList<Pair<Long, Float>>, ArrayList<Pair<Long, Float>>, ArrayList<Pair<Long, Float>>> values;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_recording);

        daHelper = DataAccessHelper.getInstance();
        xyPlot = findViewById(R.id.activity_graph);
        Paint whiteColor = new Paint();
        whiteColor.setColor(Color.WHITE);
        xyPlot.getGraph().setBackgroundPaint(whiteColor);
        xyPlot.getGraph().setGridBackgroundPaint(whiteColor);

        checkX = findViewById(R.id.cbX);
        checkY = findViewById(R.id.cbY);
        checkZ = findViewById(R.id.cbZ);


        checkX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                xChecked = isChecked;
                plotting();
            }
        });
        checkY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                yChecked = isChecked;
                plotting();
            }
        });

        checkZ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                zChecked = isChecked;
                plotting();
            }
        });



        Bundle b = getIntent().getExtras();
        String[] dateTime = b.getStringArray("time");
        String day = b.getString("date");
        String timestamp = day + " " + dateTime[0] + ":" + dateTime[1] + ":" + dateTime[2];

        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setText("recording: " + timestamp);

        getValues(timestamp);
        plotting();
    }

    private void getValues(String timestamp) {
        timestamp = "2023-07-05 13:09:14";
        try {
            values = daHelper.getValuesFromRecording(timestamp, getApplicationContext());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void plotting() {
        xyPlot.clear();
        xyPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setColor(Color.BLACK);
        xyPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().setColor(Color.BLACK);
        xyPlot.getLayoutManager().remove(xyPlot.getLegend());

        ArrayList<Pair<Long, Float>> x_list = values.getFirst();
        ArrayList<Pair<Long, Float>> y_list = values.getSecond();
        ArrayList<Pair<Long, Float>> z_list = values.getThird();

        Long[] domains = new Long[x_list.size()];
        Float[] x = new Float[x_list.size()];
        Float[] y = new Float[y_list.size()];
        Float[] z = new Float[z_list.size()];


        for(int i = 0; i < x_list.size(); i++) {
            domains[i] = x_list.get(i).first;
            x[i] = x_list.get(i).second;
            y[i] = y_list.get(i).second;
            z[i] = z_list.get(i).second;
        }

        XYSeries x_series = new SimpleXYSeries(Arrays.asList(domains), Arrays.asList(x), "x-Values");
        XYSeries y_series = new SimpleXYSeries(Arrays.asList(domains), Arrays.asList(y), "y-Values");
        XYSeries z_series = new SimpleXYSeries(Arrays.asList(domains), Arrays.asList(z), "z-Values");

        LineAndPointFormatter xFormatter = new LineAndPointFormatter(Color.RED, null, null, null);
        LineAndPointFormatter yFormatter = new LineAndPointFormatter(Color.GREEN, null, null, null);
        LineAndPointFormatter zFormatter = new LineAndPointFormatter(Color.BLUE, null, null, null);
        if(xChecked)
            xyPlot.addSeries(x_series, xFormatter);
        if(yChecked)
            xyPlot.addSeries(y_series, yFormatter);
        if(zChecked)
            xyPlot.addSeries(z_series, zFormatter);




        xyPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                char[] objectAsString = obj.toString().toCharArray();
                char[] charList = new char[10];
                int counter = 0;
                for(int i = 0; i < 11; i++) {
                    if(i != 1) {
                        charList[counter] = objectAsString[i];
                        counter++;
                    }
                }
                int timestamp = Integer.parseInt(String.valueOf(charList));

                return toAppendTo.append(TimeStamp.getLocalTimeAsStringFromSeconds(timestamp));
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });



        PanZoom.attach(xyPlot);
        xyPlot.invalidate();

    }



}
