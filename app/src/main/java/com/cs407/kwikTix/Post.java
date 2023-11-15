package com.cs407.kwikTix;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Post#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Post extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Post() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Post.
     */
    // TODO: Rename and change types and number of parameters
    public static Post newInstance(String param1, String param2) {
        Post fragment = new Post();
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
        View v = inflater.inflate(R.layout.fragment_post, container, false);

        Spinner collegeSpinner = v.findViewById(R.id.collegeSpinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SQLiteDatabase sqLiteDatabase = v.getContext().openOrCreateDatabase("kwikTix", Context.MODE_PRIVATE,null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        List<Colleges> collegeList = dbHelper.getAllColleges();

        // Add college names to the adapter
        for (Colleges college : collegeList) {
            spinnerAdapter.add(college.getCollege());
        }

        // Set the adapter to the Spinner
        collegeSpinner.setAdapter(spinnerAdapter);

        Button postButton = v.findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the values from the input fields
                String gameTitle = ((EditText) v.findViewById(R.id.gameTitleEditText)).getText().toString();
                String price = ((EditText) v.findViewById(R.id.priceEditText)).getText().toString();
                String college = ((Spinner) v.findViewById(R.id.collegeSpinner)).getSelectedItem().toString();
                String dateTime = ((EditText) v.findViewById(R.id.dateTimeEditText)).getText().toString();

                // Check if any of the fields is empty
                if (gameTitle.isEmpty() || price.isEmpty() || college.isEmpty() || dateTime.isEmpty()) {
                    // Show a message or handle the case where fields are empty
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                //double priceValue = Double.parseDouble(price);

                //Tickets newTicket = new Tickets(gameTitle, price, college, dateTime, "test");

                SQLiteDatabase sqLiteDatabase = view.getContext().openOrCreateDatabase("kwikTix", Context.MODE_PRIVATE,null);
                DBHelper dbHelper = new DBHelper(sqLiteDatabase);
                dbHelper.addTicket(gameTitle, price, college, dateTime, "test");

                Listings listingsFragment = (Listings) getParentFragmentManager().findFragmentByTag("showing Listings");
                if (listingsFragment != null) {
                    listingsFragment.refreshListings();
                }
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, Listings.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("showing Listings")
                        .commit();
            }
        });

        return v;
    }
}