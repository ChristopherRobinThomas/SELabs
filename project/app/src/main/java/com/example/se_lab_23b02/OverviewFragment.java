package com.example.se_lab_23b02;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

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


        Paint color = new Paint();
        color.setColor(Color.WHITE);
        plot.getGraph().setBackgroundPaint(color);
        plot.getGraph().setGridBackgroundPaint(color);


        Button btShowHealthGraph = (Button) view.findViewById(R.id.btHealthIndex);
        Button btNumOfInterventions = (Button) view.findViewById(R.id.btIntervention);

        btNumOfInterventions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("test");
                getActivity().setTitle("test");
            }
        });


        btShowHealthGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Number[] nums = {3, 4, 7, 8, 12, 15};
                System.out.println("click");
                draw(nums);
            }
        });

        return view;

    }



    public void draw(Number[] dataList) {

        plot.clear();

        final Number[] domainLabels = new Number[dataList.length];

        for (int i = 0; i < dataList.length; i++) {
            domainLabels[i] = i;
        }
        XYSeries series = new SimpleXYSeries(Arrays.asList(dataList), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series");
        LineAndPointFormatter seriesFormatter = new LineAndPointFormatter(Color.RED, null, null, null);

        plot.addSeries(series, seriesFormatter);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
        plot.invalidate();
    }

}