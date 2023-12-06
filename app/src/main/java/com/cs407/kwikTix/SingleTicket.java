package com.cs407.kwikTix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//import pl.droidsonroids.gif.GifDrawable;
//import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleTicket#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleTicket extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private SQLiteDatabase sqLiteDatabase;
    private DBHelper dbHelper;
    private Users seller;
    private Users userLoggedIn; // buyer
    private String userLoggedInUsername;
    private String sellerUsername;
    private String mParam2;

    public SingleTicket() {
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
    public static SingleTicket newInstance(String param1, String param2) {
        SingleTicket fragment = new SingleTicket();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
        userLoggedInUsername = sharedPreferences.getString("username", "");
        Log.d("In Single Ticket onCreate", userLoggedInUsername);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_single_ticket, container, false);
        sqLiteDatabase = v.getContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
        userLoggedInUsername = sharedPreferences.getString("username","");
        // Retrieve the selectedListing from arguments
        Bundle args = getArguments();
        if (args != null) {
            Tickets selectedListing = (Tickets) args.getSerializable("selectedListing");
            if (selectedListing != null) {
                // Setting buyer and seller arguments to be used in notifications
                userLoggedIn = dbHelper.getUser(userLoggedInUsername); // TODO maybe change  names for more clarity
                if (userLoggedIn == null) {
                    Log.d("userLoggedIn", "NULL");
                } else {
                    Log.d("userLoggedIn", "onCreateView: " + userLoggedIn.getUsername());
                }
                sellerUsername = selectedListing.getSeller().toString(); // TODO redundant toString?
                seller = dbHelper.getUser(sellerUsername);

                Log.i("TEST",selectedListing.getTitle());
                Log.i("TEST", sellerUsername);
                Log.i("TEST",selectedListing.getSeller());
                // update offer notice if previously offered.
                ArrayList<Offer> offers = dbHelper.getOffers(null,selectedListing.getId());
                for (Offer offer : offers){
                    if (offer.getBuyerUsername().equals(userLoggedIn)){
                        TextView notice = v.findViewById(R.id.offerNotice);
                        notice.setText("You previously submitted an offer for $" + offer.getOfferAmount());

                    }
                }

                // Update your UI with the selectedListing details
                TextView ticketNameTextView = v.findViewById(R.id.ticketName);
                ticketNameTextView.setText(selectedListing.getTitle());


                TextView locationNameTextView = v.findViewById(R.id.gameLocation);
                locationNameTextView.setText(selectedListing.getCollege()); // Set the college name


                // Set an OnClickListener to open Google Maps
                locationNameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openGoogleMapsForLocation(dbHelper.getCollege(selectedListing.getCollege()));
                    }
                });

                TextView gameNameTextView = v.findViewById(R.id.gameName);
                gameNameTextView.setText(selectedListing.getTitle());

                TextView ticketPriceTextView = v.findViewById(R.id.ticketPrice);
                ticketPriceTextView.setText("$" + selectedListing.getPrice().toString());

                TextView sellerNameTextView = v.findViewById(R.id.sellerName);
                sellerNameTextView.setText(selectedListing.getSeller().toString()); // TODO redundant toString?
                sellerNameTextView.setText(selectedListing.getSeller().toString());

                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEE MM/dd/yyyy 'at' hh:mm a", Locale.getDefault());
                TextView dateTimeTextView = v.findViewById(R.id.dateTime);
                String dateString = selectedListing.getDate().toString();
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

                Button buy = v.findViewById(R.id.buyNowButton);
                buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbHelper.boughtTicket(selectedListing, userLoggedInUsername);
                        showCongratulationsPopup();
                        // TODO make notification when buy is clicked; also add strings to resources
                        NotificationHelper notificationHelper = NotificationHelper.getInstance();
                        notificationHelper.setNotificationContent(
                                requireContext(),
                                userLoggedIn,
                                seller,
                                selectedListing.getTitle(),
                                -1,
                                2,
                                getContext().getString(R.string.SELLER_TICKET_PURCHASED));
                        notificationHelper.showNotification(requireContext(), -1);
                    }
                });

                Button counter = v.findViewById(R.id.counterOfferButton);
                TextView counterOfferAmount = v.findViewById(R.id.counterOfferAmount);
                counter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Inside the onClickListener for the counterOfferButton
                        showCounterOfferPopup(selectedListing, counterOfferAmount.getText().toString());

                        NotificationHelper notificationHelper = NotificationHelper.getInstance();
                        notificationHelper.setNotificationContent(
                                requireContext(),
                                userLoggedIn,
                                seller,
                                selectedListing.getTitle(),
                                Integer.parseInt(counterOfferAmount.getText().toString()),
                                0,
                                "Accept or Reject");
                        notificationHelper.showNotification(requireContext(), -1);
                    }
                });
            }
        }

        return v;
    }

    private void openGoogleMapsForLocation(Colleges college) {
        // Get college information from the database

        if (college != null) {
            // Open Google Maps with the location of the college
            double latitude = Double.parseDouble(college.getLatitude());
            double longitude = Double.parseDouble(college.getLongitude());
            String uri = String.format("geo:%f,%f?q=%f,%f", latitude, longitude, latitude, longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        } else {
            // Handle the case where college information is not available
            Toast.makeText(requireContext(), "College information not available", Toast.LENGTH_SHORT).show();
        }
    }


    private void showCongratulationsPopup() {
        View overlayView = LayoutInflater.from(requireContext()).inflate(R.layout.overlay_confetti, null);

//        GifImageView confettiGif = overlayView.findViewById(R.id.confettiGif);
//        try {
//            InputStream inputStream = requireContext().getAssets().open("confetti.gif");
//            confettiGif.setImageDrawable(new GifDrawable(inputStream));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        View rootView = requireActivity().getWindow().getDecorView().getRootView();

        ((ViewGroup) rootView).addView(overlayView);

        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_congratulations, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        new Handler().postDelayed(() -> {
            ((ViewGroup) rootView).removeView(overlayView);

            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }

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
        }, 4500);
    }
    private void showCounterOfferPopup(Tickets listing, String offerAmount) {
        if (!isValidNumber(offerAmount)) {
            // Show an error message or handle the invalid input
            Toast.makeText(requireContext(), "Invalid offer amount", Toast.LENGTH_SHORT).show();
            return;
        }
        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_counteroffer, null);
        String message = "";
        try {
            dbHelper.addOffer(listing.getId(), offerAmount, userLoggedInUsername, "PENDING");
            message = "We successfully sent a new CounterOffer to " + listing.getSeller() + " for $" + offerAmount;
        }catch(SQLiteConstraintException e){
            dbHelper.updateOffer(listing.getId(), offerAmount, userLoggedInUsername);
            message = "We successfully sent your updated CounterOffer to " + listing.getSeller() + " for $" + offerAmount;
        }

        // Replace placeholders with actual values
        ((TextView) popupView.findViewById(R.id.counterOfferMessage)).setText(message);

        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);

        // Show the popup at the center of the screen
        View rootView = requireActivity().getWindow().getDecorView().getRootView();
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        // Dismiss the popup after a certain delay
        new Handler().postDelayed(() -> {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }, 5000); // Adjust the delay (in milliseconds) as needed

        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }

    private boolean isValidNumber(String input) {
        try {
            double parsed = Double.parseDouble(input);
            return parsed >= 0; // You can add more specific validation as needed
        } catch (NumberFormatException e) {
            return false;
        }
    }








}