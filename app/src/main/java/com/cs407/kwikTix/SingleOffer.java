package com.cs407.kwikTix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.IOException;
import java.io.InputStream;
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
public class SingleOffer extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userLoggedIn;
    private String mParam2;

    public SingleOffer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleTicket.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleOffer newInstance(String param1, String param2) {
        SingleOffer fragment = new SingleOffer();
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
        } else {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
            userLoggedIn = sharedPreferences.getString("username", "");
        }
    }

    private TextWatcher priceTextWatcher;

    private EditText counterOfferAmount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_single_offer, container, false);
        SQLiteDatabase sqLiteDatabase = v.getContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
        String userLoggedInUsername = sharedPreferences.getString("username","");

        counterOfferAmount = v.findViewById(R.id.OfferAmount);
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
        counterOfferAmount.addTextChangedListener(priceTextWatcher);

        // Retrieve the selectedListing from arguments
        Bundle args = getArguments();
        if (args != null) {
            Offer selectedOffer = (Offer) args.getSerializable("selectedOffer");
            if (selectedOffer != null) {
                LinearLayout offerButtonsLayout = v.findViewById(R.id.offerNotAccepted);
                if(selectedOffer.getStatus().equals("ACCEPTED")) {
                    offerButtonsLayout.setVisibility(View.GONE);
                } else {
                    offerButtonsLayout.setVisibility(View.VISIBLE);
                }
                //retrieve ticket that the offer is associated with
                Tickets ticket = dbHelper.getTicket(selectedOffer.getId());
                //Log.i("TEST",ticket.getTitle());
                //Log.i("TEST",selectedOffer.getBuyerUsername());
                // Update your UI with the selectedListing details
                TextView ticketNameTextView = v.findViewById(R.id.ticketName);
                ticketNameTextView.setText(ticket.getTitle());

                TextView locationNameTextView = v.findViewById(R.id.gameLocation);
                locationNameTextView.setText(ticket.getCollege()); // Set the college name

                TextView gameNameTextView = v.findViewById(R.id.gameName);
                gameNameTextView.setText(ticket.getTitle());

                TextView ticketPriceTextView = v.findViewById(R.id.ticketPrice);
                ticketPriceTextView.setText("$" + ticket.getPrice().toString());

                TextView sellerNameTextView = v.findViewById(R.id.sellerName);
                sellerNameTextView.setText(ticket.getSeller());

                TextView offeredPriceTextView = v.findViewById(R.id.offeredPrice);
                offeredPriceTextView.setText(selectedOffer.getOfferAmount());

                TextView offerStatusTextView = v.findViewById(R.id.offerStatus);
                offerStatusTextView.setText(selectedOffer.getStatus());

                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEE MM/dd/yyyy 'at' hh:mm a", Locale.getDefault());
                TextView dateTimeTextView = v.findViewById(R.id.dateTime);
                String dateString = ticket.getDate();
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

                Button changeOffer = v.findViewById(R.id.changeOfferButton);
                changeOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (counterOfferAmount.getText().toString().equals("")) {
                            Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(requireContext(), "Successfully sent counteroffer!", Toast.LENGTH_SHORT).show();
                        dbHelper.updateOffer(selectedOffer.getId(), counterOfferAmount.getText().toString(), userLoggedInUsername);
                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.popBackStack();
                    }
                });

                Button revokeOffer = v.findViewById(R.id.revokeOfferButton);
                revokeOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            dbHelper.deleteOffer(selectedOffer.getId());
                            Toast.makeText(requireContext(), "Successfully Revoked Offer", Toast.LENGTH_SHORT).show();
                            FragmentManager fragmentManager = getParentFragmentManager();
                            fragmentManager.popBackStack();
                        }catch (Exception e) {
                            Toast.makeText(requireContext(), "Error Revoking Offer", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        }

        return v;
    }

    private void formatPriceInput(Editable editable) {
        String input = editable.toString();
        //Log.i("TEST", "formatiing");

        // Remove previous formatting
        String cleanString = input.replaceAll("[^0-9]", "");

        // Format the input as currency
        if (!cleanString.isEmpty()) {
            double parsed = Double.parseDouble(cleanString);
            String formatted = DecimalFormat.getCurrencyInstance().format((parsed / 100));
            counterOfferAmount.removeTextChangedListener(priceTextWatcher);
            counterOfferAmount.setText(formatted);
            counterOfferAmount.setSelection(formatted.length());
            counterOfferAmount.addTextChangedListener(priceTextWatcher);
        }
    }

}