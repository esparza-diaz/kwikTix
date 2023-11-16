package com.cs407.kwikTix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class KwikTix  extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();
    private String userLoggedIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kwiktix_layout);
        Intent intent = getIntent();
        userLoggedIn = intent.getStringExtra("username");

        Listings listingsFragment = new Listings();

        // Create an Intent to hold the username data
        Bundle bundle = new Bundle();
        bundle.putString("username", userLoggedIn);

        // Set the data bundle to the fragment
        listingsFragment.setArguments(bundle);

        // Begin the fragment transaction
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, listingsFragment)
                .setReorderingAllowed(true)
                .addToBackStack("showing Listings")
                .commit();
    }

    public void goToListings(MenuItem item) {
        Listings listingsFragment = new Listings();

        // Create an Intent to hold the username data
        Bundle bundle = new Bundle();
        bundle.putString("username", userLoggedIn);

        // Set the data bundle to the fragment
        listingsFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, listingsFragment)
                .setReorderingAllowed(true)
                .addToBackStack("showing Listings")
                .commit();
    }

    public void goToPost(MenuItem item) {
        Post postFragment = new Post();

        // Create an Intent to hold the username data
        Bundle bundle = new Bundle();
        bundle.putString("username", userLoggedIn);

        // Set the data bundle to the fragment
        postFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, postFragment)
                .setReorderingAllowed(true)
                .addToBackStack("showing Post")
                .commit();
    }

    public void goToProfile(MenuItem item) {
        Profile profileFragment = new Profile();

        // Create an Intent to hold the username data
        Bundle bundle = new Bundle();
        bundle.putString("username", userLoggedIn);

        // Set the data bundle to the fragment
        profileFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, profileFragment)
                .setReorderingAllowed(true)
                .addToBackStack("showing Profile")
                .commit();
    }

}
