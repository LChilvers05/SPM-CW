package com.example.spmapp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.spmapp.R;
import com.example.spmapp.ViewModels.DataCollectorViewModel;

public class ScreenCollectorActivity extends DataCollectorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screentime_collection);

        timerDisplayTextView = findViewById(R.id.timerDisplayTextView);
        startTimerBtn = findViewById(R.id.startTimerButton);
        endTimerBtn = findViewById(R.id.endTimerButton);

        viewModel = new DataCollectorViewModel(getApplication(), this, false);
    }

    //timer start button
    public void startTimer(View view) {
        //record unix timestamp when timer began
        viewModel.startTimer();
        runTimer();

        swapButtons();
    }

    //timer stop button
    public void endTimer(View view) {
        //save the sleep session to DB
        viewModel.endTimer(false);
        Toast.makeText(this, "Screen Time Recorded", Toast.LENGTH_SHORT).show();
        //stop timer thread
        timerHandler.removeCallbacks(UpdateTimerTask);
        String timeDisplay = "00:00:00";
        timerDisplayTextView.setText(timeDisplay);

        swapButtons();
    }

    public void BackButtonPressed(View view){
        finish();
    }
}