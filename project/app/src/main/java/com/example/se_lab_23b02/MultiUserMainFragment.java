package com.example.se_lab_23b02;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultiUserMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiUserMainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MultiUserMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MultiUserMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MultiUserMainFragment newInstance(String param1, String param2) {
        MultiUserMainFragment fragment = new MultiUserMainFragment();
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
        View view = inflater.inflate(R.layout.fragment_multi_user_main, container, false);
        Button topLeft = view.findViewById(R.id.ButtonTopLeft);
        Button topRight = view.findViewById(R.id.ButtonTopRight);
        Button bottomLeft = view.findViewById(R.id.ButtonBottomLeft);
        Button bottomRight = view.findViewById(R.id.ButtonBottomRight);

        //Just for testing-----------------------------------------------------
        ArrayList<User> users = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            User u = new User(i , String.valueOf(i),String.valueOf(i));
            users.add(u);
        }
        //----------------------------------------------------------------------


        topLeft.setText(users.get(0).getName());
        topRight.setText(users.get(1).getName());
        bottomLeft.setText(users.get(2).getName());
        bottomRight.setText(users.get(3).getName());

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        topLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction.replace(R.id.multi_user_frame_layout, MultiUserLoginFragment.newInstance(users.get(0).getName(),users.get(0).getPassword()));
                fragmentTransaction.commit();
            }
        });

        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction.replace(R.id.multi_user_frame_layout, MultiUserLoginFragment.newInstance(users.get(1).getName(),users.get(0).getPassword()));
                fragmentTransaction.commit();
            }
        });

        bottomLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction.replace(R.id.multi_user_frame_layout, MultiUserLoginFragment.newInstance(users.get(2).getName(),users.get(0).getPassword()));
                fragmentTransaction.commit();
            }
        });

        bottomRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction.replace(R.id.multi_user_frame_layout, MultiUserLoginFragment.newInstance(users.get(3).getName(),users.get(0).getPassword()));
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}