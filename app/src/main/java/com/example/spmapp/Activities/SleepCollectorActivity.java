package com.example.spmapp.Activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.R;
import com.example.spmapp.ViewModels.DataCollectorViewModel;

public class SleepCollectorActivity extends AppCompatActivity {

    DataCollectorViewModel viewModel;
    //views
    TextView timerValueTextView;
    EditText startTimeEditTxt;
    EditText endTimeEditTxt;
    ImageButton startTimerBtn;
    ImageButton endTimerBtn;
    //time Pickers
    TimePickerDialog startTimePicker;
    TimePickerDialog endTimePicker;
    //timer variables
    private final Handler timerHandler = new Handler();
    Long timerStart = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_collection);
        //grab views
        timerValueTextView = findViewById(R.id.timerValueTextView);
        startTimeEditTxt = findViewById(R.id.startTimeEntry);
        startTimeEditTxt.setInputType(0);
        endTimeEditTxt = findViewById(R.id.endTimeEntry);
        endTimeEditTxt.setInputType(0);
        startTimerBtn = findViewById(R.id.startTimerButton);
        endTimerBtn = findViewById(R.id.endTimerButton);

        viewModel = new DataCollectorViewModel(getApplication(), this);

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

    @Override
    protected void onStart() {
        super.onStart();
        //only allow start button to be pressed
        startTimerBtn.setEnabled(true);
        endTimerBtn.setEnabled(false);
        timerStart = viewModel.getTimerStart();
        //timer already started
        if (timerStart != 0L) {
            runTimer();
            //only allow stop button to be pressed
            startTimerBtn.setEnabled(false);
            endTimerBtn.setEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop the timer when leaving activity
        timerHandler.removeCallbacks(UpdateTimerTask);
        timerStart = 0L;
    }

    public void BackButtonPressed(View view){
        finish();
    }

    public void startTimer(View view) {
        //record unix timestamp when timer began
        viewModel.startTimer();

        view.setEnabled(false);
        endTimerBtn.setEnabled(true);

        runTimer();
    }

    public void endTimer(View view) {
        //save the sleep session to DB
        viewModel.endTimer(true);
        timerHandler.removeCallbacks(UpdateTimerTask);
        view.setEnabled(false);
        startTimerBtn.setEnabled(true);
    }

    private void runTimer() {
        timerStart = viewModel.getTimerStart();
        timerHandler.removeCallbacks(UpdateTimerTask);
        timerHandler.postDelayed(UpdateTimerTask, 100);
    }

    private final Runnable UpdateTimerTask = new Runnable() {
        //recursive loop to update timer display
        public void run() {
            final long start = timerStart;
            int seconds = (int)(General.getUnixTime() - start);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds     = seconds % 60;
            minutes = minutes % 60;

            //TODO: Display hours
            if (seconds < 10) {
                timerValueTextView.setText("" + minutes + ":0" + seconds);
            } else {
                timerValueTextView.setText("" + minutes + ":" + seconds);
            }
            timerHandler.postDelayed(this, 100);
        }
    };
}