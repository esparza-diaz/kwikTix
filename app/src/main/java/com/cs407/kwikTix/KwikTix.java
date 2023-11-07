package com.cs407.kwikTix;

import android.content.Intent;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kwiktix_layout);

    }

    public void goToListings(MenuItem item) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, Listings.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("showing Listings")
                .commit();
    }

    public void goToPost(MenuItem item) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, Post.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("showing Post")
                .commit();
    }

    public void goToProfile(MenuItem item) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, Profile.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("showing Profile")
                .commit();
    }

}
