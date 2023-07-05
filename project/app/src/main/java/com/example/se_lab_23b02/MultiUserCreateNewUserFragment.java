package com.example.se_lab_23b02;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultiUserCreateNewUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiUserCreateNewUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MultiUserCreateNewUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MultiUserCreateNewUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MultiUserCreateNewUserFragment newInstance(String param1, String param2) {
        MultiUserCreateNewUserFragment fragment = new MultiUserCreateNewUserFragment();
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
        View view = inflater.inflate(R.layout.fragment_multi_user_create_new_user, container, false);

        Button back = (Button) view.findViewById(R.id.CreateNewUser_BackButton);

        view.findViewById(R.id.MultiUser_TermsOfService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show Terms of service
            }
        });

        back.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.multi_user_frame_layout, new MultiUserMainFragment());
                fragmentTransaction.commit();
            }
        });

        Button NewUser = (Button) view.findViewById(R.id.CreateNewUser_LoginButton);
        TextInputEditText usernameTextField = (TextInputEditText) view.findViewById(R.id.CreateNewUser_UsernameTextField);
        TextInputEditText passwordTextField = (TextInputEditText) view.findViewById(R.id.CreateNewUser_PasswordTextField);
        NewUser.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                int localId = getLocalId();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(localId == -1) {
                    fragmentTransaction.replace(R.id.multi_user_frame_layout, new MultiUserMainFragment());
                    fragmentTransaction.commit();
                } else {

                    String username = usernameTextField.getText().toString();
                    String password = passwordTextField.getText().toString();

                    if (username.equals("") || password.equals("")){
                        Toast.makeText(getContext(), "Please ente username AND password!", Toast.LENGTH_SHORT);
                        return;
                    }

                    User newUser = new User(localId,username,password);

                    DataSavingHelper ds = DataSavingHelper.getInstance();
                    ds.saveSettingAsSharedPref("ActiveUser", newUser.toString(),getContext());
                    switch (localId)
                    {
                        case 1:
                            ds.saveSettingAsSharedPref("UserOne", newUser.toString(),getContext());
                            break;
                        case 2:
                            ds.saveSettingAsSharedPref("UserTwo", newUser.toString(),getContext());
                            break;
                        case 3:
                            ds.saveSettingAsSharedPref("UserThree", newUser.toString(),getContext());
                            break;
                        case 4:
                            ds.saveSettingAsSharedPref("UserFour", newUser.toString(),getContext());
                            break;
                        default: break;
                    }
                }
                Intent myIntent = new Intent(getActivity(), HomeScreenActivity.class);
                startActivity(myIntent);
                getActivity().finish();
            }
        });
        Button TOS = (Button) view.findViewById(R.id.MultiUser_TermsOfService);
        TOS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String tosText = "Terms and Conditions  \n====================\n\nLast updated: July 04, 2023\nPlease read these terms and conditions carefully before using Our Service.\n\nInterpretation and Definitions  \n==============================\n\nInterpretation  \n--------------\n\nThe words of which the initial letter is capitalized have meanings defined\nunder the following conditions. The following definitions shall have the same\nmeaning regardless of whether they appear in singular or in plural.\n\nDefinitions  \n-----------";
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Terms of Service");
                final TextView tos = new TextView(getContext());
                tos.setText(tosText);
                builder.setView(tos);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                builder.show();
            }
        });



        return view;
    }

    private int getLocalId()
    {
        DataSavingHelper ds = DataSavingHelper.getInstance();
        if(!ds.getAllSettingsFromSharedPref(getContext()).containsKey("ActiveUser"))
            return -1;
        if(ds.getAllSettingsFromSharedPref(getContext()).get("UserOne").equals(""))
        {
            return 1;
        }
        if(ds.getAllSettingsFromSharedPref(getContext()).get("UserTwo").equals(""))
        {
            return 2;
        }
        if(ds.getAllSettingsFromSharedPref(getContext()).get("UserThree").equals(""))
        {
            return 3;
        }
        if(ds.getAllSettingsFromSharedPref(getContext()).get("UserFour").equals(""))
        {
            return 4;
        }
        return -1;
    }
}