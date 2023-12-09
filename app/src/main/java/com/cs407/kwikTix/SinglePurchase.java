package com.cs407.kwikTix;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//import pl.droidsonroids.gif.GifDrawable;
//import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleMyListing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SinglePurchase extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userLoggedIn;
    private String mParam2;

    public SinglePurchase() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SinglePurchase.
     */
    // TODO: Rename and change types and number of parameters
    public static SinglePurchase newInstance(String param1, String param2) {
        SinglePurchase fragment = new SinglePurchase();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_single_purchase, container, false);
        SQLiteDatabase sqLiteDatabase = v.getContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        // Retrieve the selectedListing from arguments
        Bundle args = getArguments();
        if (args != null) {
            Offer selectedPurchase = (Offer) args.getSerializable("selectedPurchase");
            Tickets selectedPurchase = (Tickets) args.getSerializable("selectedPurchase");
            if (selectedPurchase != null) {
                //Tickets ticket = dbHelper.getTicket(selectedPurchase.getId());
                //retrieve ticket that the offer is associated with
                Log.i("TEST",selectedPurchase.getTitle());
                Log.i("TEST",selectedPurchase.getBuyer());
                // Update your UI with the selectedListing details
                TextView ticketNameTextView = v.findViewById(R.id.ticketName);
                ticketNameTextView.setText(selectedPurchase.getTitle());

                TextView locationNameTextView = v.findViewById(R.id.gameLocation);
                locationNameTextView.setText(selectedPurchase.getCollege()); // Set the college name

                TextView gameNameTextView = v.findViewById(R.id.gameName);
                gameNameTextView.setText(selectedPurchase.getTitle());

                TextView ticketPriceTextView = v.findViewById(R.id.ticketPrice);
                ticketPriceTextView.setText("$" + selectedPurchase.getPrice().toString());

                TextView sellerNameTextView = v.findViewById(R.id.sellerName);
                sellerNameTextView.setText(selectedPurchase.getSeller());

                TextView purchasedPriceTextView = v.findViewById(R.id.purchasedPrice);
                purchasedPriceTextView.setText("$" + selectedPurchase.getOfferAmount().toString());
                //TODO: change to method junior adds to Tickets

                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEE MM/dd/yyyy 'at' hh:mm a", Locale.getDefault());
                TextView dateTimeTextView = v.findViewById(R.id.dateTime);
                String dateString = selectedPurchase.getDate();
                try {
                    // Parse the input string into a Date object
                    Date date = inputFormat.parse(dateString);

                    // Format the Date object into the desired output format
                    String formattedDate = outputFormat.format(date);

                    // Set the formatted date to your TextView
                    dateTimeTextView.setText(formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    dateTimeTextView.setText(dateString);
                    // Handle parsing exception
                }
                // Handle Back button click
                ImageButton backButton = v.findViewById(R.id.backButton);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.popBackStack();
                    }
                });
            }
        }

        return v;
    }

}