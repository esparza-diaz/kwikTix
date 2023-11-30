package com.cs407.kwikTix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userLoggedIn;
    private String mParam2;

    public Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
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
            userLoggedIn = getArguments().getString("username");
        }
    }

    TicketAdapter adapter;
    ArrayList<Tickets> displayListings;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        ListView ticketsListView = (ListView) v.findViewById(R.id.profileListings);

        //TODO: Current using this button to logout, must change
        Button logout = v.findViewById(R.id.manageSettings);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clear sharedPreference
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        sqLiteDatabase = v.getContext().openOrCreateDatabase("kwikTix", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        displayListings = dbHelper.getListings(userLoggedIn,null, null, false);

        TicketAdapter adapter = new TicketAdapter(v.getContext(), displayListings);
        ticketsListView.setAdapter(adapter);
        return v;
    }
}