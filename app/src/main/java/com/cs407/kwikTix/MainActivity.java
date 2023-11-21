package com.cs407.kwikTix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        // instantiate sql db
        startDb();

        // login logic
        SharedPreferences sharedPreferences = getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("username","") != ""){
            Intent intent = new Intent(MainActivity.this, KwikTix.class);
            String username = sharedPreferences.getString("username","");
            intent.putExtra("username", username);
            startActivity(intent);
        }else {
            setContentView(R.layout.activity_main);
        }
    }

    public void onSigninButtonClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.cs407.kwikTix", Context.MODE_PRIVATE);
        Intent intent = new Intent(MainActivity.this, KwikTix.class);
        EditText username = (EditText) findViewById(R.id.signinName);
        if (username.getText().toString().trim().isEmpty()){
            username.setError("Please enter a non-empty value");
        }else {
            intent.putExtra("username", username.getText().toString().trim());
            sharedPreferences.edit().putString("username", username.getText().toString().trim()).apply();
            startActivity(intent);
        }
    }

    public void onSignupButtonClick(View view) {
        // TODO Implement
        Log.d("Signup Button", "onSignupButtonClick: Clicked");
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Instantiates sql db and proper tables.
     */
    public void startDb() {
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("kwikTix", Context.MODE_PRIVATE,null);
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
        dbHelper.addUser("test","password","test@gmail.com", "696-969-6969", "E-Mail", "Wisconsin");
        // create example listing
        dbHelper.addTicket("ticket #1","1/20/23","10.99", "Wisconsin","test");
        //testing for UW- Madison. This successfully gets college name, latitude and longitude for camp randall
        Users user = dbHelper.getUser("test");
        //get college info from user
        Colleges college = dbHelper.getCollege(user.getCollege());
        // get listings for user test
        ArrayList<Tickets> tickets = dbHelper.getListings("test");
        Log.i("Testing user", user.getUsername() + " " + user.getPassword() + " " + user.getEmail() + " " + user.getPhone()
                + " " + user.getPrefContactMethod() + " " + user.getCollege());
        Log.i("Testing location",college.getCollege() + " " + college.getLatitude() + " " + college.getLongitude());
        for (Tickets ticket: tickets){
            Log.i("Testing listing",ticket.getTitle() + " " + ticket.getDate() + " " + ticket.getCollege() + " " + ticket.getUsername());
        }
        ArrayList<Tickets> allTix = dbHelper.getListings();
        for (Tickets ticket: allTix){
            Log.i("Testing listing",ticket.getTitle() + " " + ticket.getDate() + " " + ticket.getCollege() + " " + ticket.getUsername() + " " + ticket.getPrice());
        }
    }
}