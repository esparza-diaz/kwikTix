package com.cs407.kwikTix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
    private String userLoggedIn;
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
            userLoggedIn = getArguments().getString("username");
        }
    }

    TicketAdapter adapter;
    ArrayList<Tickets> displayListings;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    List<Colleges> collegeList;

    ListView ticketsListView;

    // inputs used for filter
    String college = "All Colleges";
    String sort_by = null;
    boolean desc = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.listings_fragment, container, false);
        ticketsListView = (ListView) v.findViewById(R.id.myListings);

        Tickets t1 = new Tickets("Iowa Game", "Jan 1st 8:00pm", "50.00", "Texas", "test");
        Tickets t2 = new Tickets("Nebraska Game", "Jan 2st 8:00pm", "75.00", "Texas", "test");
        // Init DB
        sqLiteDatabase = v.getContext().openOrCreateDatabase("kwikTix", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);

        //dbHelper.addTicket(t1.getTitle(), t1.getDate(), t1.getPrice(), t1.getCollege(), t1.getUsername());
        //dbHelper.addTicket(t2.getTitle(), t2.getDate(), t2.getPrice(), t2.getCollege(), t2.getUsername());
        // TODO: HARDCODED LISTINGS
        displayListings = dbHelper.getListings(null,null, null, false);

        collegeList = dbHelper.getAllColleges();

        adapter = new TicketAdapter(v.getContext(), displayListings);
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
                // args.putString("username",userLoggedIn);
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
                college = (String) collegeSpinner.getSelectedItem();
                // Apply the filter based on the selected college
                // ...
                ImageView imageView = view.findViewById(R.id.filterButton);
                if (college.equals("All Colleges")) {
                    imageView.setColorFilter(null);
                }else{
                    int color = Color.parseColor("#FF0000");
                    imageView.setColorFilter(color);
                }

                refreshListings();

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
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                ImageView imageView = view.findViewById(R.id.sortButton);
                if (itemId == R.id.ascendingPrice) {
                    sort_by = "price";
                    desc = false;
                    int color = Color.parseColor("#FF0000");
                    imageView.setColorFilter(color);
                } else if(itemId == R.id.descendingPrice) {
                    sort_by = "price";
                    desc = true;
                    int color = Color.parseColor("#FF0000");
                    imageView.setColorFilter(color);
                } else if(itemId == R.id.ascendingTime) {
                    sort_by = "date";
                    desc = false;
                    int color = Color.parseColor("#FF0000");
                    imageView.setColorFilter(color);
                } else if (itemId == R.id.descendingTime) {
                    sort_by = "date";
                    desc = true;
                    int color = Color.parseColor("#FF0000");
                    imageView.setColorFilter(color);
                } else if (itemId == R.id.noFilter) {
                    sort_by = null;
                    imageView.setColorFilter(null);
                }
                refreshListings();
                return true;
            }
        });
        popupMenu.show();
    }

    public void refreshListings() {
        // TODO: Update the data from the database
        if(college.equals("All Colleges")) {
            displayListings = dbHelper.getListings(null, null, sort_by, desc);
        } else {
            displayListings = dbHelper.getListings(null, college, sort_by, desc);
        }
        // Notify the adapter that the data has changed
        Log.i("TEST", displayListings.toString());

        if (displayListings.size() == 0){
            Toast.makeText(requireContext(),"No tickets found. Modify filters.", Toast.LENGTH_LONG).show();
        }
        adapter.clear();
        adapter.addAll(displayListings);

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }
}