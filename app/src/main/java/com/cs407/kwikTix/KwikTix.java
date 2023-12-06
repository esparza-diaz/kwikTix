package com.cs407.kwikTix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationBarView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class KwikTix  extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();
    private String userLoggedInUsername;


    private void postUserNotifications(String userLoggedInUsername) {
        Users seller;
        Users buyer;
        Context context = getApplicationContext();
        NotificationHelper notificationHelper = NotificationHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        ArrayList<Tickets> myListedTickets = dbHelper.getListings(userLoggedInUsername, null, null, false);
        ArrayList<Offer> tempOffersList = new ArrayList<>();


        // Post notifications for user's tickets that have been purchased
        // Post notifications for offers for user's tickets that user needs to respond to
        ArrayList<Offer> offersToRespondTo = new ArrayList<>();
        ArrayList<Offer> purchasedOffers = new ArrayList<>();

        for (Tickets ticket : myListedTickets) {
            // Gets all of offers on each of users tickets
            tempOffersList = dbHelper.getOffers(null, ticket.getId());
            // Separates offers based on pending or 0 (purchased)
            for (Offer offer : tempOffersList) {
                // Adds offers to list of offers in need of a response
                if (offer.getStatus().equals("PENDING")) {
                    offersToRespondTo.add(offer);
                    Log.d("Offer to Respond to: ", ticket.getBuyer()
                            + " -- " + offer.getId() + " -- " + offer.getBuyerUsername());
                }

                // Adds offers (purchased tickets) to purchased offers
                if (ticket.getAvailable().equals("0")) {
                    purchasedOffers.add(offer);
                    Log.d("Purchased ticket: ", ticket.getBuyer()
                            + " -- " + offer.getId() + " -- " + offer.getBuyerUsername());
                }
            }
        }

        // Creates notifications to enable user (seller) to respond to offers
        seller = dbHelper.getUser(userLoggedInUsername);
        for (Offer offer : offersToRespondTo) {
            buyer = dbHelper.getUser(offer.getBuyerUsername());
            String offerStatus = offer.getStatus();
            String offerAmount = offer.getOfferAmount();
            String offerId = offer.getId();

            Log.d("Offer", offer.getId() + ": Status = " + offerStatus);
            Log.d("Offer", "Seller: " + seller.getUsername() + " Buyer: " + buyer.getUsername());
            if (offerStatus.equals("PENDING")) { // TODO possibly redundant checking of status
                notificationHelper.setNotificationContent(
                        context,
                        buyer,
                        seller,
                        offerId,
                        offerAmount,
                        offerStatus,
                        context.getString(R.string.SELLER_ACCEPT_REJECT));
                notificationHelper.showNotification(context, -1);
            }
        }

        // Creates notifications alerting user that their ticket(s) has/have been purchased
        for (Offer offer : purchasedOffers) {
            buyer = dbHelper.getUser(offer.getBuyerUsername());
            String offerStatus = offer.getStatus();
            String offerAmount = offer.getOfferAmount();
            String offerId = offer.getId();

            Log.d("Purchased", "Seller: " + seller.getUsername() + " Buyer: " + buyer.getUsername());
            if (offerStatus.equals("0")) { // TODO possibly redundant checking of status
                notificationHelper.setNotificationContent(
                        context,
                        buyer,
                        seller,
                        offerId,
                        offerAmount,
                        offerStatus,
                        context.getString(R.string.SELLER_TICKET_PURCHASED));
                notificationHelper.showNotification(context, -1);
            }
        }


        // Post notifications for user offers that have gotten responses
        ArrayList<Offer> userOffers = dbHelper.getOffers(userLoggedInUsername, null);

        buyer = dbHelper.getUser(userLoggedInUsername);
        for (Offer offer : userOffers) {
            seller = dbHelper.getUser(dbHelper.getTicket(offer.getId()).getSeller());
            String offerStatus = offer.getStatus();
            String offerAmount = offer.getOfferAmount();
            String offerId = offer.getId();

            Log.d("Offer", offer.getId() + ": Status = " + offerStatus);
            Log.d("Offer", "Seller: " + seller.getUsername() + " Buyer: " + buyer.getUsername());
            if (offerStatus.equals("ACCEPTED") || offerStatus.equals("REJECTED")) {
                notificationHelper.setNotificationContent(
                        context,
                        buyer,
                        seller,
                        offerId,
                        offerAmount,
                        offerStatus,
                        context.getString(R.string.BUYER_OFFER_UPDATE));
                notificationHelper.showNotification(context, -1);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kwiktix_layout);

        // Gets Notifications corresponding to this user's ticket's sold, offers made on their tickets,
        // and the status of the offers that they made on others' tickets (accepted or rejected)
        SharedPreferences sharedPreferences = getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
        userLoggedInUsername = sharedPreferences.getString("username", "");
        Log.d("KwikTix User", userLoggedInUsername);
        postUserNotifications(userLoggedInUsername);


        // Specifies which fragment to go to
        Intent intent = getIntent();
        String fragmentToStart = null;
        try {
            fragmentToStart = intent.getStringExtra("fragment");
        } catch (Exception e) {

        }

        Listings listingsFragment = new Listings();

        // Create an Intent to hold the username data
        Bundle bundle = new Bundle();
        bundle.putString("username", userLoggedInUsername);

        // Set the data bundle to the fragment
        listingsFragment.setArguments(bundle);


        if (fragmentToStart != null) {
            if (fragmentToStart.equals("Profile")) {
                Fragment profileFragment = new Profile();
                profileFragment.setArguments(bundle);

                // Begin the fragment transaction
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, profileFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack("showing Listings")
                        .commit();
            }
        } else {
            // Begin the fragment transaction
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, listingsFragment)
                    .setReorderingAllowed(true)
                    .addToBackStack("showing Listings")
                    .commit();
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnItemSelectedListener(navListener);
    }

    private NavigationBarView.OnItemSelectedListener navListener =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (R.id.navigation_listings == item.getItemId()) {
                        selectedFragment = new Listings();
                    } else if (R.id.navigation_post == item.getItemId()) {
                        selectedFragment = new Post();
                    } else if (R.id.navigation_profile == item.getItemId()) {
                        selectedFragment = new Profile();
                    }

                    if (selectedFragment != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("username", userLoggedInUsername);
                        selectedFragment.setArguments(bundle);

                        fragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, selectedFragment)
                                .setReorderingAllowed(true)
                                .addToBackStack("showing Fragment")
                                .commit();
                    }

                    return true;
                }
            };


}
