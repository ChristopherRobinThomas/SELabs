/*
 * Copyright (c) 2023.
 * Christopher Thomas, Miriam Fee, Linus Brandt, Maximilian Frese, Frederik Hüttemann
 * All rights. reserved.
 */

package com.example.se_lab_23b02;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.android.material.slider.LabelFormatter;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import kotlin.Triple;

public class OverviewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private  XYPlot plot;
    private PieChart pieChart;
    private TextView tvInfo;
    private DataSavingHelper dsHelper;
    private DataAccessHelper daHelper;

    private Button btShowHealthGraph;
    private Button btNumOfInterventions;
    private Button btNumOfActivities;
    private int currentTypeToPlot = 0;

    private Context context;
    private Resources resources;
    private ArrayList<Object> elementsForLanguageChange = new ArrayList<>();

    private LocalDateTime dateLB;
    private LocalDateTime dateUB;

    private Context contextForColor;
    private ConstraintLayout constraintLayout;

    private String mParam1;
    private String mParam2;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        constraintLayout = view.findViewById(R.id.overviewFragment);
        //button changes current Plot type and updates Plot (bar graph)
        btNumOfInterventions = (Button) view.findViewById(R.id.btIntervention);
        // show xy Plot by changing currentTypeToPlot to 0 and updating the graph
        btShowHealthGraph = (Button) view.findViewById(R.id.btHealthIndex);
        // show barchart by setting currentTypeToPlot to 2
        btNumOfActivities = (Button) view.findViewById(R.id.btDistributionOfDailytasks);


        // language changing
        elementsForLanguageChange.add(view.findViewById(R.id.btHealthIndex));
        elementsForLanguageChange.add(view.findViewById(R.id.btIntervention));
        elementsForLanguageChange.add(view.findViewById(R.id.btDistributionOfDailytasks));
        setColorTheme();
        refreshResources();
        setLanguage();



        // instantiate the plots/charts, make pieChart invisible while plot is visible
        plot = (XYPlot) view.findViewById(R.id.plt_graph);
        pieChart = (PieChart) view.findViewById(R.id.plt_pieChart);
        pieChart.setVisibility(View.INVISIBLE);

        dsHelper = DataSavingHelper.getInstance();

        Paint whiteColor = new Paint();
        whiteColor.setColor(Color.WHITE);
        Paint blackColor = new Paint();
        blackColor.setColor(Color.BLACK);

        Paint transparentColor = new Paint();
        transparentColor.setColor(Color.TRANSPARENT);

        Paint testColor = new Paint();
        testColor.setColor(getResources().getColor(R.color.below_graph));


        plot.getGraph().setBackgroundPaint(transparentColor);
        plot.getGraph().setGridBackgroundPaint(transparentColor);

        plot.getGraph().setDomainGridLinePaint(transparentColor);
        plot.getGraph().setDomainOriginLinePaint(transparentColor); //Y-Achse

        plot.getGraph().setRangeOriginLinePaint(testColor); //x-Achse
        plot.getGraph().setRangeGridLinePaint(transparentColor);


        
        tvInfo = (TextView) view.findViewById(R.id.tvNothingToSeeHere);



        btNumOfInterventions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTypeToPlot = 1;
                try {
                    updateGraph(currentTypeToPlot);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        btShowHealthGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentTypeToPlot = 0;
                    updateGraph(currentTypeToPlot);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // show barchart by setting currentTypeToPlot to 2
        Button btDailyTaskDistribution = (Button) view.findViewById(R.id.btDistributionOfDailytasks);
        btDailyTaskDistribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentTypeToPlot = 2;
                    updateGraph(currentTypeToPlot);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        // initializing the upper and lower Date boundary
        dateUB = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        dateLB = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).minusDays(7);


        // set Text of edittext to current selected time, when new time > dateUB then nothing is changed
        EditText lowerBoundTimePicker = (EditText) view.findViewById(R.id.etLowerBoundTimePicker);

        DateFormat dateFormat = new SimpleDateFormat(resources.getString(R.string.date_format));
        Date dateL = new Date(dateLB.getYear() - 1900, dateLB.getMonthValue() - 1, dateLB.getDayOfMonth());
        lowerBoundTimePicker.setText(dateFormat.format(dateL));
        lowerBoundTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = dateLB.getYear();
                int month = dateLB.getMonthValue() - 1;
                int day = dateLB.getDayOfMonth();

                DatePickerDialog dpDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog  .OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int month, int dayOfMonth) {
                                LocalDateTime tempLDT = LocalDateTime.now().withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth);
                                // check dateLB > dateUB
                                if (tempLDT.compareTo(dateUB) <= 0) {
                                    dateLB = dateLB.withYear(year);
                                    dateLB = dateLB.withMonth(month + 1);
                                    dateLB = dateLB.withDayOfMonth(dayOfMonth);
                                    Date tempDate = new Date(year - 1900, month, dayOfMonth);
                                    lowerBoundTimePicker.setText(dateFormat.format(tempDate));
                                } else {
                                    Toast.makeText(getActivity(), "Pick an earlier date", Toast.LENGTH_LONG).show();
                                }
                                try {
                                    // after the timeslot has changed the graph is updated
                                    updateGraph(currentTypeToPlot);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        },
                        year, month, day);
                dpDialog.show();
            }

        });


        // added an "editText" with Calender interaction for setting the timeframe, same as lowerBoundTimePicker
        EditText upperBoundTimePicker = (EditText) view.findViewById(R.id.etUpperBoundTimePicker);
        Date dateU = new Date(dateUB.getYear() - 1900, dateUB.getMonthValue() - 1, dateUB.getDayOfMonth());
        System.out.println(dateU);
        upperBoundTimePicker.setText(dateFormat.format(dateU));
        upperBoundTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = dateUB.getYear();
                int month = dateUB.getMonthValue() - 1;
                int day = dateUB.getDayOfMonth();

                DatePickerDialog dpDialog = new DatePickerDialog(

                        getContext(),
                        new DatePickerDialog  .OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int month, int dayOfMonth) {
                                LocalDateTime tempLDT = LocalDateTime.now().withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth);
                                if(tempLDT.compareTo(dateLB) >= 0) {
                                    dateUB = dateUB.withYear(year);
                                    dateUB = dateUB.withMonth(month + 1);
                                    dateUB = dateUB.withDayOfMonth(dayOfMonth);
                                    Date temp = new Date(year - 1900, month, dayOfMonth);

                                    upperBoundTimePicker.setText(dateFormat.format(temp));
                                } else {
                                    Toast.makeText(getActivity(), "Pick a later date", Toast.LENGTH_LONG).show();
                                }
                                try {
                                    updateGraph(currentTypeToPlot);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        },
                        year, month, day);
                dpDialog.show();
            }

        });

        try {
            updateGraph(currentTypeToPlot);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return view;

    }

    // updates the graph by reading the data from file (DAHelper) and then drawing it using draw/drawPieChart
    private void updateGraph(int typeToPlot) throws JSONException {
        plot.clear();
        pieChart.clear();
        daHelper = DataAccessHelper.getInstance();
        if (typeToPlot == 2) {
            plot.setVisibility(View.INVISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            ArrayList<Pair<Integer, Integer>> data = daHelper.collectDataFromTimeframe(dateLB, dateUB, DataAccessHelper.ACCESS_DAILY_TASK_DISTRIBUTION, getContext());
            ArrayList<Segment> segments = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                Segment temp = new Segment(DataAccessHelper.LIST_OF_DAILY_TASKS_TO_INDEX[data.get(i).second], data.get(i).first);
                segments.add(temp);
            }
            drawPieChart(segments);
            return;

        }
        if (typeToPlot == 0) {

            // get current values
            plot.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.INVISIBLE);
            ArrayList<Pair<Integer, Integer>> data = daHelper.collectDataFromTimeframe(dateLB, dateUB, DataAccessHelper.ACCESS_MENTAL_HEALTH_SCORE, getContext());
            int amountOfHours = (int) dateLB.until(dateUB, ChronoUnit.HOURS);
            Number[] yVals = new Number[data.size()];
            String[] labels = new String[amountOfHours];
            Number[] xVals = new Number[data.size()];


            // create Labels
            LocalDateTime tempDate = dateLB;
            labels[0] = tempDate.toLocalDate().toString();
            for (int i = 1; i < amountOfHours; i++) {
                tempDate = tempDate.plusHours(1);
                DateFormat df = new SimpleDateFormat(resources.getString(R.string.date_format));
                Date temp = new Date(tempDate.getYear() - 1900, tempDate.getMonthValue() - 1, tempDate.getDayOfMonth());
                labels[i] = df.format(temp);
            }

            for (int i = 0; i < data.size(); i++) {
                yVals[i] = data.get(i).first;
                xVals[i] = data.get(i).second;
            }

            drawMentalHealth(yVals, labels, xVals);
            return;
        }

        if(typeToPlot == 1) {
            plot.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.INVISIBLE);
            Triple<ArrayList<String>, ArrayList<Integer>, ArrayList<Integer>> data = daHelper.getInterventionsValues(dateLB, dateUB, getContext());
            String[] datesList = data.getFirst().toArray(new String[0]);
            Integer[] completed = data.getSecond().toArray(new Integer[0]);
            Integer[] incompleted = data.getThird().toArray(new Integer[0]);

            drawInterventions(datesList, completed, incompleted);

        }
    }


    // adds the segments to the piechart with the according formatter defined in the xml, so the color and font size fits
    private void drawPieChart(ArrayList<Segment> segments) {
        pieChart.clear();
        pieChart.setTitle(resources.getString(R.string.bt_overview_task_distribution));
        boolean content = false;

        if((int)segments.get(0).getValue() > 0) {
            SegmentFormatter sf0 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_0);
            pieChart.addSegment(segments.get(0), sf0);
            content = true;
        }
        if((int)segments.get(1).getValue() > 0) {
            SegmentFormatter sf1 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_1);
            pieChart.addSegment(segments.get(1), sf1);
            content = true;
        }
        if((int)segments.get(2).getValue() > 0) {
            SegmentFormatter sf2 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_2);
            pieChart.addSegment(segments.get(2), sf2);
            content = true;
        }
        if((int)segments.get(3).getValue() > 0) {
            SegmentFormatter sf3 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_3);
            pieChart.addSegment(segments.get(3), sf3);
            content = true;
        }
        if((int)segments.get(4).getValue() > 0) {
            SegmentFormatter sf4 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_4);
            pieChart.addSegment(segments.get(4), sf4);
            content = true;
        }
        if((int)segments.get(5).getValue() > 0) {
            SegmentFormatter sf5 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_5);
            pieChart.addSegment(segments.get(5), sf5);
            content = true;
        }
        if((int)segments.get(6).getValue() > 0) {
            SegmentFormatter sf6 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_6);
            pieChart.addSegment(segments.get(6), sf6);
            content = true;
        }
        if((int)segments.get(7).getValue() > 0) {
            SegmentFormatter sf7 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_7);
            pieChart.addSegment(segments.get(7), sf7);
            content = true;
        }
        if(content)
            tvInfo.setVisibility(TextView.INVISIBLE);
        else
            tvInfo.setVisibility(TextView.VISIBLE);
        pieChart.invalidate();
    }

    //draws the data of the parameters, both need to be the same length otherwise the graph doesn't plot properly

    private void drawMentalHealth(Number[] dataList, String[] labels, Number[] domains) {

        setPlotColor(plot);

        plot.getLayoutManager().remove(plot.getLegend());

        if (dataList.length == 0) {
            tvInfo.setVisibility(TextView.VISIBLE);
            plot.invalidate();
            return;
        } else
            tvInfo.setVisibility(TextView.INVISIBLE);

        //final Number[] domainLabels = domains;

        plot.setTitle(resources.getString(R.string.bt_overview_health_index));
        plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        plot.setDomainBoundaries(null, null, BoundaryMode.AUTO);
        final Number[] domainLabels = domains;

        XYSeries series = new SimpleXYSeries(Arrays.asList(domainLabels), Arrays.asList(dataList), "Series");
        LineAndPointFormatter seriesFormatter = new LineAndPointFormatter( getResources().getColor(R.color.below_graph), null, getResources().getColor(R.color.below_graph), null);
        seriesFormatter.setInterpolationParams(new CatmullRomInterpolator.Params(30, CatmullRomInterpolator.Type.Uniform));
        plot.addSeries(series, seriesFormatter);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(labels[i]);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        plot.invalidate();
    }
    private void drawInterventions(String[] dates, Number[] completed, Number[] incompleted) {
        plot.clear();
        setPlotColor(plot);
        plot.setRangeLowerBoundary(0, BoundaryMode.AUTO);

        plot.setTitle(resources.getString(R.string.bt_overview_interventions_graph));
        BarFormatter bf = new BarFormatter(getResources().getColor(R.color.bar_graph_red), Color.TRANSPARENT);
        bf.setMarginLeft(PixelUtils.dpToPix(1));
        bf.setMarginRight(PixelUtils.dpToPix(1));


        int max = 0;
        for (int i = 0; i < completed.length; i++) {
            if((int)completed[i] + (int)incompleted[i] > max)
                max = (int)completed[i] + (int)incompleted[i];
        }

        if(max == 0) {
            tvInfo.setVisibility(View.VISIBLE);
            plot.invalidate();
            return;
        } else
            tvInfo.setVisibility(View.INVISIBLE);

        plot.setRangeBoundaries(0, max, BoundaryMode.FIXED);

        Paint barGraphRed = new Paint();
        barGraphRed.setColor(getResources().getColor(R.color.bar_graph_red));

        BarFormatter bf1 = new BarFormatter(getResources().getColor(R.color.bar_graph_red), Color.TRANSPARENT);
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(incompleted), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series");
        plot.addSeries(series1, bf1);

        BarFormatter bf2 = new BarFormatter(getResources().getColor(R.color.bar_graph_green), Color.TRANSPARENT);
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(completed), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series");
        plot.addSeries(series2, bf2);


        BarRenderer renderer = plot.getRenderer(BarRenderer.class);
        renderer.setBarOrientation(BarRenderer.BarOrientation.STACKED);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, PixelUtils.dpToPix(10));

            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                    int i = Math.round(((Number) obj).floatValue());
                    try {
                        return toAppendTo.append(dates[i]);
                    } catch(ArrayIndexOutOfBoundsException e) {
                        Debug.logStack("ERROR", "index out of bounds while plotting bar chart", 0);
                        return toAppendTo.append("");
                    }

                }

                @Override
                public Object parseObject(String source, ParsePosition pos) {
                    return null;
                }
            });

            plot.invalidate();
            return;
        }

    private void refreshResources() {

        String language = DataSavingHelper.getInstance().getSelectedLanguage(getActivity().getApplicationContext());

        String language_short = "en";

        switch(language) {
            case "francais":
                language_short = "fr";
                break;
            case "deutsch":
                language_short = "de";
                break;
        }
        context = LocaleHelper.setLocale(getActivity().getApplicationContext(), language_short);
        resources = context.getResources();
    }

    private void setLanguage() {
        Button bt = (Button) elementsForLanguageChange.get(0);
        bt.setText(resources.getString(R.string.bt_overview_health_index));
        bt = (Button) elementsForLanguageChange.get(1);
        bt.setText(resources.getString(R.string.bt_overview_interventions_graph));
        bt = (Button) elementsForLanguageChange.get(2);
        bt.setText(resources.getString(R.string.bt_overview_task_distribution));
    }


    private void setColorTheme()
    {
        int ButtonTransparency = 50;

        contextForColor = getActivity().getApplicationContext();
        String mode = dsHelper.getInstance().getSpecificSettingsFromSharedPref("dmValue",contextForColor);
        String color = dsHelper.getInstance().getSpecificSettingsFromSharedPref("color",contextForColor);

        switch(mode)
        {
            case "dark-mode":
                ButtonTransparency = 70;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                switch (color)
                {
                    case "blue":
                        constraintLayout.setBackgroundResource(R.drawable.blue_dark_mode_background);
                        break;
                    case "red":
                        constraintLayout.setBackgroundResource(R.drawable.red_dark_mode_background);
                        break;
                    case "green":
                        constraintLayout.setBackgroundResource(R.drawable.green_dark_mode_background);
                        break;
                }
                break;
            case "white-mode":
                ButtonTransparency = 150;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                switch (color)
                {
                    case "blue":
                        constraintLayout.setBackgroundResource(R.drawable.blue_light_mode_background);
                        break;
                    case "red":
                        constraintLayout.setBackgroundResource(R.drawable.red_light_mode_background);
                        break;
                    case "green":
                        constraintLayout.setBackgroundResource(R.drawable.green_light_mode_background);
                        break;
                }
                break;

        }
        btNumOfActivities.getBackground().setAlpha(ButtonTransparency);
        btNumOfInterventions.getBackground().setAlpha(ButtonTransparency);
        btShowHealthGraph.getBackground().setAlpha(ButtonTransparency);
    }


    private void setPlotColor(XYPlot plot)
    {
        //Get Color Scheme
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setTextSize(50);
        blackPaint.setTextAlign(Paint.Align.CENTER);

        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(50);
        whitePaint.setTextAlign(Paint.Align.CENTER);
        contextForColor = getActivity().getApplicationContext();
        String mode = dsHelper.getInstance().getSpecificSettingsFromSharedPref("dmValue",contextForColor);
        plot.clear();
        if(mode.equals("white-mode"))
        {
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setColor(Color.BLACK);
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().setColor(Color.BLACK);
            plot.getTitle().setLabelPaint(blackPaint);
        }
        else
        {
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setColor(Color.WHITE);
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().setColor(Color.WHITE);
            plot.getTitle().setLabelPaint(whitePaint);

        }

        plot.getLayoutManager().remove(plot.getLegend());
    }
}