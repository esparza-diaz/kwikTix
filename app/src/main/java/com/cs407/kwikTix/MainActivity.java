package com.cs407.kwikTix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, KwikTix.class);
        startActivity(intent);
    }

    public void onLoginButtonClick(View view) {
        goToMainActivity();
    }
}