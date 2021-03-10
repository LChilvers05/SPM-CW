package com.example.spmapp.Activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spmapp.Helpers.General;
import com.example.spmapp.R;
import com.example.spmapp.ViewModels.DataCollectorViewModel;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.Time;
import java.util.Calendar;

public class SleepCollectorActivity extends AppCompatActivity {

    DataCollectorViewModel viewModel;
    Timer timer;
    TimerHandler timerHandler = new TimerHandler(this);
    Boolean timerRunning = false;
    Double timerValue = 0.0;

    TextView timerValueTextView;





        EditText startTimeEditTxt;
        EditText endTimeEditTxt;

        TimePickerDialog startTimePicker;
        TimePickerDialog endTimePicker;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_collection);

        startTimeEditTxt = (EditText)findViewById(R.id.startTimeEntry);
        startTimeEditTxt.setInputType(0);
        endTimeEditTxt = (EditText)findViewById(R.id.endTimeEntry);
        endTimeEditTxt.setInputType(0);

        startTimeEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimePicker = new TimePickerDialog(com.example.spmapp.Activities.SleepCollectorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker startTimePicker, int selectedHours, int selectedMinutes) {
                        String startTimeString;
                        if(selectedMinutes<10){
                            startTimeString = selectedHours + ":0" + selectedMinutes;
                        }else{
                            startTimeString = selectedHours + ":" + selectedMinutes;
                        }
                        startTimeEditTxt.setText(startTimeString);
                    }
                }, 0, 0, true);
                startTimePicker.show();
            }

        });

        endTimeEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimePicker = new TimePickerDialog(com.example.spmapp.Activities.SleepCollectorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker endTimePicker, int selectedHours, int selectedMinutes) {
                        String endTimeString;
                        if(selectedMinutes<10){
                            endTimeString = selectedHours + ":0" + selectedMinutes;
                        }else{
                            endTimeString = selectedHours + ":" + selectedMinutes;
                        }

                        endTimeEditTxt.setText(endTimeString);
                    }
                }, 0, 0, true);
                endTimePicker.show();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        viewModel = new DataCollectorViewModel(getApplication(), this);
        long timerStart = viewModel.getTimerStart();
        if (timerStart != 0) {
            timerRunning = true;
            timerValue = (double)(General.getUnixTime() - timerStart);
            runTimer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timerRunning = false;
        timerValue = 0.0;
    }

    public void timerStarted(View view) {
        viewModel.startTimer();
        view.setEnabled(false);
        //stopButton.setEnabled(true);
        runTimer();
    }

    public void endTimer(View view) {
        viewModel.endTimer(true);
        view.setEnabled(false);
        //startButton.setEnabled(true);
    }

    private void runTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println("Hello there");
                timerValue += 0.01; //increase every sec
                timerHandler.obtainMessage(1).sendToTarget();
            }
        }, 0, 10);
    }

    private static class TimerHandler extends Handler {

        private final WeakReference<SleepCollectorActivity> weakRefActivity;

        public TimerHandler(SleepCollectorActivity activity) {
            this.weakRefActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SleepCollectorActivity activity = weakRefActivity.get();
            if (activity != null) {
                activity.timerValueTextView.setText(activity.timerValue.toString());
            }
        }
    }
}