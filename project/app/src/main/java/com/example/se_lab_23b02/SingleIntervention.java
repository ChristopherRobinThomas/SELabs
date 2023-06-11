package com.example.se_lab_23b02;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static android.content.Intent.getIntent;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleIntervention#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleIntervention extends Fragment {

    private static String pInterventionName;
    TextView interventionName;
    Button completeButton;

    public SingleIntervention() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleIntervention.
     */
    public static SingleIntervention newInstance(String param1, String param2) {
        SingleIntervention fragment = new SingleIntervention();
        Bundle args = new Bundle();
        args.putString(pInterventionName, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pInterventionName = getArguments().getString("InterventionName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_intervention, container, false);

        interventionName = view.findViewById(R.id.textView3);
        completeButton = view.findViewById(R.id.buttonComplete);
        completeButton.setOnClickListener(v -> {
            System.out.println("Placeholder");
            // TODO: Do something
        });
        switch (pInterventionName) {
            case "sampleIntervention1":  interventionName.setText("Sample Intervention 1");
                break;
            default: interventionName.setText("Default Text");
                break;
        }

        return view;
    }

    public void interventionComplete() {

    }
}