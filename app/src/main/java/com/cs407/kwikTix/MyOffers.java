package com.cs407.kwikTix;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyOffers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyOffers extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String sellerUsername;
    private String userLoggedIn;
    private String offerAmount;

    public static ArrayList<Offer> t1offers;
    public static ArrayList<Offer> t2offers;

    public MyOffers() {
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
    public static MyOffers newInstance(Tickets t, String param1, String param2, String param3) {
        MyOffers fragment = new MyOffers();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sellerUsername = getArguments().getString("sellerUsername");
            userLoggedIn = getArguments().getString("buyerUsername");
            offerAmount = getArguments().getString("offerAmount");
        }
    }

    OfferAdapter adapter;
    ArrayList<Offer> displayOffers;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile_my_offers, container, false);
        ListView offersListView = (ListView) v.findViewById(R.id.myOffers);

        // Init DB
        sqLiteDatabase = v.getContext().openOrCreateDatabase("kwikTix", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);

        //Tickets t1 = dbHelper.getTicket("Iowa Game");
        //Tickets t2 = dbHelper.getTicket("Nebraska Game");

        //displayOffers = dbHelper.getMyOffers(userLoggedIn);
       // dbHelper.addOffer(t1, userLoggedIn, 1, 40.00);
       // dbHelper.addOffer(t1, userLoggedIn, 2, 45.00);
       // dbHelper.addOffer(t2, userLoggedIn, 1, 60.00);
        OfferAdapter adapter = new OfferAdapter(v.getContext(), displayOffers);
        offersListView.setAdapter(adapter);

        FragmentManager fragmentManager = getParentFragmentManager();
        offersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Offer selectedOffer = displayOffers.get(i);

                // Create a new instance of SingleTicketFragment and pass the selectedListing
                SingleOffer singleOfferFragment = new SingleOffer();
                Bundle args = new Bundle();
                args.putSerializable("selectedOffer", selectedOffer);
                args.putString("buyerUsername",userLoggedIn);
                singleOfferFragment.setArguments(args);

                // TODO: Replace Listings fragment with single offer fragment, not singleTicketFragment
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, singleOfferFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("showing Single Offer")
                        .commit();
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
//        for (Colleges college : collegeList) {
//            spinnerAdapter.add(college.getCollege());
//        }


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


    public void refreshOffers() {
        // TODO: Update the data from the database
        //displayOffers = dbHelper.getMyOffers(userLoggedIn);
        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }
}