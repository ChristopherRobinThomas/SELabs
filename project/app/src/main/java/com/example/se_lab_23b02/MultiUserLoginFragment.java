package com.example.se_lab_23b02;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.sql.Time;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultiUserLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiUserLoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private String password;

    public MultiUserLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MultiUserLoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MultiUserLoginFragment newInstance(String param1, String param2) {
        MultiUserLoginFragment fragment = new MultiUserLoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_multi_user_login, container, false);
        TextView username = view.findViewById(R.id.Login_Name);
        username.setText(mParam1);

        //BackButton
        view.findViewById(R.id.BackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.multi_user_frame_layout, new MultiUserMainFragment());
                fragmentTransaction.commit();
            }
        });
        //Login Button
        view.findViewById(R.id.LoginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText testPassword = view.findViewById(R.id.PasswordInput);
                if(testPassword.getText().toString() != null){
                    Log.d("Login Test",testPassword.getText().toString());
                    Log.d("Login Test",mParam2);
                    Log.d("Login Test",String.valueOf(testPassword.getText().toString().equals(mParam2)));
                    if(testPassword.getText().toString().equals(mParam2)){
                        TimeStamp.setStandardTimers(getContext());

                        Intent myIntent = new Intent(getActivity(), HomeScreenActivity.class);
                        startActivity(myIntent);
                        getActivity().finish();
                    }else
                    {
                        Toast.makeText(getActivity(),"Wrong password",Toast.LENGTH_LONG);
                    }
                }
            }
        });

        return view;
    }
}