package com.cs407.kwikTix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyListings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyListings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String userLoggedIn;

    public MyListings() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Listings.
     */
    // TODO: Rename and change types and number of parameters
    public static MyListings newInstance(String param1) {
        MyListings fragment = new MyListings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ListingAdapter adapter;
    ArrayList<Tickets> displayListings = new ArrayList<Tickets>();
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;

    ListView myticketsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile_listing, container, false);
        myticketsListView = (ListView) v.findViewById(R.id.myListings);

        // Init DB
        sqLiteDatabase = v.getContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
        userLoggedIn = sharedPreferences.getString("username","");


        displayListings = dbHelper.getListings(userLoggedIn,null, null, false);

        adapter = new ListingAdapter(v.getContext(), displayListings);
        myticketsListView.setAdapter(adapter);

        FragmentManager fragmentManager = getParentFragmentManager();
        myticketsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tickets selectedListing = displayListings.get(i);

                // Create a new instance of SingleTicketFragment and pass the selectedListing
                SingleMyListing singleListingFragment = new SingleMyListing();
                Bundle args = new Bundle();
                args.putSerializable("selectedListing", selectedListing);
                //args.putString("username",userLoggedIn);
                singleListingFragment.setArguments(args);

                // Replace Listings fragment with SingleTicketFragment
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, singleListingFragment)
                        //.setReorderingAllowed(true)
                        .addToBackStack("showing Single Listing")
                        .commit();
            }
        });

        // Handle Back button click
        ImageButton backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.popBackStack();
            }
        });

        return v;

    }

    public void refreshListings() {
        // Notify the adapter that the data has changed
        Log.i("TEST", displayListings.toString());

        if (displayListings.size() == 0){
            Toast.makeText(requireContext(),"No tickets found", Toast.LENGTH_LONG).show();
        }
        adapter.clear();
        adapter.addAll(displayListings);

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }
}