package com.example.spmapp.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
    LocalDateTime localDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_collection);
        //grab views
        startTimeEditTxt = findViewById(R.id.startTimeEntry);
        endTimeEditTxt = findViewById(R.id.endTimeEntry);
        dateEditTxt = findViewById(R.id.startDateEntry);
        logButton = findViewById(R.id.logDataButton);
        timerDisplayTextView = findViewById(R.id.timerDisplayTextView);
        startTimerBtn = findViewById(R.id.startTimerButton);
        endTimerBtn = findViewById(R.id.endTimerButton);

        localDateTime = LocalDateTime.now();

        viewModel = new DataCollectorViewModel(getApplication(), this, true);

        launchStartPicker();
        launchEndPicker();
        launchDatePicker();
    }

    private void launchDatePicker(){
        dateEditTxt = findViewById(R.id.startDateEntry);
        dateEditTxt.setInputType(0);
        //for some reason, month is off by one, +/- 1 fixes
        dateEditTxt.setOnClickListener(view -> new DatePickerDialog(this, (datePicker, yearSelected, monthSelected, daySelected) -> {
            String dateSelected = daySelected + "/" + (monthSelected + 1) + "/" + yearSelected;
            dateEditTxt.setText(dateSelected);
            setLogButton(checkFields());
        }, localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth()).show());
    }

    private void launchStartPicker(){
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
                setLogButton(checkFields());
            }, 0, 0, true);
            startTimePicker.show();
        });
    }

    private void launchEndPicker(){
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
                setLogButton(checkFields());
            }, localDateTime.getHour(), localDateTime.getMinute(), true);
            endTimePicker.show();
        });
    }

    //stops user from calling logSleep until all EditTexts filled
    protected Boolean checkFields() {
        if (TextUtils.isEmpty(endTimeEditTxt.getText().toString())) { return false; }
        if (TextUtils.isEmpty(startTimeEditTxt.getText().toString())) { return false; }
        return !(TextUtils.isEmpty(dateEditTxt.getText().toString()));
    }

    //view model does calculations to insert start and end time into DB
    public void logSleep(View view) {
        if (checkFields()) {
            viewModel.logSleep(dateEditTxt.getText().toString(),
                    startTimeEditTxt.getText().toString(),
                    endTimeEditTxt.getText().toString());
            Toast.makeText(this, "Sleep Recorded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
        }
        dateEditTxt.getText().clear();
        startTimeEditTxt.getText().clear();
        endTimeEditTxt.getText().clear();
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