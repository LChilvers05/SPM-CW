package com.example.spmapp.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spmapp.R;
import com.example.spmapp.ViewModels.DataCollectorViewModel;

public class DataCollectorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
    }

    public void BackButtonPressed(View view){
        finish();
    }
}
