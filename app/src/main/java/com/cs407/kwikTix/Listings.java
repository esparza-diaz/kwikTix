package com.cs407.kwikTix;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.listings_fragment, container, false);
        ListView notesListView = (ListView) v.findViewById(R.id.myListings);

        Listing l1 = new Listing("Iowa Game", 50.00);
        Listing l2 = new Listing("Iowa Game", 70.00);
        Listing l3 = new Listing("Nebraska Game", 55.00);

        // TODO: HARDCODED LISTINGS
        ArrayList<String> displayListings = new ArrayList<>();
        displayListings.add(String.format("Title:%s\nPrice: $%s\n", l1.getTitle(), l1.getPrice()));
        displayListings.add(String.format("Title:%s\nPrice: $%s\n", l2.getTitle(), l2.getPrice()));
        displayListings.add(String.format("Title:%s\nPrice: $%s\n", l3.getTitle(), l3.getPrice()));
        ArrayAdapter adapter = new ArrayAdapter(v.getContext(), android.R.layout.simple_list_item_1, displayListings);
        notesListView.setAdapter(adapter);
        return v;
    }
}