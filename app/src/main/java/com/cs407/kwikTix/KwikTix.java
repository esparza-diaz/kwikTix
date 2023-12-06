package com.cs407.kwikTix;

import android.app.NotificationManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kwiktix_layout);

        // Gets Notifications corresponding to this user's ticket's sold, offers made on their tickets,
        // and the status of the offers that they made on others' tickets (accepted or rejected)
        SharedPreferences sharedPreferences = getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);

        userLoggedInUsername = sharedPreferences.getString("username", "");
        Log.d("KwikTix User", userLoggedInUsername);


        String wereNotificationsPosted = sharedPreferences.getString("wereNotificationsPosted: " + userLoggedInUsername, "");
        Log.d("posted Notes", wereNotificationsPosted);
        if (wereNotificationsPosted.equals("")) {
            postUserNotifications(userLoggedInUsername); // TODO test
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("wereNotificationsPosted: " + userLoggedInUsername, "yes");
            editor.commit();
        }
        wereNotificationsPosted = sharedPreferences.getString("wereNotificationsPosted: " + userLoggedInUsername, "");
        Log.d("posted Notes", wereNotificationsPosted);

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


    private void postUserNotifications(String userLoggedInUsername) {
        Users seller;
        Users buyer;
        Context context = getApplicationContext();
        NotificationHelper notificationHelper = NotificationHelper.getInstance();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        ArrayList<Tickets> myListedTickets = dbHelper.getListings(userLoggedInUsername, null, null, false);
        ArrayList<Offer> tempOffersList;


        // Only gets notifications for listings if user has actually listed tickets
        if (myListedTickets.size() != 0) {
            Log.d("User Listings Notification", "User has posted tickets");

            ArrayList<Offer> offersToRespondTo = new ArrayList<>();
            ArrayList<Tickets> ticketsWithPendingOffers = new ArrayList<>();
            ArrayList<Tickets> purchasedTickets = new ArrayList<>();
            ArrayList<Offer> purchasedOffers = new ArrayList<>();

            for (Tickets ticket : myListedTickets) {
                Log.d("Ticket in myListedTickets", ticket.getTitle());
                // Gets all of offers on each of users tickets
                tempOffersList = dbHelper.getOffers(null, ticket.getId());

                // Separates offers based on pending or 0 (purchased)
                // Post notifications for offers for user's tickets that user needs to respond to
                for (Offer offer : tempOffersList) {
                    // Adds offers to list of offers in need of a response
                    if (offer.getStatus().equals("PENDING")) {
                        offersToRespondTo.add(offer);
                        ticketsWithPendingOffers.add(ticket);
                        Log.d("Offer to Respond to: ", ticket.getBuyer()
                                + " -- " + ticket.getTitle() + " -- " + offer.getBuyerUsername());
                    }
                    // Post notifications for user's tickets that have been purchased
                    // Adds offers (purchased tickets) to purchased offers
                    if (ticket.getAvailable().equals("0")) {
                        purchasedOffers.add(offer);
                        purchasedTickets.add(ticket);
                        Log.d("Purchased ticket: ", ticket.getBuyer()
                                + " -- " + ticket.getTitle() + " -- " + offer.getBuyerUsername());
                    }
                }
            }

            // Creates notifications to enable user (seller) to respond to offers
            seller = dbHelper.getUser(userLoggedInUsername);
            Offer pendingOffer;
            Tickets ticket;
            for (int i = 0; i < offersToRespondTo.size(); i++) {
                pendingOffer = offersToRespondTo.get(i);
                ticket = ticketsWithPendingOffers.get(i);
                buyer = dbHelper.getUser(pendingOffer.getBuyerUsername());
                String offerStatus = pendingOffer.getStatus();
                String offerAmount = pendingOffer.getOfferAmount();
                String offerId = pendingOffer.getId();
                String ticketTitle = ticket.getTitle();
                String listingId = ticket.getId(); // TODO make sure listingId is ticketId

                Log.d("Offer", offerId + ": Status = " + offerStatus); // TODO THIS WORKS!!
                Log.d("Offer Ticket", "Seller: " + seller.getUsername() + " Buyer: " + buyer.getUsername()
                        + "Ticket Title: " + ticket);
                if (offerStatus.equals("PENDING")) { // TODO possibly redundant checking of status
                    notificationHelper.setNotificationContent(
                            context,
                            buyer,
                            seller,
                            ticketTitle,
                            offerAmount,
                            offerStatus,
                            context.getString(R.string.SELLER_ACCEPT_REJECT),
                            offerId,
                            listingId);
                    notificationHelper.showNotification(context, -1);// TODO THIS WORKS!!
                }
            }

            // Creates notifications alerting user that their ticket(s) has/have been purchased
            Offer purchasedOffer;
            Tickets purchasedTicket;
            for (int i = 0; i < purchasedOffers.size(); i++) {
                purchasedOffer = purchasedOffers.get(i);
                purchasedTicket = purchasedTickets.get(i);
                buyer = dbHelper.getUser(purchasedOffer.getBuyerUsername());
                String offerStatus = purchasedOffer.getStatus();
                String offerAmount = purchasedOffer.getOfferAmount();
                String offerId = purchasedOffer.getId();
                String ticketTitle = purchasedTicket.getTitle();
                String listingId = purchasedTicket.getId();

                Log.d("Purchased Offer: " + offerId, "Seller: " + seller.getUsername() + " Buyer: " + buyer.getUsername()
                        + " Ticket Title: " + ticketTitle);
                if (offerStatus.equals("0")) { // TODO possibly redundant checking of status
                    notificationHelper.setNotificationContent(
                            context,
                            buyer,
                            seller,
                            ticketTitle,
                            offerAmount,
                            offerStatus,
                            context.getString(R.string.SELLER_TICKET_PURCHASED),
                            offerId,
                            listingId);
                    notificationHelper.showNotification(context, -1);
                }
            }
        } else {
            Log.d("User Listings Notification", "No listings from user");
        }

        // Post notifications for user offers that have gotten responses
        ArrayList<Offer> userOffers = dbHelper.getOffers(userLoggedInUsername, null);

        if (userOffers.size() != 0) {
            Log.d("User Offers", "User Offers made offers");
            buyer = dbHelper.getUser(userLoggedInUsername);
            for (Offer offer : userOffers) {
                seller = dbHelper.getUser(dbHelper.getTicket(offer.getId()).getSeller());
                String offerStatus = offer.getStatus();
                String offerAmount = offer.getOfferAmount();
                String offerId = offer.getId();
                String ticketTitle = dbHelper.getTicket(offer.getId()).getTitle();

                Log.d("Offer", offer.getId() + ": Status = " + offerStatus);
                Log.d("Offer", "Seller: " + seller.getUsername() + " Buyer: " + buyer.getUsername());
                if (offerStatus.equals("ACCEPTED") || offerStatus.equals("REJECTED")) {
                    notificationHelper.setNotificationContent(
                            context,
                            buyer,
                            seller,
                            ticketTitle,
                            offerAmount,
                            offerStatus,
                            context.getString(R.string.BUYER_OFFER_UPDATE),
                            offerId,
                            "");
                    notificationHelper.showNotification(context, -1);
                }
            }
        } else {
            Log.d("User Offers", "User has not made offers");
        }
    }
}
