package com.paddy.chatheads;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SettingsFragment settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
    }
}
