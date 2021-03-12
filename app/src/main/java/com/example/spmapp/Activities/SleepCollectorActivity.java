package com.example.spmapp.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.example.spmapp.R;
import com.example.spmapp.ViewModels.DataCollectorViewModel;
import java.time.LocalDateTime;


public class SleepCollectorActivity extends DataCollectorActivity {

    //views
    EditText startTimeEditTxt;
    EditText endTimeEditTxt;
    EditText dateEditTxt;

    //Pickers
    TimePickerDialog startTimePicker;
    TimePickerDialog endTimePicker;
    DatePickerDialog datePickerDialog;
    LocalDateTime localDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_collection);
        //grab views
        timerDisplayTextView = findViewById(R.id.timerDisplayTextView);

        localDateTime = LocalDateTime.now();

        startTimerBtn = findViewById(R.id.startTimerButton);
        endTimerBtn = findViewById(R.id.endTimerButton);

        viewModel = new DataCollectorViewModel(getApplication(), this, true);

        launchStartPicker();
        launchEndPicker();
        launchDatePicker();
    }

    public void launchDatePicker(){
        dateEditTxt = findViewById(R.id.startDateEntry);
        dateEditTxt.setInputType(0);

        dateEditTxt.setOnClickListener(view -> {
            new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){

                @Override
                public void onDateSet(DatePicker datePicker, int yearSelected, int monthSelected, int daySelected) {
                    String dateSelected = daySelected + "/" + monthSelected + "/" + yearSelected;
                    dateEditTxt.setText(dateSelected);
                }
            }, localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth()).show();
        });
    }

    public void launchStartPicker(){
        //start time picker dialog and add selection to EditText view
        startTimeEditTxt = findViewById(R.id.startTimeEntry);
        startTimeEditTxt.setInputType(0);

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
    }

    public void launchEndPicker(){
        //end time picker dialog and add selection to EditText view
        endTimeEditTxt = findViewById(R.id.endTimeEntry);
        endTimeEditTxt.setInputType(0);

        endTimeEditTxt.setOnClickListener(view -> {
            endTimePicker = new TimePickerDialog(SleepCollectorActivity.this, (endTimePicker, selectedHours, selectedMinutes) -> {
                String endTimeString;
                if(selectedMinutes<10){
                    endTimeString = selectedHours + ":0" + selectedMinutes;
                }else{
                    endTimeString = selectedHours + ":" + selectedMinutes;
                }

                endTimeEditTxt.setText(endTimeString);
            }, localDateTime.getHour(), localDateTime.getMinute(), true);
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