package com.cs407.kwikTix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
        //ListView ticketsListView = (ListView) v.findViewById(R.id.clickableMyListings);

        sqLiteDatabase = v.getContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        displayListings = dbHelper.getListings(userLoggedIn,null, null, false);

        Button logout = v.findViewById(R.id.logout);
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

        // Manage Settings button opens new activity to edit user information
        Button manageSettingsButton = v.findViewById(R.id.manageSettings);
        manageSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                //TODO Implement user updating: below line updates sql database
                // dbHelper.setUser(userLoggedIn, "new@gmail.com", "1233333412", "come@33me", "HOMESCHOLED");
                Intent intent = new Intent(requireContext(), ManageSettings.class);
                intent.putExtra("username", userLoggedIn);
                startActivity(intent);
            }
        });

        TicketAdapter adapter = new TicketAdapter(v.getContext(), displayListings);
        //ticketsListView.setAdapter(adapter);
        return v;

/*<<<<<<< HEAD
        LinearLayout myListings = v.findViewById(R.id.clickableMyListings);
        myListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                FragmentManager fragmentManager = getParentFragmentManager();
                Listings listingsFragment = new Listings();

                Bundle bundle = new Bundle();
                bundle.putString("username", userLoggedIn);
                listingsFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, listingsFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("showing Post")
                        .commit();
            }
        });

        LinearLayout myOffers = v.findViewById(R.id.clickableMyOffers);
        myOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                FragmentManager fragmentManager = getParentFragmentManager();
                Offers offersFragment = new Offers();

                Bundle bundle = new Bundle();
                bundle.putString("username", userLoggedIn);
                offersFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, offersFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("showing Post")
                        .commit();
            }
        });

        sqLiteDatabase = v.getContext().openOrCreateDatabase("kwikTix", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        displayListings = dbHelper.getListings(userLoggedIn);
=======*/


    }
}