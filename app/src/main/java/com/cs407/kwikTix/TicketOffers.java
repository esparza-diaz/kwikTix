package com.cs407.kwikTix;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicketOffers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicketOffers extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userLoggedIn;
    public TicketOffers() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Listings.
     */
    // TODO: Rename and change types and number of parameters
    public static TicketOffers newInstance(String param1, String param2) {
        TicketOffers fragment = new TicketOffers();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TicketOfferAdapter adapter;
    ArrayList<Offer> displayTicketOffers = new ArrayList<Offer>();
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    ListView ticketOffersListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile_ticket_offers, container, false);
        ticketOffersListView = (ListView) v.findViewById(R.id.ticketOffers);

        // Init DB
        sqLiteDatabase = v.getContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
        userLoggedIn = sharedPreferences.getString("username","");

        Bundle args = getArguments();
        Tickets ticket = (Tickets) args.getSerializable("selectedListing");

        //filter offers by username, not by ticket id
        ArrayList<Offer> allOffers = dbHelper.getOffers(null,ticket.getId());
        displayTicketOffers.clear();

        for (Offer offer: allOffers) {
            if (!offer.getStatus().equals("REJECTED")) {
                displayTicketOffers.add(offer);
            }
        }
        //displayTicketOffers = dbHelper.getOffers(null,ticket.getId());
        adapter = new TicketOfferAdapter(v.getContext(), displayTicketOffers);
        adapter.setUsername(userLoggedIn);
        ticketOffersListView.setAdapter(adapter);

        TextView gameNameTextView = v.findViewById(R.id.gameName);
        gameNameTextView.setText(ticket.getTitle());

        TextView ticketPriceTextView = v.findViewById(R.id.ticketPrice);
        ticketPriceTextView.setText(ticket.getPrice());

        ticketOffersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fragmentManager = getParentFragmentManager();
                Offer selectedOffer = displayTicketOffers.get(i);

                // Create a new instance of SingleTicketOfferFragment and pass the selectedOffer
                SingleOffer singleOffer = new SingleOffer();
                Bundle args = new Bundle();
                args.putSerializable("selectedOffer", selectedOffer);
                singleOffer.setArguments(args);

                // Replace Listings fragment with SingleTicketFragment
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, singleOffer)
                        //.setReorderingAllowed(true)
                        .addToBackStack("showing Single Listing")
                        .commit();

                //TODO: add methods for onClick of buttons to accept or reject offers
                //not sure if these methods will go inside or outside this onItemClick method

            }
        });

        // Handle Back button click
        ImageButton backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        return v;
    }


    public void refreshOffers() {
        // Notify the adapter that the data has changed
        //Log.i("TEST", displayTicketOffers.toString());

        if (displayTicketOffers.size() == 0){
            Toast.makeText(requireContext(),"No offers found", Toast.LENGTH_LONG).show();
        }
        adapter.clear();
        adapter.addAll(displayTicketOffers);

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }
}