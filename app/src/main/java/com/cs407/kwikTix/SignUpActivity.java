package com.cs407.kwikTix;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String username;
    String password;
    String email;
    String phone;
    String prefContactMethod;
    String college;
    ArrayList<String> colleges;
    Spinner prefContactMethodSpinner;
    Spinner collegesSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        Intent intent = getIntent();
        colleges = intent.getStringArrayListExtra("collegesArrayList");
        colleges.add(0,"Select College");

        prefContactMethodSpinner = (Spinner) findViewById(R.id.signupContactMethodDropdown);
        collegesSpinner = (Spinner) findViewById(R.id.signupCollege);

        // Setting adapters for spinners
        ArrayAdapter<String> collegesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                colleges);
        ArrayAdapter<CharSequence> contactMethodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.contact_methods_array,
                android.R.layout.simple_list_item_1
        );

        // TODO Unnecessary?
        contactMethodAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        collegesAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // Setting spinner methods to control selection functionality
        prefContactMethodSpinner.setAdapter(contactMethodAdapter);
        prefContactMethodSpinner.setOnItemSelectedListener(this);

        collegesSpinner.setAdapter(collegesAdapter);
        collegesSpinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (parent == prefContactMethodSpinner) {
            prefContactMethod = parent.getItemAtPosition(pos).toString();
            Log.i("PREF CONTACT - Selected", prefContactMethod);
        }
        if (parent == collegesSpinner) {
            college = parent.getItemAtPosition(pos).toString();
            Log.i("PREF CONTACT - Selected", college);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        prefContactMethod = parent.getItemAtPosition(0).toString();
        Log.i("PREF CONTACT - Nothing Selected", prefContactMethod);
    }

    public void onSignupPageButtonClick(View view) {
        username = ((EditText) findViewById(R.id.signupName)).getText().toString();
        password = ((EditText) findViewById(R.id.signupPassword)).getText().toString();
        email = ((EditText) findViewById(R.id.signupEmail)).getText().toString();
        phone = ((EditText) findViewById(R.id.signupPhone)).getText().toString();

        // Checks if user input is valid
        if (username == null || username == "") {
            Toast.makeText(this, "Please Fill Each Entry", Toast.LENGTH_LONG).show();
        } else if (password == null || password == "") {
            Toast.makeText(this, "Please Fill Each Entry", Toast.LENGTH_LONG).show();
        } else if (email == null || email == "") {
            Toast.makeText(this, "Please Fill Each Entry", Toast.LENGTH_LONG).show();
        } else if (phone == null || phone == "") {
            Toast.makeText(this, "Please Fill Each Entry", Toast.LENGTH_LONG).show();
        } else if (prefContactMethod == null || prefContactMethod.equals("Preferred Contact Method")) {
            Toast.makeText(this, "Please Fill Each Entry", Toast.LENGTH_LONG).show();
        } else if (college == null || college.equals("Select College")) {
            Toast.makeText(this, "Please Fill Each Entry", Toast.LENGTH_LONG).show();
        } else {
            SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE,null);
            DBHelper dbHelper = new DBHelper(sqLiteDatabase);

            try {
                // Adds new user to SQLiteDatabase
                boolean userAdded = dbHelper.addUser(
                        username,
                        password,
                        email,
                        phone,
                        prefContactMethod,
                        college
                );

                if (!userAdded) {
                    Toast.makeText(this, "Username Taken: Please Choose Another!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Successfully Added: " + username + " ! Sign In", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error Message", Toast.LENGTH_LONG).show();
            }
        }
    }


}