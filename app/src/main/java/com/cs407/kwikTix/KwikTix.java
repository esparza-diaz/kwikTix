package com.cs407.kwikTix;

import android.content.Intent;
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
                        bundle.putString("username", userLoggedIn);
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
