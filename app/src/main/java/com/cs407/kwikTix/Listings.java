package com.cs407.kwikTix;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Listings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Listings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static ArrayList<Listing> listings;

    public Listings() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Listings.
     */
    // TODO: Rename and change types and number of parameters
    public static Listings newInstance(String param1, String param2) {
        Listings fragment = new Listings();
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

    TicketAdapter adapter;
    ArrayList<Tickets> displayListings;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.listings_fragment, container, false);
        ListView ticketsListView = (ListView) v.findViewById(R.id.myListings);

        Tickets t1 = new Tickets("Iowa Game", "Jan 1st 8:00pm", "50.00", "Texas", "test");
        Tickets t2 = new Tickets("Nebraska Game", "Jan 2st 8:00pm", "75.00", "Texas", "test");
        // Init DB
        sqLiteDatabase = v.getContext().openOrCreateDatabase("kwikTix", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);

        dbHelper.addTicket(t1.getTitle(), t1.getDate(), t1.getPrice(), t1.getCollege(), t1.getUsername());
        dbHelper.addTicket(t2.getTitle(), t2.getDate(), t2.getPrice(), t2.getCollege(), t2.getUsername());
        // TODO: HARDCODED LISTINGS
        displayListings = dbHelper.getListings();

        TicketAdapter adapter = new TicketAdapter(v.getContext(), displayListings);
        ticketsListView.setAdapter(adapter);

        FragmentManager fragmentManager = getParentFragmentManager();
        ticketsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tickets selectedListing = displayListings.get(i);

                // Create a new instance of SingleTicketFragment and pass the selectedListing
                SingleTicket singleTicketFragment = new SingleTicket();
                Bundle args = new Bundle();
                args.putSerializable("selectedListing", selectedListing);
                singleTicketFragment.setArguments(args);

                // Replace Listings fragment with SingleTicketFragment
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, singleTicketFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("showing Single Ticket")
                        .commit();
            }
        });

        return v;
    }

    public void refreshListings() {
        // TODO: Update the data from the database
        displayListings = dbHelper.getListings();

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }
}