package com.example.se_lab_23b02;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
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
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private  XYPlot plot;
    private PieChart pieChart;
    private TextView tvInfo;
    private DataSavingHelper dsHelper;
    private DataAccessHelper daHelper;
    private int currentTypeToPlot = 0;

    private LocalDateTime dateLB;
    private LocalDateTime dateUB;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        plot = (XYPlot) view.findViewById(R.id.plt_graph);
        pieChart = (PieChart) view.findViewById(R.id.plt_pieChart);
        pieChart.setVisibility(View.INVISIBLE);

        dsHelper = DataSavingHelper.getInstance();

        Paint whiteColor = new Paint();
        whiteColor.setColor(Color.WHITE);
        Paint blackColor = new Paint();
        blackColor.setColor(Color.BLACK);

        plot.getGraph().setBackgroundPaint(whiteColor);
        plot.getGraph().setGridBackgroundPaint(whiteColor);

        tvInfo = (TextView) view.findViewById(R.id.tvNothingToSeeHere);


        Button btNumOfInterventions = (Button) view.findViewById(R.id.btIntervention);
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


        Button btShowHealthGraph = (Button) view.findViewById(R.id.btHealthIndex);
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

        Button btNumOfActivities = (Button) view.findViewById(R.id.btNumOfActivities);
        btNumOfActivities.setOnClickListener(new View.OnClickListener() {
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

        dateLB = LocalDateTime.now().minusDays(7);


        EditText lowerBoundTimePicker = (EditText) view.findViewById(R.id.etLowerBoundTimePicker);
        lowerBoundTimePicker.setText(dateLB.getDayOfMonth() + "." + (dateLB.getMonthValue()) + "." + dateLB.getYear());
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
                                if (tempLDT.compareTo(dateUB) <= 0) {
                                    dateLB = dateLB.withYear(year);
                                    dateLB = dateLB.withMonth(month + 1);
                                    dateLB = dateLB.withDayOfMonth(dayOfMonth);
                                    lowerBoundTimePicker.setText(dayOfMonth + "." + (month + 1) + "." + year);
                                } else {
                                    Toast.makeText(getActivity(), "Pick an earlier date", Toast.LENGTH_LONG).show();
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


        // added an "editText" with Calender interaction for setting the timeframe
        EditText upperBoundTimePicker = (EditText) view.findViewById(R.id.etUpperBoundTimePicker);
        upperBoundTimePicker.setText(dateUB.getDayOfMonth() + "." + (dateUB.getMonthValue()) + "." + dateUB.getYear());
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
                                dateUB = dateUB.withYear(year);
                                dateUB = dateUB.withMonth(month + 1);
                                dateUB = dateUB.withDayOfMonth(dayOfMonth);
                                upperBoundTimePicker.setText(dayOfMonth + "." + (month + 1) + "." + year);
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

    // updates the graph by reading the data from file (DAHelper) and then drawing it using draw()
    private void updateGraph(int typeToPlot) throws JSONException {

        daHelper = DataAccessHelper.getInstance();
        if(typeToPlot == 2) {
            plot.setVisibility(View.INVISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            ArrayList<Pair<Integer, Integer>> data = daHelper.collectDataFromTimeframe(dateLB, dateUB, DataAccessHelper.ACCESS_DAILY_TASK_DISTRIBUTION, getContext());
            ArrayList<Segment> segments = new ArrayList<Segment>();
            for (int i = 0; i < data.size(); i++) {
                Segment temp = new Segment(DataAccessHelper.LIST_OF_DAILY_TASKS_TO_INDEX[data.get(i).second], data.get(i).first);
                segments.add(temp);
            }
            drawPieChart(segments);
            return;

        }
        // get current values
        plot.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.INVISIBLE);
        ArrayList<Pair<Integer, Integer>> data = daHelper.collectDataFromTimeframe(dateLB, dateUB, DataAccessHelper.ACCESS_MENTAL_HEALTH_SCORE, getContext());
        int amountOfHours = (int)dateLB.until(dateUB, ChronoUnit.HOURS);
        Number[] yVals = new Number[data.size()];
        String[] labels = new String[amountOfHours];
        Number[] xVals = new Number[data.size()];


        // create Labels
        LocalDateTime tempDate = dateLB;
        labels[0] = tempDate.toLocalDate().toString();
        for (int i = 1; i < amountOfHours; i++) {
            tempDate = tempDate.plusHours(1);
            labels[i] = tempDate.toLocalDate().toString();
        }

        for(int i = 0; i < data.size(); i++) {
            yVals[i] = data.get(i).first;
            xVals[i] = data.get(i).second;
        }

        draw(typeToPlot, yVals, labels, xVals);

    }

    private void drawPieChart(ArrayList<Segment> segments) {
        pieChart.clear();
        pieChart.setTitle("Distribution of the Daily Tasks");

        if((int)segments.get(0).getValue() > 0) {
            SegmentFormatter sf0 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_0);
            pieChart.addSegment(segments.get(0), sf0);
        }
        if((int)segments.get(1).getValue() > 0) {
            SegmentFormatter sf1 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_1);
            pieChart.addSegment(segments.get(1), sf1);
        }
        if((int)segments.get(2).getValue() > 0) {
            SegmentFormatter sf2 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_2);
            pieChart.addSegment(segments.get(2), sf2);
        }
        if((int)segments.get(3).getValue() > 0) {
            SegmentFormatter sf3 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_3);
            pieChart.addSegment(segments.get(3), sf3);
        }
        if((int)segments.get(4).getValue() > 0) {
            SegmentFormatter sf4 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_4);
            pieChart.addSegment(segments.get(4), sf4);
        }
        if((int)segments.get(5).getValue() > 0) {
            SegmentFormatter sf5 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_5);
            pieChart.addSegment(segments.get(5), sf5);
        }
        if((int)segments.get(6).getValue() > 0) {
            SegmentFormatter sf6 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_6);
            pieChart.addSegment(segments.get(6), sf6);
        }
        if((int)segments.get(7).getValue() > 0) {
            SegmentFormatter sf7 = new SegmentFormatter(getContext(), R.xml.pie_segment_formatter_7);
            pieChart.addSegment(segments.get(7), sf7);
        }
        pieChart.invalidate();
    }

    private int getColorFromIndex(int index) {
        switch(index) {
            case 0:
                return R.xml.pie_segment_formatter_0;
            case 1:
                return R.xml.pie_segment_formatter_1;
            case 2:
                return R.xml.pie_segment_formatter_2;
            case 3:
                return R.xml.pie_segment_formatter_3;
            case 4:
                return R.xml.pie_segment_formatter_4;
            case 5:
                return R.xml.pie_segment_formatter_5;
            case 6:
                return R.xml.pie_segment_formatter_6;
            case 7:
                return R.xml.pie_segment_formatter_7;
            default:
                return -1;
        }
    }

    //draws the data of the parameters, both need to be the same length otherwise the graph doesn't plot properly
    private void draw(int typeToPlot, Number[] dataList, String[] labels, Number[] domains) {

        plot.clear();
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).getPaint().setColor(Color.BLACK);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().setColor(Color.BLACK);
        plot.getLayoutManager().remove(plot.getLegend());

        if(dataList.length == 0) {
            tvInfo.setVisibility(TextView.VISIBLE);
        } else
            tvInfo.setVisibility(TextView.INVISIBLE);


        if (typeToPlot == 0) {
            plot.setTitle("Health Index");
            plot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
            plot.setDomainBoundaries(null, null, BoundaryMode.AUTO);
            final Number[] domainLabels = domains;

            XYSeries series = new SimpleXYSeries(Arrays.asList(domainLabels), Arrays.asList(dataList), "Series");
            LineAndPointFormatter seriesFormatter = new LineAndPointFormatter(Color.RED, null, null, null);
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
            return;
        }
        if(typeToPlot == 1) {
            plot.setTitle("Amount of Activities");
            BarFormatter bf = new BarFormatter(Color.RED, Color.WHITE);
            bf.setMarginLeft(PixelUtils.dpToPix(1));
            bf.setMarginRight(PixelUtils.dpToPix(1));
            XYSeries series = new SimpleXYSeries(Arrays.asList(dataList), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series");
            plot.addSeries(series, bf);
            BarRenderer renderer = plot.getRenderer(BarRenderer.class);
            plot.invalidate();
            renderer.setBarOrientation(BarRenderer.BarOrientation.SIDE_BY_SIDE);
            renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, PixelUtils.dpToPix(5));

        }
    }

}