package com.cs407.kwikTix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.core.app.NotificationManagerCompat;
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
import android.widget.TextView;

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

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
        userLoggedIn = sharedPreferences.getString("username","");
        Button logout = v.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                notificationManager.cancelAll();
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
                Intent intent = new Intent(requireContext(), ManageSettings.class);
                intent.putExtra("username", userLoggedIn);
                startActivity(intent);
            }
        });

        TextView usernameText = v.findViewById(R.id.username);
        usernameText.setText(userLoggedIn);
        LinearLayout myListings = v.findViewById(R.id.clickableMyListings);
        myListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                FragmentManager fragmentManager = getParentFragmentManager();
                MyListings listingsFragment = new MyListings();

                Bundle bundle = new Bundle();
                bundle.putString("username", userLoggedIn);
                listingsFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, listingsFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Listing")
                        .commit();
            }
        });

        LinearLayout myOffers = v.findViewById(R.id.clickableMyOffers);
        myOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                FragmentManager fragmentManager = getParentFragmentManager();
                MyOffers offersFragment = new MyOffers();

                Bundle bundle = new Bundle();
                bundle.putString("username", userLoggedIn);
                offersFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, offersFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Offer")
                        .commit();
            }
        });

        LinearLayout myPurchases = v.findViewById(R.id.clickableMyPurchases);
        myPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                FragmentManager fragmentManager = getParentFragmentManager();
                MyPurchases purchasesFragment = new MyPurchases();

                Bundle bundle = new Bundle();
                bundle.putString("username", userLoggedIn);
                purchasesFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, purchasesFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("Showing Purchase")
                        .commit();
            }
        });

        return v;
    }
}