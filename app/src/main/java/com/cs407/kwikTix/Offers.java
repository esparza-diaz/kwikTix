package com.cs407.kwikTix;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Offers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Offers extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userLoggedIn;
    private String mParam2;

    public static ArrayList<Offer> offers;

    public Offers() {
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
    public static Offers newInstance(String param1, String param2) {
        Offers fragment = new Offers();
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
    ArrayList<Tickets> displayOffers;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    List<Colleges> collegeList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.offers_fragment_placeholder, container, false);
        ListView ticketsListView = (ListView) v.findViewById(R.id.myOffers);

        Tickets t1 = new Tickets("Iowa Game", "Jan 1st 8:00pm", "50.00", "Texas", "test");
        Tickets t2 = new Tickets("Nebraska Game", "Jan 2st 8:00pm", "75.00", "Texas", "test");
        //Tickets t1 = new Tickets("Iowa Game", "Jan 1st 8:00pm", "50.00", "Texas", "test", "40.00");
        //Tickets t2 = new Tickets("Nebraska Game", "Jan 2st 8:00pm", "75.00", "Texas", "test", "60.00");

        // Init DB
        sqLiteDatabase = v.getContext().openOrCreateDatabase("kwikTix", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);

        dbHelper.addTicket(t1.getTitle(), t1.getDate(), t1.getPrice(), t1.getCollege(), t1.getUsername());
        dbHelper.addTicket(t2.getTitle(), t2.getDate(), t2.getPrice(), t2.getCollege(), t2.getUsername());
        // TODO: figure out how to add the offer amount(s) associated with tickets
        //dbHelper.addTicket(t1.getTitle(), t1.getDate(), t1.getPrice(), t1.getCollege(), t1.getUsername(), t1.getOfferAmount());
        //dbHelper.addTicket(t2.getTitle(), t2.getDate(), t2.getPrice(), t2.getCollege(), t2.getUsername(), t2.getOfferAmount());

        // TODO: HARDCODED LISTINGS
        displayOffers = dbHelper.getOffers();

        collegeList = dbHelper.getAllColleges();

        TicketAdapter adapter = new TicketAdapter(v.getContext(), displayOffers);
        ticketsListView.setAdapter(adapter);

        FragmentManager fragmentManager = getParentFragmentManager();
        ticketsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tickets selectedOffer = displayOffers.get(i);

                // Create a new instance of SingleTicketFragment and pass the selectedListing
                SingleTicket singleTicketFragment = new SingleTicket();
                Bundle args = new Bundle();
                args.putSerializable("selectedOffer", selectedOffer);
                args.putString("username",userLoggedIn);
                singleTicketFragment.setArguments(args);

                // Replace Listings fragment with SingleTicketFragment
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, singleTicketFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("showing Single Ticket")
                        .commit();
            }
        });

        ImageButton sortButton = v.findViewById(R.id.sortButton);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSortMenu(view);
            }
        });


        ImageButton filterButton = v.findViewById(R.id.filterButton);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCollegeFilterPopup(view);
            }
        });

        return v;
    }

    private void showCollegeFilterPopup(View view) {
        // Inflate the popup layout
        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.filter_menu, null);

        // Set up the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);

        Spinner collegeSpinner = popupView.findViewById(R.id.collegesSpinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAdapter.add("All Colleges");
        // Add college names to the adapter
        for (Colleges college : collegeList) {
            spinnerAdapter.add(college.getCollege());
        }


        collegeSpinner.setAdapter(spinnerAdapter);
        // Set up the filter button in the popup
        Button filterByCollegeButton = popupView.findViewById(R.id.filterByCollegeButton);
        filterByCollegeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle filter by college
                String selectedCollege = (String) collegeSpinner.getSelectedItem();
                // Apply the filter based on the selected college
                // ...

                // Dismiss the popup
                popupWindow.dismiss();
            }
        });

        // Show the popup at the center of the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void showSortMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.sort_menu); // Create a menu resource file (res/menu/filter_menu.xml)

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //handleFilterSelection(menuItem.getItemId());
                return true;
            }
        });

        popupMenu.show();
    }

    public void refreshListings() {
        // TODO: Update the data from the database
        displayListings = dbHelper.getListings();

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }
}