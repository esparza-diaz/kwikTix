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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create colleges table from college.csv onCreate
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("kwik", Context.MODE_PRIVATE,null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
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

                //add to db
                dbHelper.addCollege(team,latitude,longitude);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //testing for UW- Madison. This successfully gets college name, latitude and longitude for camp randall
        ArrayList<Colleges> colleges = dbHelper.getColleges("Wisconsin");
        for (Colleges college : colleges){
            Log.i("Testing",college.getCollege() + " " + college.getLatitude() + " " + college.getLongitude());
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