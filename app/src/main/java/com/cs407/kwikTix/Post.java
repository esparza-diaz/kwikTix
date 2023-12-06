package com.cs407.kwikTix;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private String userLoggedIn;
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
            userLoggedIn = getArguments().getString("username");
        }
    }

    private EditText priceEditText;
    private TextWatcher priceTextWatcher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post, container, false);

        priceEditText = v.findViewById(R.id.priceEditText);

        // Add TextWatcher to format the price input
        priceTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // No action needed when text is changing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                formatPriceInput(editable);
            }
        };
        priceEditText.addTextChangedListener(priceTextWatcher);


        EditText dateTimeEditText = v.findViewById(R.id.dateTimeEditText);
        dateTimeEditText.setFocusable(false); // To disable manual editing

        // Set up DatePickerDialog
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            // Set the selected date to the editText
            String date = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
            dateTimeEditText.setText(date);
        };

        dateTimeEditText.setOnClickListener(view -> {
            // Show DatePickerDialog
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    dateSetListener,
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });

        EditText timeEditText = v.findViewById(R.id.timeEditText);
        timeEditText.setFocusable(false); // To disable manual editing

        // Set up TimePickerDialog
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            // Set the selected time to the editText
            String amPm = (hourOfDay < 12) ? "AM" : "PM";
            String time = String.format(Locale.US, "%02d:%02d %s", (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12, minute, amPm);
            timeEditText.setText(time);
        };

        timeEditText.setOnClickListener(view -> {
            // Show TimePickerDialog
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    timeSetListener,
                    hour,
                    minute,
                    false
            );
            timePickerDialog.show();
        });

        Spinner homeTeamSpinner = v.findViewById(R.id.homeTeamSpinner);
        Spinner awayTeamSpinner = v.findViewById(R.id.awayTeamSpinner);

        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SQLiteDatabase sqLiteDatabase = v.getContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE,null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        List<Colleges> collegeList = dbHelper.getAllColleges();

        teamAdapter.add("Choose College");
        // Add college names to the adapter
        for (Colleges college : collegeList) {
            teamAdapter.add(college.getCollege());
        }

        // Set the adapter to the Spinner
        homeTeamSpinner.setAdapter(teamAdapter);
        awayTeamSpinner.setAdapter(teamAdapter);

        Button postButton = v.findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the values from the input fields
                String homeTeam = homeTeamSpinner.getSelectedItem().toString();
                String awayTeam = awayTeamSpinner.getSelectedItem().toString();

                if (homeTeam.equals("Choose College") || awayTeam.equals("Choose College")) {
                    Toast.makeText(requireContext(), "Please select a college", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (homeTeam.equals(awayTeam)) {
                    Toast.makeText(requireContext(), "Home team cannot be the same as away team", Toast.LENGTH_SHORT).show();
                    return;
                }
                String gameTitle = homeTeam + " vs " + awayTeam;
                String price = ((EditText) v.findViewById(R.id.priceEditText)).getText().toString().replaceAll("[^0-9.]", "");;
                String college = ((Spinner) v.findViewById(R.id.homeTeamSpinner)).getSelectedItem().toString();
                String date = ((EditText) v.findViewById(R.id.dateTimeEditText)).getText().toString();
                String time = ((EditText) v.findViewById(R.id.timeEditText)).getText().toString();

                // Combine date and time into a DateTime object
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
                try {
                    calendar.setTime(dateFormat.parse(date + " " + time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String dateTime = calendar.getTime().toString();


                // Check if any of the fields is empty
                if (gameTitle.isEmpty() || price.isEmpty() || college.isEmpty() || dateTime.isEmpty()) {
                    // Show a message or handle the case where fields are empty
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                //double priceValue = Double.parseDouble(price);

                //Tickets newTicket = new Tickets(gameTitle, price, college, dateTime, "test");

                SQLiteDatabase sqLiteDatabase = view.getContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE,null);
                DBHelper dbHelper = new DBHelper(sqLiteDatabase);
                dbHelper.addTicket(gameTitle, dateTime,price, college, userLoggedIn,"1");

                Toast.makeText(requireContext(), "Successfully added " + gameTitle + " for $" + price, Toast.LENGTH_SHORT).show();
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

    private void formatPriceInput(Editable editable) {
        String input = editable.toString();

        // Remove previous formatting
        String cleanString = input.replaceAll("[^0-9]", "");

        // Format the input as currency
        if (!cleanString.isEmpty()) {
            double parsed = Double.parseDouble(cleanString);
            String formatted = DecimalFormat.getCurrencyInstance().format((parsed / 100));
            priceEditText.removeTextChangedListener(priceTextWatcher);
            priceEditText.setText(formatted);
            priceEditText.setSelection(formatted.length());
            priceEditText.addTextChangedListener(priceTextWatcher);
        }
    }
}