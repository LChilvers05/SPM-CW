package com.example.spmapp.Activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spmapp.R;
import com.example.spmapp.ViewModels.DataCollectorViewModel;

public class SleepCollectorActivity extends DataCollectorActivity {

    //views
    EditText startTimeEditTxt;
    EditText endTimeEditTxt;
    //time Pickers
    TimePickerDialog startTimePicker;
    TimePickerDialog endTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_collection);
        //grab views
        timerDisplayTextView = findViewById(R.id.timerDisplayTextView);
        startTimeEditTxt = findViewById(R.id.startTimeEntry);
        startTimeEditTxt.setInputType(0);
        endTimeEditTxt = findViewById(R.id.endTimeEntry);
        endTimeEditTxt.setInputType(0);
        startTimerBtn = findViewById(R.id.startTimerButton);
        endTimerBtn = findViewById(R.id.endTimerButton);

        viewModel = new DataCollectorViewModel(getApplication(), this, true);

        //start time picker dialog and add selection to EditText view
        startTimeEditTxt.setOnClickListener(view -> {
            startTimePicker = new TimePickerDialog(SleepCollectorActivity.this, (startTimePicker, selectedHours, selectedMinutes) -> {
                String startTimeString;
                if(selectedMinutes<10){
                    startTimeString = selectedHours + ":0" + selectedMinutes;
                }else{
                    startTimeString = selectedHours + ":" + selectedMinutes;
                }
                startTimeEditTxt.setText(startTimeString);
            }, 0, 0, true);
            startTimePicker.show();
        });
        //start time picker dialog and add selection to EditText view
        endTimeEditTxt.setOnClickListener(view -> {
            endTimePicker = new TimePickerDialog(SleepCollectorActivity.this, (endTimePicker, selectedHours, selectedMinutes) -> {
                String endTimeString;
                if(selectedMinutes<10){
                    endTimeString = selectedHours + ":0" + selectedMinutes;
                }else{
                    endTimeString = selectedHours + ":" + selectedMinutes;
                }

                endTimeEditTxt.setText(endTimeString);
            }, 0, 0, true);
            endTimePicker.show();
        });
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
        viewModel.endTimer(true);
        Toast.makeText(this, "Sleep Recorded", Toast.LENGTH_SHORT).show();
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