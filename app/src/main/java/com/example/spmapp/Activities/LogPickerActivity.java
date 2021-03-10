package com.example.spmapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.example.spmapp.R;

public class LogPickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_picker);

    }

    public void logSleepTapped(View view) {
        Intent dataCollection = new Intent(this, DataCollectorActivity.class);
        startActivity(dataCollection);
    }

    public void logScreenTimeTapped(View view) {
        Intent dataCollection = new Intent(this, DataCollectorActivity.class);
        startActivity(dataCollection);
    }
}