package com.cs407.kwikTix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageSettings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DBHelper dbHelper;

    // TODO might need different context
    SQLiteDatabase sqLiteDatabase;
    String username;
    String email;
    EditText emailInput;
    String phone;
    EditText phoneInput;
    String prefContactMethod;
    Spinner prefContactMethodSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_settings);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Log.d("Username String Extra", username);

        sqLiteDatabase = getApplicationContext().openOrCreateDatabase(getResources().getString(R.string.sql_db), Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        Users user = dbHelper.getUser(username);
        if (user == null) {
            Log.d("USER NULL", "USER NULL");
        } else {
            Log.d("DB and User", dbHelper.toString() + ", " + user.getUsername());
        }

        emailInput = ((EditText) findViewById(R.id.editEmail));
        // Auto-populates user info
        emailInput.setText(user.getEmail());

        phoneInput = ((EditText) findViewById(R.id.editPhone));
        phoneInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher("US"));
        phoneInput.setText(user.getPhone());

        prefContactMethodSpinner = (Spinner) findViewById(R.id.editContactMethodDropdown);

        // Setting adapters for spinners
        ArrayAdapter<CharSequence> contactMethodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.contact_methods_array,
                android.R.layout.simple_list_item_1
        );

        // TODO Unnecessary?
        contactMethodAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // Setting spinner methods to control selection functionality
        prefContactMethodSpinner.setAdapter(contactMethodAdapter);
        prefContactMethodSpinner.setOnItemSelectedListener(this);
    }

    private boolean emailInputValidation(String email) {
        // Checks if email is null or empty
        if (email == null || email.equals("Email") || email.equals("")) {
            return false;
        }

        if (email.contains(" ")) {
            return false;
        }

        // Regular expression to verify email
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$");
        Matcher matcher = pattern.matcher(email);
        boolean validEmail = matcher.find();

        if (!validEmail) {
            return false;
        } else {
            Log.d("UserValidation", "valid email");
        }

        return true;
    }

    private boolean phoneInputValidation(String phone) {
        // Checks if empty
        if (phone == null || phone.equals("Phone") || phone.equals("")) {
            return false;
        }

        // Regular expression to verify phone
        Pattern pattern = Pattern.compile("^(1\\s)?\\(\\d{3}\\)\\s(\\d{3})-(\\d{4})$");
        Matcher matcher = pattern.matcher(phone);
        boolean validPhone = matcher.find();

        if (!validPhone) {
            Log.d("UserValidation", "Phone regex didn't match");
            return false;
        } else {
            Log.d("UserValidation", "valid phone");
        }

        return true;
    }

    private boolean prefContactMethodInputValidation(String prefContactMethod) {
        // Checks if prefContactMethod is the non-default value (a selection was made)
        if (prefContactMethod == null || prefContactMethod.equals("Preferred Contact Method")) {
            Toast.makeText(this, "Please Select Preferred Contact Method!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void onSaveButtonClick(View view) {
        email = emailInput.getText().toString();

        // Strip away everything but 10 digits from phone number // 1 (012) 345-6789
        phone = phoneInput.getText().toString();

        if (!emailInputValidation(email)) {
            Log.d("UserValidation", "Email Error");
            emailInput.setError("Invalid Email!");
            return;
        }
        if (!phoneInputValidation(phone)) {
            Log.d("UserValidation", "Phone: Error");
            phoneInput.setError("Invalid Phone Number!");
            return;
        } else {
            phone = phone.substring(phone.length()-13).replace(" ", "").replace(")", "");
            phone = phone.replace("-", "");
            Log.d("UserValidation", "Update phone: " + phone);
        }
        if (!prefContactMethodInputValidation(prefContactMethod)) {
            Log.d("UserValidation", "PCM Error");
            return;
        }

        // Puts updated user info into DB
        dbHelper.setUser(username, email, phone, prefContactMethod);
        Toast.makeText(this, "Information Updated", Toast.LENGTH_LONG).show();
    }

    public void onExitButtonClick(View view) {
        Intent intent = new Intent(getApplicationContext(), KwikTix.class);
        intent.putExtra("username", username);
        intent.putExtra("fragment", "Profile");
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (parent == prefContactMethodSpinner) {
            prefContactMethod = parent.getItemAtPosition(pos).toString();
            Log.i("PREF CONTACT - Selected", prefContactMethod);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}