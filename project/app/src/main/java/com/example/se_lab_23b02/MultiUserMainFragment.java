package com.example.se_lab_23b02;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Map;

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

    private ArrayList<Button> buttons;

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

        //DataStorage
        ArrayList<User> users;
        if(DataSavingHelper.getInstance().getAllSettingsFromSharedPref(getContext()).containsKey("ActiveUser"))
        {
            resetActiveUser();
            users = getAllLoggedInUsers();
            DataSavingHelper.getInstance().saveSettingAsSharedPref("ActiveUser","None",getContext());
        }else{
            //If the values for The users are not in the settings file add them
            users = new ArrayList<>();
            DataSavingHelper.getInstance().saveSettingAsSharedPref("ActiveUser","None",getContext());
            DataSavingHelper.getInstance().saveSettingAsSharedPref("UserOne","",getContext());
            DataSavingHelper.getInstance().saveSettingAsSharedPref("UserTwo","",getContext());
            DataSavingHelper.getInstance().saveSettingAsSharedPref("UserThree","",getContext());
            DataSavingHelper.getInstance().saveSettingAsSharedPref("UserFour","",getContext());
        }

        users = getAllLoggedInUsers();
        //Log.d("User Length", String.valueOf(users.size()));
        buttonSetup(view, users);

        return view;
    }

    private void buttonSetup(View view, ArrayList<User> users)
    {
        buttons = new ArrayList<>();
        buttons.add(view.findViewById(R.id.ButtonTopLeft));
        buttons.add(view.findViewById(R.id.ButtonTopRight));
        buttons.add(view.findViewById(R.id.ButtonBottomLeft));
        buttons.add(view.findViewById(R.id.ButtonBottomRight));


        for(int i = 0; i < buttons.size(); i++)
        {
            if(users.size() < i)
            {
                buttons.get(i).setVisibility(View.GONE);
                buttons.get(i).setEnabled(false);
            }
        }
        buttons.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(users.size() > 0 ) {
                    buttons.get(0).setText(users.get(0).getName());
                    setActiveUser(users.get(0));
                    transition(users.get(0));
                    Log.d("ButtonTest","To Login");
                } else if(users.size() ==  0) {
                    //Make it to and "Add Button"
                    transitionToCreateUserFragment();
                    Log.d("ButtonTest","To Create new user");
                }
            }
        });

        buttons.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(users.size() > 1 ) {
                    buttons.get(1).setText(users.get(1).getName());
                    setActiveUser(users.get(1));
                    transition(users.get(1));
                    Log.d("ButtonTest","To Login");
                } else if(users.size() ==  1) {
                    //Make it to and "Add Button"
                    transitionToCreateUserFragment();
                    Log.d("ButtonTest","To Create new user");
                }
            }
        });


        buttons.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(users.size() > 2 ) {
                    buttons.get(2).setText(users.get(2).getName());
                    setActiveUser(users.get(2));
                    transition(users.get(2));
                    Log.d("ButtonTest","To Login");
                } else if(users.size() ==  2) {
                    //Make it to and "Add Button"
                    transitionToCreateUserFragment();
                    Log.d("ButtonTest","To Create new user");
                }
            }
        });

        buttons.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(users.size() > 3 ) {
                    buttons.get(3).setText(users.get(3).getName());
                    setActiveUser(users.get(3));
                    transition(users.get(3));
                    Log.d("ButtonTest","To Login");
                } else if(users.size() ==  3) {
                    //Make it to and "Add Button"
                    transitionToCreateUserFragment();
                    Log.d("ButtonTest","To Create new user");
                }
            }
        });
    }

    private void transition(User u)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.multi_user_frame_layout, MultiUserLoginFragment.newInstance(u.getName(),u.getPassword()));
        fragmentTransaction.commit();
    }

    private void transitionToCreateUserFragment()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //Need to create Fragment to be called
        fragmentTransaction.replace(R.id.multi_user_frame_layout, MultiUserCreateNewUserFragment.newInstance("",""));
        fragmentTransaction.commit();
    }
    private ArrayList<User> getAllLoggedInUsers()
    {
        ArrayList<User> users = new ArrayList<>();
        DataSavingHelper dsh = DataSavingHelper.getInstance();

        //Find all existing Users

        String s = dsh.getAllSettingsFromSharedPref(getContext()).get("UserOne");
        if(!s.equals(""))
        {
            users.add(new User(s));
        }
        s = dsh.getAllSettingsFromSharedPref(getContext()).get("UserTwo");
        if(!s.equals(""))
        {
            users.add(new User(s));
        }
        s = dsh.getAllSettingsFromSharedPref(getContext()).get("UserThree");
        if(!s.equals(""))
        {
            users.add(new User(s));
        }
        s = dsh.getAllSettingsFromSharedPref(getContext()).get("UserFour");
        if(!s.equals(""))
        {
            users.add(new User(s));
        }

        return users;
    }

    private void resetActiveUser()
    {
        DataSavingHelper dsh = DataSavingHelper.getInstance();
        dsh.saveSettingAsSharedPref("ActiveUser","None",getContext());
    }

    private void setActiveUser(User u)
    {
        DataSavingHelper dsh = DataSavingHelper.getInstance();
        dsh.saveSettingAsSharedPref("ActiveUser",u.toString(),getContext());
    }

}