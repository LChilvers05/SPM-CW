package com.example.spmapp.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spmapp.R;
import com.example.spmapp.ViewModels.DataCollectorViewModel;
import java.time.LocalDateTime;

public class ScreenCollectorActivity extends DataCollectorActivity {

    //views
    EditText lengthEditTxt;
    EditText dateEditTxt;
    TimePickerDialog lengthPickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screentime_collection);

        lengthEditTxt = findViewById(R.id.startTimeEntry);
        dateEditTxt = findViewById(R.id.startDateEntry);
        logButton = findViewById(R.id.logDataButton);
        timerDisplayTextView = findViewById(R.id.timerDisplayTextView);
        startTimerBtn = findViewById(R.id.startTimerButton);
        endTimerBtn = findViewById(R.id.endTimerButton);

        launchSessionLengthPicker();
        launchDatePicker();

        viewModel = new DataCollectorViewModel(getApplication(), this, false);
    }

    public void launchDatePicker(){
        //set listener for date selection
        dateEditTxt = findViewById(R.id.sessionDateEntry);
        dateEditTxt.setInputType(0);
        dateEditTxt.setOnClickListener(view -> {
            LocalDateTime localDateTime = LocalDateTime.now();
            new DatePickerDialog(this, (datePicker, yearSelected, monthSelected, daySelected) -> {
                String dateSelected = daySelected + "/" + monthSelected + "/" + yearSelected;
                dateEditTxt.setText(dateSelected);
                setLogButton(checkFields());
            }, localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth()).show();
        });
    }

    public void launchSessionLengthPicker(){
        lengthEditTxt = findViewById(R.id.startTimeEntry);
        lengthEditTxt.setInputType(0);

        lengthEditTxt.setOnClickListener(view -> {
            lengthPickerDialog = new TimePickerDialog(this, (startTimePicker, selectedHours, selectedMinutes) -> {
                String sessionLengthString;
                if(selectedMinutes<10){
                    sessionLengthString = selectedHours + ":0" + selectedMinutes;
                }else{
                    sessionLengthString = selectedHours + ":" + selectedMinutes;
                }
                lengthEditTxt.setText(sessionLengthString);
                setLogButton(checkFields());
            }, 0, 0, true);
            lengthPickerDialog.setTitle("Select session length");
            lengthPickerDialog.show();
        });
    }

    //stops user from calling logSleep until all EditTexts filled
    protected Boolean checkFields() {
        if (TextUtils.isEmpty(lengthEditTxt.getText().toString())) { return false; }
        return !(TextUtils.isEmpty(dateEditTxt.getText().toString()));
    }

    //view model does calculations to insert start and end time into DB
    public void logScreen(View view) {
        if (checkFields()) {
            viewModel.logScreen(dateEditTxt.getText().toString(),
                    lengthEditTxt.getText().toString());
            Toast.makeText(this, "Screen Time Recorded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
        }
        dateEditTxt.getText().clear();
        lengthEditTxt.getText().clear();
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