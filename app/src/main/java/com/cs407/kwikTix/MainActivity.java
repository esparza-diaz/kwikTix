package com.cs407.kwikTix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> colleges; // stores college names
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // instantiate sql db
        startDb();
    }

    /**
     * Instantiates sql db and proper tables.
     */
    public void startDb() {
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("kwik", Context.MODE_PRIVATE,null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        colleges = new ArrayList<>();
        // creates colleges db from college.csv
        try {
            InputStream inputStream = getAssets().open("colleges.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);

            String line;

            // Read and process each line
            //br.readLine();
            while ((line = br.readLine()) != null) {
                // Split the line into fields using a comma as the delimiter
                String[] fields = line.split(",");

                // Process each field
                String team = fields[3].trim();
                String latitude = fields[9].trim();
                String longitude = fields[10].trim();

                // store names to arrayList for college names
                colleges.add(team);

                //add to db to be used for mapping
                dbHelper.addCollege(team,latitude,longitude);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //create user test
        dbHelper.addUser("test","password","test@gmail.com", "Wisconsin");
        // create example listing
        dbHelper.addTicket("ticket #1","1/20/23", "Wisconsin","test");
        //testing for UW- Madison. This successfully gets college name, latitude and longitude for camp randall
        Users user = dbHelper.getUser("test");
        //get college info from user
        Colleges college = dbHelper.getCollege(user.getCollege());
        // get listings for user test
        ArrayList<Tickets> tickets = dbHelper.getListings("test");
        Log.i("Testing user", user.getUsername() + " " + user.getPassword() + " " + user.getEmail() + " " + user.getCollege());
        Log.i("Testing location",college.getCollege() + " " + college.getLatitude() + " " + college.getLongitude());
        for (Tickets ticket: tickets){
            Log.i("Testing listing",ticket.getTitle() + " " + ticket.getDate() + " " + ticket.getCollege() + " " + ticket.getUsername());
        }

    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, KwikTix.class);
        startActivity(intent);
    }

    public void onLoginButtonClick(View view) {
        goToMainActivity();
    }
}