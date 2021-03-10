package com.example.spmapp.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spmapp.Helpers.General;
import com.example.spmapp.R;
import com.example.spmapp.ViewModels.DataCollectorViewModel;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class SleepCollectorActivity extends AppCompatActivity {

    DataCollectorViewModel viewModel;
    Timer timer;
    TimerHandler timerHandler = new TimerHandler(this);
    Boolean timerRunning = false;
    Double timerValue = 0.0;

    TextView timerValueTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_collection);
        timerValueTextView = findViewById(R.id.timerValueTextView);
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