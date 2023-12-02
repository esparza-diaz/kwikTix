package com.cs407.kwikTix;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public void onBackToLoginButtonClick(View view) {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private boolean phoneInputValidation(String phone) {
        // Checks if empty
        if (phone == null || phone == "") {
            Toast.makeText(this, "Please Fill Each Entry", Toast.LENGTH_LONG).show();
            return false;
        }

        // Checks if input is number
        try {
            Integer.parseInt(phone);
        } catch (NumberFormatException e) {
            Log.d("phoneInputValidation", "Can't parse phone input as int");
            return false;
        }

        // Checks if input has length 10
        if (phone.length() != 10) {
            return false;
        }
        return true;
    }

    private boolean usernameInputValidation(String username, DBHelper dbhelper) {
        // Checks if username is empty or null
        if (username == null || username == "") {
            Toast.makeText(this, "Please Fill Each Entry", Toast.LENGTH_LONG).show();
        }

        // Checks if username is already taken
        Users user = dbhelper.getUser(username);
        if (user != null) {
            Log.d("UserValidation", user.toString());
            Toast.makeText(this, "Username Taken: Please Choose Another!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            Log.d("UserValidation", "User is null (NOT TAKEN)");
        }

        return true;
    }

    private boolean emailInputValidation(String email) {
        if (email.contains(" ")) {
            Toast.makeText(this, "Email Cannot Contain Spaces!", Toast.LENGTH_LONG);
            return false;
        }

        // Regular expression to verify email
        Pattern pattern = Pattern.compile("(\\w@((cs\\.)?|(cae\\.)?)wisc\\.edu)$");
        Matcher matcher = pattern.matcher(email);
        boolean validUWSuffix = matcher.find();

        if (!validUWSuffix) {
            Toast.makeText(this, "Not a valid UW email!", Toast.LENGTH_LONG);
            return false;
        } else {
            Log.d("UserValidation", "valid uw email");
        }

        return true;
    }

    private boolean passwordInputValidation(String password) {
        // Checks if password is null or empty
        if (password == null || password == "") {
            Toast.makeText(this, "Please Fill Each Entry", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean prefContactMethodInputValidation(String prefContactMethod) {
        // Checks if prefContactMethod is the non-default value (a selection was made)
        if (prefContactMethod == null || prefContactMethod.equals("Preferred Contact Method")) {
            Toast.makeText(this, "Please Fill Each Entry", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean collegeInputValidation(String college) {
        // Checks if college is the non-default value (a selection was made)
        if (college == null || college.equals("Select College")) {
            Toast.makeText(this, "Please Fill Each Entry", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public void onSignupPageButtonClick(View view) {
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE,null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        username = ((EditText) findViewById(R.id.signupName)).getText().toString();
        password = ((EditText) findViewById(R.id.signupPassword)).getText().toString();
        email = ((EditText) findViewById(R.id.signupEmail)).getText().toString();
        phone = ((EditText) findViewById(R.id.signupPhone)).getText().toString();

        if (!usernameInputValidation(username, dbHelper)){
            Log.d("UserValidation", "Username Error");
            return;
        };
        if (!passwordInputValidation(password)) {
            Log.d("UserValidation", "Password Error");
            return;
        }
        if (!emailInputValidation(email)) {
            Log.d("UserValidation", "Email Error");
            return;
        }
        if (!phoneInputValidation(phone)) {
            Log.d("UserValidation", "Phone: Error");
            return;
        }
        if (prefContactMethodInputValidation(prefContactMethod)) {
            Log.d("UserValidation", "PCM Error");
            return;
        }
        if (!collegeInputValidation(college)) {
            Log.d("UserValidation", "College Error");
            return;
        }

        // Checks if user input is valid
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
                Toast.makeText(this, "Error Adding User!", Toast.LENGTH_LONG).show();
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